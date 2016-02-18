package net.minegage.minigame.command;


import com.comphenix.packetwrapper.WrapperPlayServerWorldBorder;
import com.comphenix.protocol.wrappers.EnumWrappers.WorldBorderAction;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityExperienceOrb;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minegage.common.command.Flags;
import net.minegage.common.log.L;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.event.EventClickEntity;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;


public class CommandDebug
		extends RankedCommand
		implements Listener {
		
	private MinigameManager manager;
	
	public CommandDebug(MinigameManager manager) {
		super(Rank.ADMIN, "gd");
		this.manager = manager;
		
		manager.registerEvents(this);
		
	}
	
	private Game getGame() {
		return manager.getGame();
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		int i = Integer.parseInt(args.get(0));
		Location l = player.getLocation();
		
		if (i == 1) {
			
			float yaw = l.getYaw();
			L.d("yaw " + yaw);
			
			float degreeChange = 15F;
			L.d("degree change " + degreeChange);
			float yawChange = 360F / ( degreeChange * 360F );
			L.d("yaw change " + yawChange);
			
			yaw += yawChange;
			L.d("new yaw " + yaw);
			
			yaw %= 1;
			L.d("truncated " + yaw);
			
			l.setYaw(yaw);
			player.teleport(l);
			
		} else if (i == 2) {
			
			l.add(0, 5, 0);
			
			LivingEntity entity = (LivingEntity) l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(1)));
					
			Entity name = l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(2)));
			name.setCustomName("Name");
			name.setCustomNameVisible(true);
			
			UtilEntity.getNMSEntity(name)
					.setInvisible(true);
					
			if (name instanceof LivingEntity) {
				( (LivingEntity) name ).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
			}
			
			entity.setPassenger(name);
		} else if (i == 3) {
			LivingEntity entity = (LivingEntity) l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(1)));
					
			ExperienceOrb name = (ExperienceOrb) l.getWorld()
					.spawnEntity(l, EntityType.EXPERIENCE_ORB);
					
			name.setCustomName("Name");
			name.setCustomNameVisible(true);
			
			entity.setPassenger(name);
			
		} else if (i == 4) {
			LivingEntity entity = (LivingEntity) l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(1)));
					
			Location skullLoc = l.clone()
					.add(0, 55, 0);
					
			WitherSkull fakeName = (WitherSkull) l.getWorld()
					.spawnEntity(skullLoc, EntityType.WITHER_SKULL);
					
			fakeName.setCustomName("Skull Name");
			fakeName.setCustomNameVisible(true);
			PacketPlayOutAttachEntity fakeRide = new PacketPlayOutAttachEntity(0, UtilEntity.getNMSEntity(entity), UtilEntity.getNMSEntity(fakeName));
			
			for (Player p : UtilServer.players()) {
				PlayerConnection conn = UtilPlayer.getNmsPlayer(p).playerConnection;
				
				// conn.sendPacket(fakeSpawn);
				conn.sendPacket(fakeRide);
			}
			
		} else if (i == 5) {
			WorldServer world = ( (CraftWorld) l.getWorld() ).getHandle();
			EntityChicken chicken = new EntityChicken(world);
			
			
			
			PacketPlayOutSpawnEntity spawnSkull = new PacketPlayOutSpawnEntity(chicken, id++);
			
			for (Player p : UtilServer.players()) {
				UtilPlayer.getNmsPlayer(p).playerConnection.sendPacket(spawnSkull);
			}
			
		} else if (i == 6) {
			
			Entity entity = l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(1)));
					
			WorldServer world = ( (CraftWorld) l.getWorld() ).getHandle();
			EntityExperienceOrb orb = new EntityExperienceOrb(world);
			
			orb.setLocation(l.getX(), l.getY() + 5, l.getZ(), 0, 0);
			orb.mount(UtilEntity.getNMSEntity(entity));
			
			PacketPlayOutSpawnEntityExperienceOrb spawnExp = new PacketPlayOutSpawnEntityExperienceOrb(orb);
			
			for (Player p : UtilServer.players()) {
				UtilPlayer.getNmsPlayer(p).playerConnection.sendPacket(spawnExp);
			}
			
		} else if (i == 7) {
			Entity entity = l.getWorld()
					.spawnEntity(l, UtilJava.parseEnum(EntityType.class, args.get(1)));
					
			entity.setCustomName("Name");
			
			net.minecraft.server.v1_8_R3.Entity nmsEntity = UtilEntity.getNMSEntity(entity);
			DataWatcher watch = nmsEntity.getDataWatcher();
			
			List<WatchableObject> watchables = watch.c();
			int data = watchables.size() - 1;
			for (WatchableObject obj : watch.c()) {
				L.d(data + " = " + obj.b());
				data--;
			}
			
			entity.setCustomNameVisible(true);
			
			data = watchables.size() - 1;
			for (WatchableObject obj : watch.c()) {
				L.d(data + " = " + obj.b());
				data--;
			}
			
		} else if (i == 8) {
			String stat = args.get(1);
			int val = manager.getGame()
					.getStatTracker()
					.get(player, stat);
			player.sendMessage(stat + " = " + val + " (you)");
		} else if (i == 9) {
			String stat = args.get(1);
			GameTeam team = manager.getGame()
					.getTeam(player);
			int val = manager.getGame()
					.getStatTracker()
					.get(team, stat);
			player.sendMessage(stat + " = " + val + " (team)");
		} else if (i == 10) {
			ItemStack item = player.getItemInHand();
			net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
			NBTTagCompound nbt = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
			
			L.d("attributes:");
			for (String str : nbt.c()) {
				L.d(str + " = " + nbt.b(str));
			}
			
			L.d("lore:");
			if (item.hasItemMeta() && item.getItemMeta()
					.hasLore()) {
				for (String str : item.getItemMeta()
						.getLore()) {
					L.d(str);
				}
			}
			L.d("done");
		} else if (i == 11) {
			L.d("giving effect...");
			player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(Integer.parseInt(args.get(1)), Integer.parseInt(args.get(2))));
		} else if (i == 12) {
			L.d("giving effect");
			player.addPotionEffect(PotionEffectType.JUMP.createEffect(100, 2), true);
		} else if (i == 13) {
			Entity entity = player.getWorld()
					.spawnEntity(player.getLocation(), EntityType.RABBIT);
			L.d(entity.getUniqueId()
					.toString());
			entity.teleport(UtilWorld.getMainWorld()
					.getSpawnLocation());
			L.d(entity.getUniqueId()
					.toString());
		} else if (i == 14) {
			player.sendMessage("display name '" + player.getDisplayName()
					.replaceAll(ChatColor.COLOR_CHAR + "", "&") + "'");
		} else if (i == 15) {
			player.setDisplayName(C.cBlue + "display name 2");
		} else if (i == 16) {
			player.setCustomName(C.cRed + "log name");
		} else if (i == 17) {
			player.setCustomName(C.cBlue + "log name 2");
		} else if (i == 18) {
			player.setItemInHand(null);
			player.updateInventory();
		} else if (i == 19) {
			for (Block block : UtilBlock.getBlocksNear(l.getBlock(), Integer.parseInt(args.get(1)))) {
				block.setType(Material.GLASS);
			}
		} else if (i == 20) {
			UtilEffect.breakAnimation(l.add(0, -1, 0)
					.getBlock(), Integer.parseInt(args.get(1)));
		} else if (i == 21) {
			for (Block block : UtilBlock.getBlocksNear(l.getBlock(), Integer.parseInt(args.get(1)))) {
				block.setType(Material.QUARTZ_BLOCK);
			}
		} else if (i == 22) {
			for (GameTeam team : getGame().getTeams()) {
				L.d(team.getName());
			}
		} else if (i == 23) {
			
			WrapperPlayServerWorldBorder border = new WrapperPlayServerWorldBorder();
			border.setCenterX(l.getX());
			border.setCenterZ(l.getZ());
			border.setOldRadius(10.0);
			border.setRadius(15.0);
			border.setSpeed(0L);
			border.setAction(WorldBorderAction.INITIALIZE);
			border.setPortalTeleportBoundary(0);
			border.setWarningDistance(0);
			border.setWarningTime(0);
			
			border.sendPacket(player);
		} else if (i == 24) {
			LivingEntity entity = (LivingEntity) l.getWorld().spawnEntity(l, EntityType.COW);

			Slime slime1 = spawnSlime(l);
			entity.setPassenger(slime1);

			ArmorStand stand1 = spawnStand(l);
			slime1.setPassenger(stand1);

			Slime slime2 = spawnSlime(l);
			stand1.setPassenger(slime2);

			Slime slime3 = spawnSlime(l);
			slime2.setPassenger(slime3);

			Slime slime4 = spawnSlime(l);
			slime3.setPassenger(slime4);

			ArmorStand stand2 = spawnStand(l);
			slime4.setPassenger(stand2);
		}
		
	}

	private Slime spawnSlime(Location loc) {
		Slime slime = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);

		slime.setSize(-1);
		slime.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));

		return slime;
	}

	private ArmorStand spawnStand(Location loc) {
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setCustomName("armor stand");
		stand.setCustomNameVisible(true);
		stand.setSmall(true);
		stand.setVisible(false);

		return stand;
	}
	
	
	@EventHandler
	public void onRightClick(EventClickEntity event) {
		Entity entity = event.getClicked();
		if (!( entity instanceof LivingEntity )) {
			return;
		}
		LivingEntity living = (LivingEntity) entity;
		UtilEntity.getSupportingBlocks(living);
		
	}
	
	
	
	private int id = 50000;
	
}
