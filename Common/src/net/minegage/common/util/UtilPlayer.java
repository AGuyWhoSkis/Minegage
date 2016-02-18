package net.minegage.common.util;



import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerAbilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class UtilPlayer {
	
	public static void giveItem(Player player, ItemStack item, int max) {
		int current = 0;
		for (ItemStack other : player.getInventory().getContents()) {
			if (UtilItem.isType(item, other)) {
				current += other.getAmount();
			}
		}

		int add = Math.min(item.getAmount(), max - current);

		if (add <= 0) {
			return;
		}

		ItemStack copy = new ItemStack(item);
		copy.setAmount(add);

		player.getInventory().addItem(copy);
	}

	public static void consumeItem(Player player, int amount) {
		ItemStack item = player.getItemInHand();
		int stackSize = item.getAmount();
		stackSize -= amount;
		
		if (stackSize < 1) {
			int slot = player.getInventory()
					.getHeldItemSlot();
			player.getInventory()
					.clear(slot);
		} else {
			item.setAmount(stackSize);
		}
	}
	
	public static void setCollides(Player player, boolean collides) {
		player.spigot()
				.setCollidesWithEntities(collides);
	}
	
	public static boolean getCollides(Player player) {
		return player.spigot()
				.getCollidesWithEntities();
	}
	
	public static void setFlySpeed(Player player, float speed) {
		EntityHuman nmsHuman = getNmsHuman(player);
		PlayerAbilities abilities = nmsHuman.abilities;
		
		if (abilities.flySpeed != speed) {
			abilities.flySpeed = speed;
			nmsHuman.updateAbilities();
		}
	}
	
	public static void setWalkSpeed(Player player, float speed) {
		EntityHuman nmsHuman = getNmsHuman(player);
		PlayerAbilities abilities = nmsHuman.abilities;
		
		if (abilities.walkSpeed != speed) {
			abilities.walkSpeed = speed;
			nmsHuman.updateAbilities();
		}
	}
	
	public static void resetSpeed(Player player) {
		setFlySpeed(player, 0.05F);
		setWalkSpeed(player, 0.1F);
	}

	@Deprecated
	public static void resetInv(Player player) {
		UtilInv.clear(player.getInventory());
	}
	
	public static void resetProperties(Player player) {
		player.setSprinting(false);
		player.setLevel(0);
		player.setExp(0F);
		
		UtilEntity.resetHealth(player);
		resetSpeed(player);
	}
	
	public static void reset(Player player) {
		player.closeInventory();
		
		player.eject();
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGameMode(Bukkit.getDefaultGameMode());
		
		getNmsPlayer(player).reset();
		UtilInv.clear(player.getInventory());
		resetProperties(player);
	}
	
	public static void setHunger(Player player, int foodLevel, float saturation, float exhaustion) {
		player.setFoodLevel(foodLevel);
		player.setSaturation(saturation);
		player.setExhaustion(exhaustion);
	}
	
	public static void resetHunger(Player player) {
		setHunger(player, 20, 5F, 0F);
	}
	
	public static EntityPlayer getNmsPlayer(Player player) {
		return ( (CraftPlayer) player ).getHandle();
	}
	
	public static EntityHuman getNmsHuman(Player player) {
		return ( (CraftPlayer) player ).getHandle();
	}
	
	public static void sendPacket(Player player, Packet<?> packet) {
		getNmsPlayer(player).playerConnection.sendPacket(packet);
	}
	
	public static boolean isHolding(Player player, ItemStack item) {
		ItemStack holding = player.getItemInHand();
		if (holding == null) {
			return false;
		}
		
		return holding.equals(item);
	}
	
	public static boolean isHolding(Player player, Material material) {
		return UtilItem.is(player.getItemInHand(), material);
	}
	
	public static boolean isHolding(Player player, Material material, byte data) {
		return UtilItem.is(player.getItemInHand(), material, data);
	}
	
}
