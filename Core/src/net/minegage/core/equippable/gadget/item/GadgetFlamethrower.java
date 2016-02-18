package net.minegage.core.equippable.gadget.item;


import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilSound;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;


public class GadgetFlamethrower
		extends GadgetItem {
		
	private final int LENGTH = 8;
	
	public GadgetFlamethrower(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank) {
		super(manager, name, item, displaySlot, rank);
	}
	
	@Override
	public void use(Player player) {
		long interval = 5000L;
		
		if (player.hasPermission("lebronhub.gadget.flamethrower.override")) {
			interval = 0L;
		}
		
		if (Timer.instance.use(player, "Gadget", "Flamethrower", interval, true)) {
			
			BlockIterator blockIterator = new BlockIterator(player, LENGTH);
			World world = player.getWorld();
			while (blockIterator.hasNext()) {
				Block block = blockIterator.next();
				Location loc = block.getLocation();
				
				world.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1, 10000);
				world.playEffect(loc, Effect.EXPLOSION, 1, 10000);
				world.playEffect(loc, Effect.EXPLOSION_LARGE, 1, 10000);
			}
			
			UtilSound.playPhysical(player.getLocation(), Sound.ENDERDRAGON_GROWL, 0.5F, 2F);
			
		}
	}
	
}
