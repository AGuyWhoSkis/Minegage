package net.minegage.minigame.game.games.riot.kit.attrib;


import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilEvent;
import net.minegage.minigame.kit.attrib.Attrib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;


public class AttribExtinguish
		extends Attrib {
		
	public AttribExtinguish() {
		super("Extinguish");
	}
	
	@Override
	public void apply(Player player) {
	
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!UtilEvent.isBlockClick(event)) {
			return;
		}
		
		if (!appliesTo(event.getPlayer())) {
			return;
		}
		
		Block block = event.getClickedBlock();
		
		if (block.getType() == Material.FIRE) {
			return;
		}
		
		for (Block adj : UtilBlock.getAdjacentBlocks(block)) {
			if (adj.getType() == Material.FIRE) {
				UtilEffect.breakBlock(adj);
			}
		}
	}
	
}
