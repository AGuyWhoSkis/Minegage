package net.minegage.common.block;


import net.minegage.common.module.LazyScheduler;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;


public class ExplosionManager
		extends PluginModule {
		
	private BlockManager manager;
	
	public boolean enabled = true;
	
	public int max = 250;
	
	public boolean regenerate = true;
	public boolean debris = true;
	
	/* Average seconds it should take for a block to regenerate */
	public double regenTime = 10.0;
	/* How many seconds the regen time should vary by */
	public double regenFlex = 2.0;
	
	public ExplosionManager(BlockManager manager) {
		super("Explosion Manager", manager);
		
		this.manager = manager;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onExplosion(BlockExplodeEvent event) {
		if (!enabled) {
			return;
		}
		
		handleExplosion(event.getBlock()
				.getLocation(), event.blockList());
		event.setYield(0F);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!enabled) {
			return;
		}
		
		handleExplosion(event.getLocation(), event.blockList());
		event.setYield(0F);
	}
	
	@SuppressWarnings("deprecation")
	private void handleExplosion(Location location, List<Block> blocks) {
		int current = manager.getFallingBlocks();
				
		List<Block> blockList = new ArrayList<>(blocks);
		
		ListIterator<Block> blockListIt = blockList.listIterator();
		while (blockListIt.hasNext()) {
			Block next = blockListIt.next();
			
			Material type = next.getType();
			if (type == Material.TNT || type == Material.AIR) {
				blockListIt.remove();
			}
			
			for (Block attached : UtilBlock.getAdjacentBlocks(next)) {
				if (attached.getState()
						.getData() instanceof Attachable) {
						
					if (attached.getRelative(( (Attachable) attached.getState()
							.getData() ).getAttachedFace())
							.equals(next) && !blockList.contains(attached)) {
							
						blockListIt.add(attached);
					}
				}
			}
		}
		
		if (regenerate) {
			blockList.sort(new Comparator<Block>() {
				@Override
				public int compare(Block b1, Block b2) {
					int mat1 = 0, mat2 = 0;
					
					if (b1.getType()
							.isSolid()) {
						mat1 = 1;
					}
					if (b2.getType()
							.isSolid()) {
						mat2 = 1;
					}
					
					return Integer.compare(mat2, mat1);
				}
			});
			
			long flex = UtilTime.toTicks(regenFlex);
			long ticks = UtilTime.toTicks(regenTime) + Rand.rLong(-flex, flex);
			
			for (Block block : blockList) {
				runSyncDelayed(ticks, new RegenRunnable(this, block.getState(), ticks));
				ticks++;
			}
		}
		
		for (Block block : blockList) {
			double percent = 100.0 - ( 100.0 * ( (double) current / (double) max ) );
			BlockState prevState = block.getState();
			
			UtilBlock.set(block, Material.AIR);
			
			if (Rand.chance(percent)) {
				Vector velocity = block.getLocation()
						.subtract(location)
						.toVector();
						
				velocity.setX(velocity.getX() * Rand.rDouble(0.2, 0.4));
				velocity.setY(velocity.getY() * Rand.rDouble(0.2, 0.4));
				velocity.setZ(velocity.getZ() * Rand.rDouble(0.2, 0.4));
				
				velocity.setY(Math.abs(velocity.getY()));
				
				manager.createFallingBlock(block.getLocation(), prevState.getType(), prevState.getRawData(), !debris)
						.setVelocity(velocity);
			}
			
			current++;
		}
	}
	
	private static class RegenRunnable
			implements Runnable {
			
		private LazyScheduler scheduler;
		private BlockState state;
		private MaterialData data;
		private long ticks;
		
		public RegenRunnable(LazyScheduler scheduler, BlockState state, long ticks) {
			this.scheduler = scheduler;
			this.state = state;
			this.data = state.getData();
			this.ticks = ticks;
		}
		
		@Override
		public void run() {
			state.update(true, false);
			
			if (state.getWorld()
					.getBlockAt(state.getLocation())
					.getType() == Material.AIR) {
				ticks = ticks / 2;
				if (ticks > 2) {
					state.setData(data);
					
					if (state.getType() != Material.AIR) {
						scheduler.runSyncDelayed(ticks, this);
					}
				}
			}
		}
		
	}
	
}
