package net.minegage.minigame.game.games.spleef;


import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilSound;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.spleef.kits.KitAmazing;
import net.minegage.minigame.game.games.spleef.kits.KitStandard;
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


public class GameSpleef
		extends GameFFA {
		
	public GameSpleef(MinigameManager manager) {
		super(manager, GameType.SPLEEF, new String[] { "Don't fall! Last player alive wins!",
				"Dig blocks to sabatoge other players!" }, new KitStandard(), new KitAmazing());
				
		this.damagePlayerVsPlayer = false;
		this.explainFreeze = false;
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
	
	@Override
	public void loadWorldData(DataFile worldData) {
		super.loadWorldData(worldData);
		// Do nothing
	}
	
	@Override
	public boolean endCheck() {
		return getPlayers(PlayerState.IN).size() < 2;
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
		if (Rand.rDouble(1.0) > 0.90) {
			player.getInventory()
					.addItem(snowball);
		}
	}
	
}
