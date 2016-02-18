package net.minegage.common.util;


import com.comphenix.packetwrapper.WrapperPlayServerBlockBreakAnimation;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class UtilEffect {

	public static void play(Location location, Effect effect, Material data) {
		location.getWorld()
				.playEffect(location, effect, data);
	}

	public static void play(Location location, Effect effect, int id, int data, float offX, float offY, float offZ,
	                        float speed,
	                        int particleCount, int radius) {
		location.getWorld()
				.spigot()
				.playEffect(location, effect, id, data, offX, offY, offZ, speed, particleCount, radius);
	}

	@SuppressWarnings ("deprecation")
	public static void breakBlock(Location location, Material type, byte data) {
		location.getWorld()
				.spigot()
				.playEffect(location, Effect.STEP_SOUND, type.getId(), data, 0F, 0F, 0F, 0F, 0, 50);
	}

	@SuppressWarnings ("deprecation")
	public static void breakBlock(Block block) {
		breakBlock(block.getLocation(), block.getType(), block.getData());
		block.setType(Material.AIR);
	}


	private static int crackId = 100000;

	/**
	 @param block The block to crack
	 @param step  The crack "step"; a value from 1 to 10 (The higher, the more cracked)
	 */
	public static void breakAnimation(Block block, int step) {
		WrapperPlayServerBlockBreakAnimation crack = new WrapperPlayServerBlockBreakAnimation();
		crack.setDestroyStage(step);
		crack.setEntityID(crackId++);
		crack.setLocation(new BlockPosition(block.getX(), block.getY(), block.getZ()));

		for (Player player : block.getWorld()
				.getPlayers()) {
			crack.sendPacket(player);
		}
	}

}
