package net.minegage.common.block;


import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;


public class BlockTracker {
	
	protected Set<BlockState> blocks = new HashSet<>();
	
	public void track(Block block) {
		blocks.add(block.getState());
	}
	
	public void restoreAll() {
		for (BlockState state : blocks) {
			state.update(true);
		}
	}
	
}
