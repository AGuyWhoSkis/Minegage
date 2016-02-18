package net.minegage.core.equippable.gadget.item;


import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilSound;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class GadgetLeap
		extends GadgetItem {
		
	public GadgetLeap(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank) {
		super(manager, name, item, displaySlot, rank);
	}
	
	@Override
	public void use(Player player) {
		long interval = 2500L;
		
		Rank rank = RankManager.instance.getRank(player);
		
		if (rank.includes(Rank.MVP)) {
			interval = 100L;
		} else if (rank.includes(Rank.ACE)) {
			interval = 1000L;
		} else if (rank.includes(Rank.PRO)) {
			interval = 1500L;
		}
		
		if (Timer.instance.use(player, "Gadget", "Leap", interval, true)) {
			Vector newVelocity = player.getLocation()
					.getDirection()
					.multiply(1.4F);
					
			if (newVelocity.getY() < 0.5D) {
				newVelocity.setY(0.5D);
			}
			
			player.setVelocity(player.getVelocity()
					.add(new Vector(0, 0.5D, 0)));
					
			manager.runSyncDelayed(2L, new Runnable() {
				@Override
				public void run() {
					if (player == null || !player.isOnline() || player.isDead()) {
						return;
					}
					
					player.setVelocity(newVelocity);
					UtilSound.playLocal(player, Sound.ZOMBIE_INFECT, 2F, 1F);
				}
			});
			
		}
	}
	
}
