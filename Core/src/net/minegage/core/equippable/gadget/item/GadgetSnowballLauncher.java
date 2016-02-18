package net.minegage.core.equippable.gadget.item;


import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.google.common.collect.Sets;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilParticle;
import net.minegage.common.util.UtilPos;
import net.minegage.common.util.UtilSound;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.gadget.Gadget;
import net.minegage.core.rank.Rank;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class GadgetSnowballLauncher
		extends GadgetItem {

	private final int NUM_SNOWBALLS = 5;
	private Set<Material> ignore = Sets.newHashSet();

	public GadgetSnowballLauncher(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank) {
		super(manager, name, item, displaySlot, rank);

		i(Material.SNOW_BLOCK);
		i(Material.SIGN_POST);
		i(Material.WALL_SIGN);
	}

	private void i(Material type) {
		ignore.add(type);
	}

	@Override
	public void use(Player player) {
		long charge       = 2000L;
		int  numSnowballs = NUM_SNOWBALLS;

		if (player.getName()
				.equals("Skis")) {
			charge = 0L;
			numSnowballs = 30;
		}

		if (Timer.instance.use(player, "Gadget", "Snowball Launcher", charge, true)) {

			Vector direction = player.getLocation()
					.getDirection()
					.multiply(1.5D);
			UtilSound.playPhysical(player.getLocation(), Sound.CHICKEN_EGG_POP, 1F, 1F);

			List<Snowball> snowballs = new ArrayList<>();
			for (int i = 0; i < numSnowballs; i++) {
				Vector vel = direction.clone();

				Vector rand = UtilPos.createRand(0.15);
				vel.add(rand);

				Snowball snowball = (Snowball) player.getWorld()
						.spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
				snowball.setVelocity(vel);
				snowball.setShooter(player);

				snowball.setMetadata(Gadget.GADGET_METADATA_KEY, new FixedMetadataValue(manager.getPlugin(), true));
				snowballs.add(snowball);
			}

			getPlugin().getServer()
					.getScheduler()
					.runTaskLater(getPlugin(), () -> {
						player.getWorld()
								.getPlayers()
								.stream()
								.filter(other -> other.canSee(player))
								.forEach(other -> {
									for (Snowball snowball : snowballs) {
										UtilParticle.send(Particle.CLOUD, snowball.getLocation(),
										                  snowball.getVelocity(), 1, 0.5F,
										                  true);
									}
								});

					}, 2L);

		}
	}


	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (!projectile.getType()
				.equals(EntityType.SNOWBALL)) {
			return;
		}

		if (!projectile.hasMetadata(Gadget.GADGET_METADATA_KEY)) {
			return;
		}

		Block      hitBlock = UtilEvent.getCollidedBlock(projectile);
		BlockState hitState = hitBlock.getState();

		if (hitState instanceof InventoryHolder || hitState instanceof Banner) {
			return;
		}

		Material hitMaterial = hitState.getType();

		if (!hitMaterial.isSolid()) {
			return;
		}

		if (ignore.contains(hitMaterial)) {
			return;
		}

		hitBlock.setType(Material.SNOW_BLOCK);

		getPlugin().getServer()
				.getScheduler()
				.runTaskLater(getPlugin(), new Runnable() {

					@Override
					public void run() {
						hitState.update(true);
					}

				}, 120L);
	}


}
