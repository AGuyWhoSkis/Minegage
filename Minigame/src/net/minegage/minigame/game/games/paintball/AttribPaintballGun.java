package net.minegage.minigame.game.games.paintball;


import net.minegage.common.misc.Click;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilMat;
import net.minegage.common.util.UtilSound;
import net.minegage.core.combat.event.CombatEvent;
import net.minegage.core.combat.DeathMessenger;
import net.minegage.minigame.kit.attrib.AttribItem;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class AttribPaintballGun
		extends AttribItem {
		
	private List<MaterialData> splatterBlocks = new ArrayList<>();
	
	public AttribPaintballGun() {
		super("Paintball Gun", new String[] { "Pew pew pew!" }, Material.IRON_BARDING);
		
		splatterBlocks.add(UtilMat.getData(DyeColor.RED));
		splatterBlocks.add(UtilMat.getData(DyeColor.BLUE));
		splatterBlocks.add(UtilMat.getData(DyeColor.LIME));
		splatterBlocks.add(UtilMat.getData(DyeColor.MAGENTA));
		splatterBlocks.add(UtilMat.getData(DyeColor.YELLOW));
		splatterBlocks.add(UtilMat.getData(DyeColor.ORANGE));
	}
	
	@Override
	public void apply(Player player) {
	
	}
	
	@Override
	public void onClick(Player player, ItemStack item, Click click) {
		if (Timer.instance.use(player, getName(), 300L, false)) {
			Material type = item.getType();

			Class<? extends Projectile> entClass = null;

			GameTeam team = getGame().getTeam(player);
			if (team.getName().contains("Red")) {
				entClass = Snowball.class;
			} else if (team.getName().contains("Blue")) {
				entClass = EnderPearl.class;
			}

			Projectile proj = player.launchProjectile(entClass);
			proj.setVelocity(proj.getVelocity()
					.multiply(Rand.rDouble(1.75, 2.0)));
					
			DeathMessenger.setWeaponName(proj, "Paintball Gun");
			UtilSound.playPhysical(proj.getLocation(), Sound.CHICKEN_EGG_POP, 1F, Rand.rFloat(1.5F, 2F));
		}
	}
	
	@EventHandler
	public void instakill(CombatEvent event) {
		if (event.isCancelled() || !isActive()) {
			return;
		}

		if (!event.isPlayerDamaged()) {
			return;
		}
		
		Entity damager = event.getDirectDamager();
		if (damager == null) {
			return;
		}
		
		if (damager.getType() == EntityType.ENDER_PEARL || damager.getType() == EntityType.SNOWBALL) {
			event.addModIncrement(100.0);
			
			if (event.isPlayerDamager()) {
				UtilSound.playLocal(event.getPlayerDamager(), Sound.SUCCESSFUL_HIT, 1F, 0.5F);
			}
		}
		
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!isActive()) {
			return;
		}

		if (event.getEntityType() == EntityType.SNOWBALL || event.getEntityType() == EntityType.ENDER_PEARL) {
			splatter(event.getEntity());
			event.getEntity()
					.remove();
		}
	}
	
	private void splatter(Projectile proj) {
		Block block = UtilEvent.getCollidedBlock(proj);
		if (block != null) {
			
			Set<Block> blocks = UtilBlock.getAdjacentBlocks(block);
			
			MaterialData splatter = UtilJava.getRandIndex(splatterBlocks);
			
			Iterator<Block> blocksIt = blocks.iterator();
			while (blocksIt.hasNext()) {
				Block splattered = blocksIt.next();
				
				if (Rand.chance(50.0)) {
					splatter(splattered, splatter);
				}
			}
			
			// Guarantee that the hit block will be splattered
			splatter(block, splatter);
			
			UtilEffect.play(block.getLocation(), Effect.STEP_SOUND, splatter.getItemType());
		}
		
	}
	
	private void splatter(Block block, MaterialData splatter) {
		if (block.getType()
				.isSolid()) {
			UtilBlock.set(block, splatter);
		}
	}
	
}
