package net.minegage.common.block;


import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.token.BlockToken;
import net.minegage.common.util.UtilEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BlockManager
		extends PluginModule {

	private int fallingBlocks = 0;

	private final String METADATA_KEY = "tracked block";

	private Map<BlockToken, Integer> cracked = new HashMap<>();
	
	public BlockManager(JavaPlugin plugin) {
		super("Block Manager", plugin);
	}
	
	@SuppressWarnings("deprecation")
	public FallingBlock createFallingBlock(Block block, boolean deleteOnDeath) {
		Location   loc   = block.getLocation();
		BlockState state = block.getState();
		block.setType(Material.AIR);
		return createFallingBlock(loc, state.getType(), state.getData()
				.getData(), deleteOnDeath);
	}
	
	@SuppressWarnings("deprecation")
	public FallingBlock createFallingBlock(Location location, Material type, byte data, boolean deleteOnDeath) {
		FallingBlock fallingBlock = location.getWorld()
				.spawnFallingBlock(location, type, data);
				
		setDeleteOnDeath(fallingBlock, deleteOnDeath);
		fallingBlock.setDropItem(false);

		fallingBlocks++;
		return fallingBlock;
	}
	
	public void setDeleteOnDeath(FallingBlock fallingBlock, boolean deleteOnDeath) {
		fallingBlock.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, deleteOnDeath));
	}
	
	public boolean isDeleteOnDeath(FallingBlock fallingBlock) {
		if (!fallingBlock.hasMetadata(METADATA_KEY)) {
			return false;
		}
		
		return fallingBlock.getMetadata(METADATA_KEY)
				.get(0)
				.asBoolean();
	}
	
	@EventHandler
	public void removeBlock(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof FallingBlock)) {
			return;
		}

		FallingBlock fallingBlock = (FallingBlock) event.getEntity();
		if (fallingBlock.hasMetadata(METADATA_KEY)) {
			fallingBlocks--;
			final Block block = event.getBlock();
			runSyncDelayed(5L, new Runnable() {
				@Override
				public void run() {
					UtilEffect.breakBlock(block);
				}
			});

		}
	}
	
	public void crackBlock(Block block, int step) {
		removeCrack(block);
		
		if (step >= 1 && step <= 10) {
			BlockToken token = new BlockToken(block);
			cracked.put(token, step);
		}
		
		UtilEffect.breakAnimation(block, step);
	}
	
	public void clearCrack(Block block) {
		crackBlock(block, -1);
	}
	
	/**
	 * Because cracks will automatically go away after 20 seconds, the packet must be resent to
	 * "refresh" the cracks. 15 second tick is used instead of 20, as 20 may cause flickering
	 */
	@EventHandler
	public void refreshCrack(TickEvent event) {
		if (event.isNot(Tick.SEC_20)) {
			return;
		}
		
		for (Map.Entry<BlockToken, Integer> entry : cracked.entrySet()) {
			Block block = entry.getKey()
					.getBlock();
			if (block == null) {
				continue;
			}
			
			UtilEffect.breakAnimation(block, entry.getValue());
		}
	}
	
	@EventHandler
	public void removeCrack(BlockBreakEvent event) {
		removeCrack(event.getBlock());
	}
	
	@EventHandler
	public void removeCrack(ChunkUnloadEvent event) {
		Iterator<Entry<BlockToken, Integer>> crackedIt = cracked.entrySet()
				.iterator();
				
		while (crackedIt.hasNext()) {
			BlockToken next = crackedIt.next()
					.getKey();
					
			Location location = next.getLocation();
			if (location == null || event.getChunk()
					.equals(location.getChunk())) {
				crackedIt.remove();
			}
		}
	}
	
	private void removeCrack(Block block) {
		Iterator<Entry<BlockToken, Integer>> crackedIt = cracked.entrySet()
				.iterator();
				
		while (crackedIt.hasNext()) {
			BlockToken token = crackedIt.next()
					.getKey();
			Block tokenBlock = token.getBlock();
			if (tokenBlock != null && tokenBlock.equals(block)) {
				crackedIt.remove();
				break;
			}
		}
	}

	public int getFallingBlocks() {
		return fallingBlocks;
	}


}
