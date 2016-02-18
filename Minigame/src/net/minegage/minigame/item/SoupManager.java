package net.minegage.minigame.item;


import net.minegage.common.module.PluginModule;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilMat;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilSound;
import net.minegage.minigame.game.Game;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

public class SoupManager
		extends PluginModule {
		
	public boolean bowlDropDelete = true;
	
	public SoupManager(Game game) {
		super("Soup Manager", game);
		game.itemDropAllow.add(UtilMat.getData(Material.BOWL));
	}
	
	@EventHandler
	public void consumeSoup(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (!UtilPlayer.isHolding(player, Material.MUSHROOM_SOUP)) {
			return;
		}
		
		player.removePotionEffect(PotionEffectType.REGENERATION);
		player.addPotionEffect(PotionEffectType.REGENERATION.createEffect(100, 3));
		UtilSound.playPhysical(player.getLocation(), Sound.EAT, 1F, Rand.rFloat(1.1F, 1.3F));
		
		PlayerInventory inv = player.getInventory();
		final int slot = inv.getHeldItemSlot();
		
		runSyncDelayed(0L, () -> {
			inv.clear(slot);
			player.updateInventory();
		});
	}
	
	@EventHandler
	public void onBowlDrop(PlayerDropItemEvent event) {
		if (!bowlDropDelete) {
			return;
		}
		
		Item drop = event.getItemDrop();
		ItemStack item = drop.getItemStack();
		
		if (item.getType() == Material.BOWL) {
			drop.remove();
		}
		
	}
	
}
