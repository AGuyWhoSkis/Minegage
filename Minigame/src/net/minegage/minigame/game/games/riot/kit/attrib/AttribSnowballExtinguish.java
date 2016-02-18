package net.minegage.minigame.game.games.riot.kit.attrib;


import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilSound;
import net.minegage.minigame.kit.attrib.Attrib;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;


public class AttribSnowballExtinguish
		extends Attrib {
		
		
	public AttribSnowballExtinguish() {
		super("Extinguish", "Throw snowballs at fire to put it out!");
	}
	
	@Override
	public void apply(Player player) {
		// Do nothing
	}
	
	@EventHandler
	public void onSnowballHit(ProjectileHitEvent event) {
		if (event.getEntityType() != EntityType.SNOWBALL) {
			return;
		}
		
		ProjectileSource source = event.getEntity()
				.getShooter();
		if (!( source instanceof Player )) {
			return;
		}
		
		Player player = (Player) source;
		if (!appliesTo(player)) {
			return;
		}
		
		Block hitBlock = UtilEvent.getCollidedBlock(event.getEntity());
		if (hitBlock.getType() == Material.AIR) {
			return;
		}
		
		for (Block block : UtilBlock.getBlocksNear(hitBlock, 2)) {
			if (block.getType() == Material.FIRE) {
				block.setType(Material.AIR);
				UtilEffect.play(block.getLocation(), Effect.STEP_SOUND, Material.SNOW_BLOCK);
				UtilSound.playPhysical(block.getLocation(), Sound.FIZZ, 1F, 1F);
			}
		}
		
	}
	
	
}
