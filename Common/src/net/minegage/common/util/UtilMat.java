package net.minegage.common.util;


import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;


public class UtilMat {
	
	@SuppressWarnings("deprecation")
	private static MaterialData getData(int typeId, byte data) {
		return new MaterialData(typeId, data);
	}
	
	@SuppressWarnings("deprecation")
	public static MaterialData getData(Material type, byte data) {
		return getData(type.getId(), data);
	}
	
	@SuppressWarnings("deprecation")
	public static MaterialData getData(DyeColor colour) {
		return getData(Material.WOOL, colour.getData());
	}
	
	public static MaterialData getData(Material type) {
		return getData(type, (byte) 0);
	}
	
	@SuppressWarnings("deprecation")
	public static MaterialData getData(MaterialData other) {
		return new MaterialData(other.getItemType(), other.getData());
	}
	
	public static MaterialData getData(BlockState state) {
		if (state == null) {
			return null;
		}
		
		return getData(state.getData());
	}
	
	public static MaterialData getData(Block block) {
		if (block == null) {
			return null;
		}
		
		return getData(block.getState());
	}
	
}
