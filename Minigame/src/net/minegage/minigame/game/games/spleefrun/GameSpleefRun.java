package net.minegage.minigame.game.games.spleefrun;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilMat;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.board.helper.SurvivalHelper;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.spleefrun.kits.KitDefault;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GameSpleefRun
		extends GameFFA {
		
	private Set<DecayBlock> decaying = new HashSet<>();
	
	public GameSpleefRun(MinigameManager manager) {
		super(manager, GameType.SPLEEF_RUN, new String[] { }, new KitDefault());
		
		this.damagePlayerVsPlayer = true;
		this.explainFreeze = false;

		getBoardManager().addBoardHelper(new SurvivalHelper(this));
	}

	@Override
	public void giveBoard(Player player, Board board) {
		super.giveBoard(player, board);

		ObjectiveSide side = board.getSideObjective();
		side.addRow("");
	}

	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		Projectile proj = event.getEntity();
		if (proj.getType() == EntityType.SNOWBALL) {
			Block block = UtilEvent.getCollidedBlock(proj);
			if (block.getType()
					.isSolid()) {
				block.setType(Material.AIR);
			}
		}
	}

	@EventHandler
	public void breakBlock(PlayerInteractEvent event) {
		if (!UtilEvent.isBlockClick(event)) {
			return;
		}

		breakBlock(event.getPlayer(), event.getClickedBlock());
	}

	@EventHandler
	public void breakBlock(BlockDamageEvent event) {
		breakBlock(event.getPlayer(), event.getBlock());
	}

	public void breakBlock(Player player, Block block) {
		if (!isPlaying() || explaining || !canInteract(player)) {
			return;
		}

		block.setType(Material.AIR);
		giveSnowball(player);
		UtilSound.playPhysical(block.getLocation(), Sound.DIG_STONE, 1F, Rand.rFloat(0.8F, 1.3F));
	}

	private ItemStack snowball = new ItemStack(Material.SNOW_BALL);

	public void giveSnowball(Player player) {
		if (Rand.rDouble(1.0) > 0.80) {
			player.getInventory()
					.addItem(snowball);
		}
	}
	
	private boolean isDecaying(Block block) {
		for (DecayBlock decay : decaying) {
			if (decay.block.equals(block)) {
				return true;
			}
		}
		
		return false;
	}
	
	@EventHandler
	public void tickDecay(TickEvent event) {
		if (event.isNot(Tick.TICK_1)) {
			return;
		}
		
		if (!isPlaying() || explaining) {
			return;
		}
		
		for (Player player : getPlayersIn()) {
			for (Block block : UtilEntity.getSupportingBlocks(player)) {
				if (!isDecaying(block)) {
					decaying.add(new DecayBlock(block));
				}
			}
		}
		
		Iterator<DecayBlock> decayIt = decaying.iterator();
		
		while (decayIt.hasNext()) {
			DecayBlock decay = decayIt.next();
			
			decay.tick();
			if (decay.isExpired()) {
				decayIt.remove();
			}
			
		}
	}
	
	private class DecayBlock {
		
		DecayState state = DecayState.STABLE;
		long decayStart = System.currentTimeMillis();
		Block block;
		
		public DecayBlock(Block block) {
			this.block = block;
		}
		
		public void tick() {
			long timePassed = UtilTime.timePassedSince(decayStart);
			DecayState newState = DecayState.getState(timePassed);
			
			if (state != newState) {
				
				if (newState == DecayState.FALL) {
					getFallingBlockManager().createFallingBlock(block, true);
				} else {
					UtilBlock.set(block, newState.data);
				}
			}
		}
		
		public boolean isExpired() {
			return UtilTime.hasPassedSince(decayStart, DecayState.FALL.time + 100L);
		}
		
	}
	
	private enum DecayState {
		FALL(UtilMat.getData(Material.STAINED_CLAY, (byte) 14), 750L),
		RED(UtilMat.getData(Material.STAINED_CLAY, (byte) 14), 500L),
		ORANGE(UtilMat.getData(Material.STAINED_CLAY, (byte) 1), 250L),
		YELLOW(UtilMat.getData(Material.STAINED_CLAY, (byte) 4), 0L),
		STABLE(null, 0L),
		
		;
		private MaterialData data;
		private long time;
		
		DecayState(MaterialData data, long time) {
			this.data = data;
			this.time = time;
		}
		
		public static DecayState getState(long timePassed) {
			for (DecayState state : values()) {
				if (UtilTime.hasPassed(timePassed, state.time)) {
					return state;
				}
			}
			
			return null;
		}
		
	}
	
}
