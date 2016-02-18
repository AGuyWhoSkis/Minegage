package net.minegage.core.equippable.trail;


import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.move.MoveManager;
import net.minegage.common.move.MoveToken;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilTime;
import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


public class TrailManager
		extends PluginModule {
		
	private EquippableManager manager;
	
	protected MenuTrail trailMenu;
	protected MoveManager moveManager;
	
	public TrailManager(EquippableManager manager, MoveManager moveManager) {
		super("Trail Manager", manager);
		
		this.manager = manager;
		this.moveManager = moveManager;
		
		int slot = 18;
		
		manager.addDonator(new TrailEffect(manager, "Crit", UtilItem.create(Material.DIAMOND_SWORD), slot++, Rank.PRO, Effect.CRIT, 0, 0, 2,
		                                   new Vector(), 1F, new Vector(), 0));
		create("Void", UtilItem.create(Material.BEDROCK), slot++, Rank.PRO, Particle.SUSPENDED_DEPTH, 10, new Vector(0.5, 0.25, 0.5));
		create("Smoke", UtilItem.create(Material.QUARTZ), slot++, Rank.PRO, Particle.SMOKE_LARGE, 3);
		
		
		create("Snow", UtilItem.create(Material.SNOW_BALL), slot++, Rank.PRO, Particle.SNOW_SHOVEL, 4, new Vector(), 0.075F, new Vector(0.1, 0, 0.1));
		create("Green Star", UtilItem.create(Material.EMERALD), slot++, Rank.ACE, Particle.VILLAGER_HAPPY, 2, new Vector(0.25, 0.25, 0.25), 0.1F,
				new Vector(0, 1, 0));
		create("Water Bubbles", UtilItem.create(Material.WATER_BUCKET), slot++, Rank.ACE, Particle.WATER_WAKE, 5, new Vector(), 0F, new Vector(0, 1,
				0));
		create("Thundercloud", UtilItem.create(Material.FLINT), slot++, Rank.ACE, Particle.VILLAGER_ANGRY, 1, new Vector(), 0F, new Vector(0, 1.5,
				0));
		create("Lava Drip", UtilItem.create(Material.MAGMA_CREAM), slot++, Rank.ACE, Particle.DRIP_LAVA, 5, new Vector(0.02, 0, 0.02), 0.1F,
				new Vector(0, 1, 0));
		create("Cloud", UtilItem.create(Material.WOOL), slot++, Rank.ACE, Particle.CLOUD, 4, new Vector(), 0.075F, new Vector(0.1, 0, 0.1));


		create("Lava", UtilItem.create(Material.LAVA_BUCKET), slot++, Rank.MVP, Particle.LAVA, 2);
		create("Love", UtilItem.create(Material.RED_ROSE), slot++, Rank.MVP, Particle.HEART, 1, new Vector(), 0, new Vector(0, 1, 0));
		create("Miniboom", UtilItem.create(Material.TNT), slot++, Rank.MVP, Particle.EXPLOSION_LARGE);
		create("Music", UtilItem.create(Material.JUKEBOX), slot++, Rank.MVP, Particle.NOTE, 1, new Vector(), 1F, new Vector(0, 1, 0));
		create("Ender", UtilItem.create(Material.ENDER_PEARL), slot++, Rank.MVP, Particle.PORTAL, 5, new Vector(0.1, 0.1, 0.1), 0.05F, new Vector(0,
				0.3, 0));
		create("Slime", UtilItem.create(Material.SLIME_BALL), slot++, Rank.MVP, Particle.SLIME);
		create("Purple Star", UtilItem.create(Material.FERMENTED_SPIDER_EYE), slot++, Rank.MVP, Particle.SPELL_WITCH, 1, new Vector(), 0F, new Vector(
				0, 1, 0));
		create("Redstone", UtilItem.create(Material.REDSTONE), slot++, Rank.MVP, Particle.REDSTONE, 3, new Vector(0.05, 0.05, 0.05), 0F, new Vector(0,
				1, 0));
		create("Flames", UtilItem.create(Material.BLAZE_POWDER), slot++, Rank.MVP, Particle.FLAME, 5, new Vector(0.5F, 0.55F, 0.5F), 0.01F,
				new Vector(0F, 0.5F, 0F));
				
		// TrailParticle barrier = new TrailParticle(manager, "Barrier",
		// UtilItem.create(Material.BARRIER), slot++, Rank.DEFAULT,
		// Particle.BARRIER, true, new Vector(), new Vector(), 0F, 1, new int[0]);
		// barrier.setPermOverride("lebronhub.trail.barrier");
		//
		// manager.addFree(barrier, C.iMain + "Unlock by getting a " + C.iOut + "level 1000 ",
		// C.iMain + "island in skyblock");
	}



	
	@EventHandler
	public void trailTick(TickEvent event) {
		if (!event.getTick()
				.equals(Tick.TICK_1)) {
			return;
		}
		
		SafeMap<UUID, SafeMap<Trail, Location>> trailQueue = new SafeMap<>();
		
		Iterator<Entry<UUID, List<Equippable>>> activeIt = manager.getActiveEquipped()
				.entrySet()
				.iterator();
		while (activeIt.hasNext()) {
			Entry<UUID, List<Equippable>> entry = activeIt.next();
			
			UUID uid = entry.getKey();
			Player player = getServer().getPlayer(uid);
			
			List<Equippable> activeEquipped = entry.getValue();
			
			SafeMap<Trail, Location> locations = new SafeMap<>();
			for (Equippable equipped : activeEquipped) {
				if (equipped instanceof Trail) {
					Trail trail = (Trail) equipped;
					locations.put(trail, trail.getLocation(player));
				}
			}
			
			trailQueue.put(uid, locations);
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<UUID, SafeMap<Trail, Location>> entry : trailQueue.entrySet()) {
					
					UUID uid = entry.getKey();
					Player player = getServer().getPlayer(uid);
					
					if (player == null || !player.isOnline()) {
						continue;
					}
					
					for (Entry<Trail, Location> innerEntry : entry.getValue()
							.entrySet()) {
						Trail trail = innerEntry.getKey();
						Location location = innerEntry.getValue();
						
						MoveToken playerMovement = moveManager.getMoveToken(player);
						
						// If the player has moved within this time
						if (!UtilTime.hasPassedSince(playerMovement.lastPhysicalMoved, 100L)) {
							trail.play(player, location);
						}
					}
				}
			}
		}.runTaskLater(plugin, 3L);
		
	}
	
	public MenuTrail getTrailMenu() {
		return trailMenu;
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, boolean longDistance,
			Vector particleOffset, Vector spawnOffset, float particleData, int particleCount, int[] data) {
		TrailParticle particle = new TrailParticle(manager, name, item, displaySlot, rank, particleType, longDistance, particleOffset, spawnOffset,
				particleData, particleCount, data);
				
		if (rank == null) {
			throw new NullPointerException("Cannot use Trail creation shortcut with null rank");
		}
		
		manager.addDonator(particle, new String[0]);
		
		
		return particle;
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, int particleCount,
			Vector particleOffset, float speed, Vector spawnOffset, boolean longDistance) {
		return create(name, item, displaySlot, rank, particleType, longDistance, particleOffset, spawnOffset, speed, particleCount, new int[0]);
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, int particleCount,
			Vector particleOffset, float speed, Vector spawnOffset) {
		return create(name, item, displaySlot, rank, particleType, particleCount, particleOffset, speed, spawnOffset, true);
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, int particleCount,
			Vector particleOffset, float speed) {
		return create(name, item, displaySlot, rank, particleType, particleCount, particleOffset, speed, new Vector(), true);
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, int particleCount,
			Vector particleOffset) {
		return create(name, item, displaySlot, rank, particleType, particleCount, particleOffset, 0F);
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, int particleCount) {
		return create(name, item, displaySlot, rank, particleType, particleCount, new Vector());
	}
	
	public TrailParticle create(String name, ItemStack item, int displaySlot, Rank rank, Particle particleType) {
		return create(name, item, displaySlot, rank, particleType, 1);
	}
	
	
}
