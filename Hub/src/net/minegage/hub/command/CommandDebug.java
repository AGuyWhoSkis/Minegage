package net.minegage.hub.command;


import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.command.Flags;
import net.minegage.common.log.L;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilParticle;
import net.minegage.common.util.UtilPos;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.hub.HubPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class CommandDebug
		extends RankedCommand {
		
	private HubPlugin plugin;
	private Board board;
	
	public CommandDebug(HubPlugin plugin) {
		super(Rank.ADMIN, "hd");
		this.plugin = plugin;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onCommand(Player p, List<String> args, String raw, Flags flags) {

		if (args.size() == 0) {
			C.pMain(p, "Debug", "Specify an integer");
			return;
		}

		int i = -1;
		try {
			i = Integer.parseInt(args.get(0));
		} catch (NumberFormatException ex) {
			C.pMain(p, "Debug", "'" + args.get(0) + "' is not an integer");
			return;
		}

		Location l = p.getLocation();
		if (i == 1) {
			Block block = l.getBlock();
			if (block instanceof Attachable) {
				L.d("block");
			} else if (block.getState() instanceof Attachable) {
				L.d("state");
			} else {
				L.d("other");
			}
		} else if (i == 2) {
			L.error(new Exception("error message"), "Unable to do the thing");
		} else if (i == 3) {
			L.d("local to " + p.getName());
			UtilSound.playLocal(p, Sound.NOTE_PLING, 1F, 1F);
		} else if (i == 4) {

			LivingEntity entity = (LivingEntity) p.getWorld()
					.spawnEntity(p.getLocation(), EntityType.PIG);
			EntityCreature e = ((CraftCreature) entity).getHandle();

			PathfinderGoalSelector goalSelector   = e.goalSelector;
			PathfinderGoalSelector targetSelector = e.targetSelector;

			List<?> goalB = (List<?>) getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
			goalB.clear();
			List<?> goalC = (List<?>) getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
			goalC.clear();
			List<?> targetB = (List<?>) getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
			targetB.clear();
			List<?> targetC = (List<?>) getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
			targetC.clear();

			e.goalSelector.a(new PathfinderGoalLookAtPlayer(e, EntityHuman.class, 8.0F));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, true, false));

			final int eid = e.getId();

			ProtocolLibrary.getProtocolManager()
					.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.REL_ENTITY_MOVE) {
						@Override
						public void onPacketSending(PacketEvent event) {
							if (event.getPacketType() == PacketType.Play.Server.REL_ENTITY_MOVE) {
								PacketContainer packet = event.getPacket();
								if (eid == packet.getEntityModifier(event)
										.read(0)
										.getEntityId()) {
									event.setCancelled(true);
								}
							}
						}
					});


			Bukkit.getScheduler()
					.runTaskTimer(plugin, new Runnable() {
						@Override
						public void run() {
							Location location = entity.getLocation();
							location.setX(l.getX());
							location.setY(l.getY());
							location.setZ(l.getZ());
							entity.teleport(location);
						}

					}, 1L, 1L);

		} else if (i == 6) {
			p.sendMessage(C.translate(UtilJava.joinList(args, " ", 1)));
		} else if (i == 7) {

			Particle type = Particle.valueOf(args.get(1)
					                                 .toUpperCase());
			Vector location = p.getEyeLocation()
					.toVector();

			float offsetX = Float.parseFloat(args.get(2));
			float offsetY = Float.parseFloat(args.get(3));
			float offsetZ = Float.parseFloat(args.get(4));

			float speed        = Float.parseFloat(args.get(5));
			int   numParticles = Integer.parseInt(args.get(6));

			final WrapperPlayServerWorldParticles particle = UtilParticle
					.create(type, location, new Vector(offsetX, offsetY, offsetZ), numParticles,
					        speed, true);

			particle.sendPacket(p);


		} else if (i == 8) {
			Particle type = Particle.valueOf(args.get(1)
					                                 .toUpperCase());

			Vector centre = p.getEyeLocation()
					.toVector();

			int numParticles = Integer.parseInt(args.get(2));

			final WrapperPlayServerWorldParticles particle = UtilParticle
					.create(type, centre, new Vector(), numParticles, 0, true);
			final WrapperPlayServerWorldParticles particle2 = UtilParticle
					.create(type, centre, new Vector(), numParticles, 0, true);

			final double radius      = 1D;
			final int    deltaStep   = 15;
			final double peakRadDist = 0.2;

			new BukkitRunnable() {

				private int step = 0;

				@Override
				public void run() {
					Location loc = p.getEyeLocation();

					float x = (float) loc.getX();
					float y = (float) loc.getY() - 0.2F;
					float z = (float) loc.getZ();

					double rad = Math.toRadians(step);

					// Circle motion
					float deltaX = (float) (Math.cos(rad) * radius);
					float deltaZ = (float) (Math.sin(rad) * radius);

					// Vertical motion
					float deltaY = (float) Math.cos(rad * peakRadDist);

					particle.setX(x + deltaX);
					particle.setY(y + deltaY);
					particle.setZ(z + deltaZ);

					particle2.setX(x - deltaX);
					particle2.setY(y + deltaY);
					particle2.setZ(z - deltaZ);

					for (Player player : Bukkit.getOnlinePlayers()) {
						particle.sendPacket(player);
						particle2.sendPacket(player);
					}

					if ((step += deltaStep) > 3600) {
						cancel();
					}
				}
			}.runTaskTimer(plugin, 0L, 1L);
		} else if (i == 9) {
			Particle type = Particle.valueOf(args.get(1)
					                                 .toUpperCase());
			Vector centre = p.getEyeLocation()
					.toVector();

			int numParticles = Integer.parseInt(args.get(2));
			final WrapperPlayServerWorldParticles particle = UtilParticle
					.create(type, centre, new Vector(), numParticles, 0, true);
			final int deltaStep = 15;

			new BukkitRunnable() {
				int step = 0;

				@Override
				public void run() {
					Vector l = p.getEyeLocation()
							.toVector();

					l.add(new Vector(0, -0.25, 0));

					new BukkitRunnable() {
						@Override
						public void run() {

							particle.setX((float) l.getX());
							particle.setY((float) l.getY());
							particle.setZ((float) l.getZ());

							for (Player player : Bukkit.getOnlinePlayers()) {
								particle.sendPacket(player);
							}
						}
					}.runTaskLater(plugin, 3L);

					if ((step += deltaStep) > 3600) {
						cancel();
					}
				}
			}.runTaskTimer(plugin, 0L, 1L);
		} else if (i == 10)

		{
			ItemStack item = p.getItemInHand();
			p.sendMessage("Item name - " + item.getType());
			p.sendMessage("Item id - " + item.getTypeId() + ":" + item.getData()
					.getData());
		} else if (i == 11)

		{

			EntityType type = EntityType.valueOf(args.get(1));
			LivingEntity entity = (LivingEntity) p.getWorld()
					.spawnEntity(p.getLocation(), type);
			EntityEquipment equipment = entity.getEquipment();

			ItemStack helmet     = new ItemStack(Material.DIAMOND_HELMET);
			ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
			ItemStack leggings   = new ItemStack(Material.DIAMOND_LEGGINGS);
			ItemStack boots      = new ItemStack(Material.DIAMOND_BOOTS);
			ItemStack hand       = new ItemStack(Material.DIAMOND_SWORD);

			equipment.setHelmet(helmet);
			equipment.setChestplate(chestplate);
			equipment.setLeggings(leggings);
			equipment.setBoots(boots);
			equipment.setItemInHand(hand);

			UtilEntity.clearPathfinderGoals(entity);
		} else if (i == 12)

		{
			EntityType type = EntityType.valueOf(args.get(1));
			LivingEntity entity = (LivingEntity) p.getWorld()
					.spawnEntity(p.getLocation(), type);

			entity.getEquipment()
					.setArmorContents(p.getInventory()
							                  .getArmorContents());
			entity.getEquipment()
					.setItemInHand(p.getItemInHand());

			WorldServer world = ((CraftWorld) entity.getWorld()).getHandle();
			world.spigotConfig.maxCollisionsPerEntity = -1;
		} else if (i == 13)

		{

			DataWatcher dataWatcher = new DataWatcher(
					new EntitySlime(((CraftWorld) UtilWorld.getMainWorld()).getHandle()));
			dataWatcher.a(0, (byte) 0);
			dataWatcher.a(1, (short) 300);
			dataWatcher.a(3, (byte) 0);
			dataWatcher.a(6, 4F);
			dataWatcher.a(7, 0);
			dataWatcher.a(8, (byte) 0);
			dataWatcher.a(9, (byte) 0);
			dataWatcher.a(15, (byte) 0);

			WrapperPlayServerSpawnEntityLiving packet = new WrapperPlayServerSpawnEntityLiving();

			packet.setEntityID(50000);
			packet.setType(EntityType.FALLING_BLOCK);

			packet.setX(l.getX());
			packet.setY(l.getY());
			packet.setZ(l.getZ());

			packet.setYaw(l.getYaw());
			packet.setHeadYaw(l.getYaw());
			packet.setHeadPitch(l.getPitch());

			final double n = 3.9D;

			double motX = 0.0D;
			double motY = 1.0D;
			double motZ = 0.0D;

			if (motX < -n) {
				motX = -n;
			}
			if (motY < -n) {
				motY = -n;
			}
			if (motZ < -n) {
				motZ = -n;
			}
			if (motX > n) {
				motX = n;
			}
			if (motY > n) {
				motY = n;
			}
			if (motZ > n) {
				motZ = n;
			}

			WrappedDataWatcher watcher = new WrappedDataWatcher(dataWatcher);
			packet.setMetadata(watcher);

			packet.sendPacket(p);
		} else if (i == 21)
		
		{
			p.damage(2000.0D);
		} else if (i == 22)
		
		{
			
			Location main = UtilPos.roundClosestWhole(l);
			L.d("main loc " + UtilPos.format(main.toVector()));
			
			Location locBase = main.clone();
			
			for (Vector vec : blocksLayer) {
				Location relLoc = locBase.clone()
						.add(vec)
						.add(0, -1, 0);
				UtilBlock.set(relLoc, 5, (byte) 1);
			}
			
			int faceIndex = 0;
			for (Vector vec : blocksLayer) {
				Location relLoc = locBase.clone()
						.add(vec);
						
				BlockFace face = stairFaces.get(faceIndex++);
				byte data = UtilBlock.getStairFacing(face);
				data = UtilBlock.invertStair(data);
				
				UtilBlock.set(relLoc, 134, data);
			}
			
			for (Vector vec : blocksLayer) {
				Location relLoc = locBase.clone()
						.add(vec)
						.add(0, 1, 0);
				UtilBlock.set(relLoc, 126, (byte) 2);
			}
			
			
		} else if (i == 23)
		
		{
			
			byte data = UtilBlock.getStairFacing(BlockFace.valueOf(args.get(1)));
			
			if (args.size() > 2) {
				data = UtilBlock.invertStair(data);
			}
			
			UtilBlock.set(l, Material.SMOOTH_STAIRS, data);
			
		} else if (i == 24)
		
		{
			if (board == null) {
				board = new Board();
				
				board.setSideObjective();
				ObjectiveSide dupe = board.setSideObjective();
				
				board.setTabObjective();
				board.setTabObjective();
				
				for (int j = 0; j < 3; j++) {
					dupe.addRow("test");
				}
				
				p.setScoreboard(board.getBoard());
			}
		} else if (i == 25)
		
		{
			p.getInventory()
					.clear();
		}
	}
	
	private static List<Vector> blocksLayer = new ArrayList<>();
	private static List<BlockFace> stairFaces = new ArrayList<>();
	
	static {
		addBlockLayer(BlockFace.SOUTH_EAST);
		addBlockLayer(BlockFace.NORTH_EAST);
		addBlockLayer(BlockFace.NORTH_WEST);
		addBlockLayer(BlockFace.SOUTH_WEST);
		
		stairFaces.add(BlockFace.NORTH);
		stairFaces.add(BlockFace.WEST);
		stairFaces.add(BlockFace.SOUTH);
		stairFaces.add(BlockFace.EAST);
	}
	
	private static void addBlockLayer(BlockFace face) {
		blocksLayer.add(UtilPos.fromBlockFace(face)
				.multiply(0.5));
	}
	

	
	public Object getPrivateField(String fieldName, Class<?> clazz, Object object) {
		Field field;
		Object o = null;
		try {
			field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			o = field.get(object);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return o;
	}
	
}
