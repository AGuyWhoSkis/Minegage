package net.minegage.common.token;


import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;


/**
 * Stores block information without the possibility of memory leaks caused by storing a location,
 * world, etc.
 */
public class BlockToken
		extends LocToken {
		
	public MaterialData blockData;
	
	public BlockToken(Block block) {
		super(block.getLocation());
		blockData = block.getState()
				.getData();
	}
	
	public Block getBlock() {
		Location location = getLocation();
		if (location != null) {
			return location.getBlock();
		}
		
		return null;
	}
	
	public BlockState getState() {
		Block block = getBlock();
		if (block == null) {
			return null;
		}
		
		return block.getState();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlockToken) {
			BlockToken other = (BlockToken) obj;
			Block otherBlock = other.getBlock();
			Block thisBlock = getBlock();
			
			return otherBlock != null && thisBlock != null && otherBlock.equals(thisBlock);
		} else if (obj instanceof Block) {
			Block otherBlock = (Block) obj;
			Block thisBlock = getBlock();
			
			return otherBlock != null && thisBlock != null && otherBlock.equals(thisBlock);
		} else {
			return false;
		}
	}
	
}
