package net.minegage.minigame.game.games.riot.kit.attrib;


import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.common.C;
import net.minegage.minigame.kit.attrib.Attrib;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;


public class AttribDefuseTNT
		extends Attrib {
		
	public AttribDefuseTNT() {
		super("Defuser");
	}
	
	@Override
	public void apply(Player player) {
		// Do nothing
	}
	
	@EventHandler
	public void onTntClick(PlayerInteractAtEntityEvent event) {
		ItemStack item = event.getPlayer()
				.getItemInHand();
		if (item == null) {
			return;
		}
		
		if (item.getType() != Material.SHEARS) {
			return;
		}
		
		if (!appliesTo(event.getPlayer())) {
			return;
		}
		
		UtilSound.playPhysical(event.getPlayer()
				.getLocation(), Sound.SHEEP_SHEAR, 1F, 1F);
				
		Entity clicked = event.getRightClicked();
		if (clicked.getType() != EntityType.PRIMED_TNT) {
			return;
		}
		
		TNTPrimed tnt = (TNTPrimed) clicked;
		int fuseTicks = tnt.getFuseTicks();
		
		double secondsToSpare = UtilMath.round(UtilTime.toSeconds(fuseTicks), 2);
		
		UtilSound.playPhysical(clicked.getLocation(), Sound.FIZZ, 1F, 2F);
		UtilEffect.play(clicked.getLocation(), Effect.STEP_SOUND, Material.TNT);
		clicked.remove();
		
		C.pMain(event.getPlayer(), "Defuse", "TNT defused with " + C.fElem(secondsToSpare + "s") + " to spare");
	}
	
}
