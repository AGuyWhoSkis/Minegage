package net.minegage.common.util;


import net.minegage.common.java.SafeMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import java.util.Collection;
import java.util.Map.Entry;


public class UtilEvent {
	
	public static void sendUnknownCommand(Player player) {
		player.sendMessage(SpigotConfig.unknownCommandMessage);
	}
	
	public static void call(Event event) {
		try {
			Bukkit.getPluginManager()
					.callEvent(event);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean isClick(PlayerInteractEvent event) {
		return event.getAction() != Action.PHYSICAL;
	}
	
	public static boolean isLeftClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		return action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
	}
	
	public static boolean isRightClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
	}
	
	public static boolean isBlockClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		return action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK;
	}
	
	public static boolean isAirClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		return action == Action.RIGHT_CLICK_AIR || action == Action.LEFT_CLICK_AIR;
	}
	
	public static void orderItems(PlayerDropItemEvent event, SafeMap<Integer, ItemStack> targetSlots) {
		if (targetSlots == null || targetSlots.size() == 0) {
			return;
		}
		
		Item itemEntity = event.getItemDrop();
		ItemStack item = itemEntity.getItemStack();
		
		Player player = event.getPlayer();

		int slot = -1;
		for (Entry<Integer, ItemStack> entry : targetSlots.entrySet()) {
			if (UtilItem.is(item, entry.getValue())) {
				slot = entry.getKey();
				break;
			}
		}

		if (slot == -1) {
			return;
		}
		
		itemEntity.remove();
		player.getInventory()
				.setItem(slot, item);
				
		player.updateInventory();
	}
	
	public static void lockItem(InventoryClickEvent event, Collection<ItemStack> items) {
		if (event.isCancelled()) {
			return;
		}
		
		Inventory inventory = event.getClickedInventory();
		if (inventory == null) {
			return;
		}
		
		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}
		
		Player player = (Player) event.getWhoClicked();
		
		for (ItemStack targetItem : items) {
			if (UtilItem.is(targetItem, item)) {
				event.setCancelled(true);
				player.updateInventory();
				return;
			}
		}
	}
	
	/**
	 * If the direct damager is a Projectile, the shooter of the projectile is returned. Otherwise,
	 * the direct damager is returned.
	 */
	public static LivingEntity getIndirectDamager(EntityDamageEvent event) {
		Entity direct = getDirectDamager(event);
		
		if (direct instanceof Projectile) {
			Projectile projectile = (Projectile) direct;
			ProjectileSource source = projectile.getShooter();
			if (source instanceof LivingEntity) {
				return (LivingEntity) source;
			}
			return null;
		} else if (direct instanceof LivingEntity) {
			return (LivingEntity) direct;
		} else {
			return null;
		}
	}
	
	public static Entity getDirectDamager(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ( (EntityDamageByEntityEvent) event ).getDamager();
			
			return damager;
		} else {
			return null;
		}
	}
	
	@Deprecated
	public static ProjectileSource getShooter(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (!( damager instanceof Projectile )) {
			return null;
		}
		
		Projectile projectile = (Projectile) damager;
		ProjectileSource source = projectile.getShooter();
		
		return source;
	}
	
	public static Block getCollidedBlock(Projectile projectile) {
		Location hitLocation = projectile.getLocation();
		Vector projectileVelocity = projectile.getVelocity();
		
		// Block that was hit & block behind that in the path of the projectile
		BlockIterator iterator = new BlockIterator(hitLocation.getWorld(), hitLocation.toVector(), projectileVelocity, 0.0D, 3);
		Block hitBlock = iterator.next();
		
		while (iterator.hasNext() && hitBlock.getType()
				.equals(Material.AIR)) {
			hitBlock = iterator.next();
		}
		
		return hitBlock;
	}
	
}
