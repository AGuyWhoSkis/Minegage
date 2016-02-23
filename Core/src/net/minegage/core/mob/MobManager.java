package net.minegage.core.mob;


import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilPos;
import net.minegage.core.event.EventClickEntity;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


/**
 Manages loading/unloading of entities from a file, and controls mob flags (eg flammable)
 */
public abstract class MobManager<T extends Mob>
		extends PluginModule {

	protected Set<T> mobs = new HashSet<>();
	protected String filePath;

	public MobManager(String name, JavaPlugin plugin, String filePath) {
		super(name, plugin);

		this.filePath = filePath;

		/* Loads the mobs in the worlds which are already loaded */
		for (World world : Bukkit.getWorlds()) {
			deserializeMobs(world);
			for (Chunk chunk : world.getLoadedChunks()) {
				manageMobs(chunk, true);
			}
		}

		runSyncTimer(100L, 100L, new BukkitRunnable() {
			@Override
			public void run() {
				for (T mob : mobs) {
					if (mob.isEntityLoaded() && mob.isArmourInvulnerable()) {
						EntityEquipment equipment = mob.getEntity()
								.getEquipment();
						if (equipment != null) {
							equipment.setHelmet(UtilItem.resetDurability(equipment.getHelmet()));
							equipment.setChestplate(UtilItem.resetDurability(equipment.getChestplate()));
							equipment.setLeggings(UtilItem.resetDurability(equipment.getLeggings()));
							equipment.setBoots(UtilItem.resetDurability(equipment.getBoots()));
						}
					}
				}
			}

		});
	}

	@Override
	protected void onDisable() {
		for (T mob : mobs) {
			mob.unload();
		}

		mobs.clear();
	}

	public abstract T deserializeMob(UUID uid, Location post, Map<String, String> properties);

	/**
	 Save any extra properties (eg kit name) to the map
	 */
	public abstract void serializeMob(T mob, Map<String, String> properties);

	/* Cache the mobs when the world is loaded */
	@EventHandler
	public final void loadMobs(WorldLoadEvent event) {
		deserializeMobs(event.getWorld());

		for (Chunk chunk : event.getWorld()
				.getLoadedChunks()) {
			manageMobs(chunk, true);
		}
	}

	/* Remove the mobs from the cache when the world is unloaded */
	@EventHandler
	public void unloadMobs(WorldUnloadEvent event) {
		for (Entity entity : event.getWorld()
				.getEntities()) {

			Iterator<T> mobsIt = mobs.iterator();

			while (mobsIt.hasNext()) {
				T mob = mobsIt.next();
				if (mob.getUid()
						.equals(entity.getUniqueId())) {
					mobsIt.remove();
				}
			}
		}
	}

	@EventHandler
	public final void loadMobs(ChunkLoadEvent event) {
		manageMobs(event.getChunk(), true);
	}

	@EventHandler
	public final void unloadMobs(ChunkUnloadEvent event) {
		manageMobs(event.getChunk(), false);
	}

	/**
	 Loads and unloads mobs from a chunk
	 */
	private void manageMobs(Chunk chunk, boolean load) {
		for (Entity entity : chunk.getEntities()) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}

			T mob = getMob(entity);
			if (mob != null) {
				if (load) {
					loadMob(mob, (LivingEntity) entity);
				} else {
					mob.unload();
				}
			}
		}
	}

	public void loadMob(T mob, LivingEntity entity) {
		mob.load(entity);

		runSyncDelayed(20L, () -> {
			if (mob.isEntityLoaded()) {
				for (String tag : mob.tags) {
					UtilEntity.removePassengers(entity);
				}
			}
		});

		runSyncDelayed(30L, () -> {
			if (mob.isEntityLoaded()) {
				for (String tag : mob.tags) {
					UtilEntity.addTag(entity, tag);
				}
			}
		});
	}

	/**
	 Reads from file and caches mobs (if any)
	 */
	public void deserializeMobs(World world) {
		File file = getFile(world);

		if (!file.exists()) {
			return;
		}

		if (!file.canRead()) {
			L.severe("Unable to load mobs from world \"" + world.getName() + "\"; no read permissions for " +
			         file.getPath());
			return;
		}

		try {
			List<String> fileLines = FileUtils.readLines(file);

			int cached      = 0;
			int currentLine = 1;
			for (String line : fileLines) {

				try {
					// Parse properties from line
					Map<String, String> properties = new HashMap<>();

					String[] split = line.split("\\|");
					for (String propertyValue : split) {
						String[] propertyValueSplit = propertyValue.split("=");

						String property = propertyValueSplit[0];
						String value    = propertyValueSplit[1];

						properties.put(property, value.replace("_", " "));
					}

					String uidString = properties.get("uid");
					UUID   uid       = UUID.fromString(uidString);

					Location post = UtilPos.deserializeLocation(properties.get("post"), world);

					T mob = deserializeMob(uid, post, properties);

					registerMob(mob);

					cached++;
				} catch (Exception ex) {
					L.error(ex, "Failed to parse mob from " + file.getPath() + "; line " + currentLine + ": \"" +
					            currentLine + "\"");
				}

				currentLine++;
			}

			logInfo("Cached " + cached + " mobs from " + world.getName());
		} catch (IOException | ArrayIndexOutOfBoundsException ex) {
			L.error(ex, "Unable to load mobs from world \"" + world.getName() + "\"");
		}
	}

	public final void deleteMob(T mob)
			throws IOException {
		World world = mob.getEntity()
				.getWorld();

		File file = getFile(world);
		file.createNewFile();

		UUID uid = mob.getEntity()
				.getUniqueId();
		String uidString = uid.toString();

		List<String> fileLines = FileUtils.readLines(file);

		Iterator<String> fileIt = fileLines.iterator();
		while (fileIt.hasNext()) {
			String serialized = fileIt.next();
			if (serialized.contains(uidString)) {
				fileIt.remove();
			}
		}

		FileUtils.writeLines(file, fileLines);

		Iterator<T> mobsIt = mobs.iterator();
		while (mobsIt.hasNext()) {
			T next = mobsIt.next();
			if (next.getUid()
					.equals(uid)) {
				mobsIt.remove();
			}
		}

		mob.entity = null;
	}

	public final void clearMobs(World world) {
		FileUtils.deleteQuietly(getFile(world));

		String worldName = world.getName();

		Iterator<T> mobsIt = mobs.iterator();
		while (mobsIt.hasNext()) {
			T mob = mobsIt.next();

			if (mob.world.equals(worldName)) {
				UUID uid = mob.getUid();

				for (Entity entity : world.getEntities()) {
					if (entity.getUniqueId()
							.equals(uid)) {
						UtilEntity.kill(entity);
					}
				}

				mobsIt.remove();
			}
		}
	}

	public final void registerMob(T mob) {
		for (T other : mobs) {
			if (mob.getUid()
					    .equals(other.getUid()) && mob.world.equals(other.world)) {
				throw new IllegalStateException("Duplicate mob " + mob.getUid());
			}
		}
		mobs.add(mob);
	}

	public final void removeMob(T mob) {
		mobs.remove(mob);
	}

	public Entity getEntity(UUID uid, World world) {
		for (Entity entity : world.getEntities()) {
			if (entity.getUniqueId()
					.equals(uid)) {
				return entity;
			}
		}
		return null;
	}

	public final Set<T> getMobs() {
		return mobs;
	}

	public final T getMob(UUID uid, World world) {
		for (T mob : mobs) {
			if (mob.getUid()
					    .equals(uid) && mob.world.equals(world.getName())) {
				return mob;
			}
		}
		return null;
	}

	public final T getMob(Entity entity) {
		return getMob(entity.getUniqueId(), entity.getWorld());
	}

	public final Set<T> getMobs(World world) {
		Set<T> mobs = new HashSet<>();

		for (T mob : this.mobs) {
			if (mob.world.equals(world.getName())) {
				mobs.add(mob);
			}
		}

		return mobs;
	}

	public final File getFile(World world) {
		return new File(world.getWorldFolder(), filePath);
	}
	
	/* Behaviour management */

	@EventHandler
	public final void preventHostility(EntityTargetEvent event) {
		// Because Bukkit hates us
		if (event.getTarget() == null) {
			return;
		}

		T targeter = getMob(event.getEntity());
		if (targeter != null && !targeter.targetsOthers) {
			event.setCancelled(true);
			return;
		}

		if (isTagSlime(event.getTarget())) {
			event.setCancelled(true);
			return;
		}

		T targeted = getMob(event.getTarget());
		if (targeted != null && !targeted.isTargettable()) {
			event.setCancelled(true);
			return;
		}

	}

	@EventHandler
	public final void preventMobDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.VOID && isTagSlime(event.getEntity())) {
			event.setCancelled(true);
			return;
		}

		T mob = getMob(event.getEntity());

		if (mob == null) {
			return;
		}

		if (mob.isInvulnerable()) {
			event.setCancelled(true);
		}

		if (event.getCause() == DamageCause.VOID) {
			if (mob.isReturnOnFall()) {
				mob.teleportToPost();
			} else {
				event.getEntity()
						.remove();
				try {
					deleteMob(mob);
				} catch (IOException ex) {
					L.error(ex, "Unable to delete mob fallen into void");
					ex.printStackTrace();
				}
			}
		}
	}

	@EventHandler
	public final void preventMobHarm(EntityDamageByEntityEvent event) {
		ProjectileSource damager = UtilEvent.getIndirectDamager(event);
		if (damager == null) {
			return;
		}

		Entity entity = (Entity) damager;

		T mob = getMob(entity);
		if (mob == null) {
			return;
		}

		if (!mob.harmsOthers()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void preventMobCombust(EntityCombustEvent event) {
		if (isTagSlime(event.getEntity())) {
			event.setCancelled(true);
			return;
		}

		T mob = getMob(event.getEntity());
		if (mob != null && !mob.isFlammable()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public final void removeDead(EntityDeathEvent event) {
		T mob = getMob(event.getEntity());
		if (mob != null) {
			UtilEntity.removePassengers(event.getEntity());
			mobs.remove(mob);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public final void onInteract(EventClickEntity event) {
		Entity entity = event.getClicked();

		T mob = getMob(entity);


		// Look through vehicles to get mob
		while (mob == null && entity.getVehicle() != null) {
			entity = entity.getVehicle();
			mob = getMob(entity);
		}

		if (mob != null) {
			mob.onClick(event);
		}
	}

	private boolean isTagSlime(Entity entity) {
		return entity != null && entity.getType() == EntityType.SLIME && ((Slime) entity).getSize() == -1;
	}

}
