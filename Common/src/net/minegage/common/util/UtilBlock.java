package net.minegage.common.util;


import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;


public class UtilBlock {
	
	public static Set<Block> getBlocksNear(Block block, int radius) {
		Set<Block> near = new HashSet<>();
		
		double radiusSquared = radius * radius + 0.5;
		int amp = Math.abs(radius);
		
		Vector pos = block.getLocation()
				.toVector();
				
		World world = block.getWorld();
		
		// Offset by 1 to correct the block location offset
		int base = -amp + 1;
		
		for (int x = base; x < amp; x++) {
			for (int y = base; y < amp; y++) {
				for (int z = base; z < amp; z++) {
					Vector other = pos.clone();
					other.setX(other.getX() + x);
					other.setY(other.getY() + y);
					other.setZ(other.getZ() + z);
					
					if (pos.distanceSquared(other) <= radiusSquared) {
						near.add(world.getBlockAt(other.getBlockX(), other.getBlockY(), other.getBlockZ()));
					}
				}
			}
		}
		
		return near;
	}
	
	public static Set<Block> getAdjacentBlocks(Block block) {
		Set<Block> adjacent = new HashSet<>();
		
		adjacent.add(block.getRelative(BlockFace.UP));
		adjacent.add(block.getRelative(BlockFace.DOWN));
		adjacent.add(block.getRelative(BlockFace.NORTH));
		adjacent.add(block.getRelative(BlockFace.EAST));
		adjacent.add(block.getRelative(BlockFace.SOUTH));
		adjacent.add(block.getRelative(BlockFace.WEST));
		
		return adjacent;
	}
	
	public static String getName(Block block) {
		return CraftMagicNumbers.getBlock(block)
				.getName();
	}
	
	public static Location getMidLocation(Block block) {
		return block.getLocation()
				.add(new Vector(0.5, 0.5, 0.5));
	}
	
	public static boolean isSign(Material type) {
		return type == Material.SIGN_POST || type == Material.WALL_SIGN;
	}
	
	public static boolean isSign(Block block) {
		return isSign(block.getType());
	}
	
	public static boolean isSolid(Block block) {
		return block.getType()
				.isSolid();
	}
	
	public static byte getStairFacing(BlockFace face) {
		switch (face) {
		case NORTH:
			return (byte) 3;
		case SOUTH:
			return (byte) 2;
		case WEST:
			return (byte) 1;
		case EAST:
			return (byte) 0;
		default:
			throw new IllegalArgumentException("Invalid stair direction " + face.toString());
		}
	}
	
	public static byte invertStair(byte stairData) {
		int data = stairData & 0x3;
		data |= 0x4;
		return (byte) data;
	}
	
	public static byte getWallSignFacing(BlockFace face) {
		switch (face) {
		case SOUTH:
			return 0;
		case SOUTH_SOUTH_WEST:
			return 1;
		case SOUTH_WEST:
			return 2;
		case WEST_SOUTH_WEST:
			return 3;
		case WEST:
			return 4;
		case WEST_NORTH_WEST:
			return 5;
		case NORTH_WEST:
			return 6;
		case NORTH_NORTH_WEST:
			return 7;
		case NORTH:
			return 8;
		case NORTH_NORTH_EAST:
			return 9;
		case NORTH_EAST:
			return 10;
		case EAST_NORTH_EAST:
			return 11;
		case EAST:
			return 12;
		case EAST_SOUTH_EAST:
			return 13;
		case SOUTH_EAST:
			return 14;
		case SOUTH_SOUTH_EAST:
			return 15;
		default:
			return -1;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void set(Block block, Material type, byte data) {
		block.setType(type);
		block.setData(data);
	}
	
	@SuppressWarnings("deprecation")
	public static void set(Block block, MaterialData data) {
		set(block, data.getItemType(), data.getData());
	}
	
	public static void set(Block block, Material type) {
		set(block, type, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public static void set(Block block, byte data) {
		block.setData(data);
	}
	
	public static void set(Location location, Material type) {
		UtilBlock.set(location, type, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public static void set(Location location, Material type, byte data) {
		UtilBlock.set(location, type.getId(), data);
	}
	
	public static void set(Location location, int id, byte data) {
		UtilBlock.set(location.getWorld(), id, data, location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}
	
	public static void set(World world, int id, byte data, int x, int y, int z) {
		net.minecraft.server.v1_8_R3.Chunk nmsChunk = ( (CraftWorld) world ).getHandle()
				.getChunkAt(x >> 4, z >> 4);
		IBlockData blockData = net.minecraft.server.v1_8_R3.Block.getByCombinedId(id + ( data << 12 ));
		
		BlockPosition position = new BlockPosition(x, y, z);
		nmsChunk.a(position, blockData);
		// nmsChunk.getWorld()
		// .notify(position);
	}
	
}
