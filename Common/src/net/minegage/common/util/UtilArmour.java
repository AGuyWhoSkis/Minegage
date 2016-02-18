package net.minegage.common.util;


import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class UtilArmour {
	
	public static boolean isArmour(int slot) {
		for (ArmourSlot armourSlot : ArmourSlot.values()) {
			if (armourSlot.getSlot() == slot) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isArmour(Material material) {
		return ArmourType.from(material) != null && ArmourSlot.from(material) != null;
	}
	
	public static boolean isLeatherArmour(Material material) {
		ArmourSlot slot = ArmourSlot.from(material);
		
		if (slot == null) {
			return false;
		}
		
		String name = material.name();
		
		return name.contains("LEATHER_");
	}
	
	public static void colourArmour(Color colour, ItemStack... items) {
		for (ItemStack armour : items) {
			if (isLeatherArmour(armour.getType())) {
				LeatherArmorMeta meta = (LeatherArmorMeta) armour.getItemMeta();
				meta.setColor(colour);
				armour.setItemMeta(meta);
			}
		}
	}
	
	public static ItemStack[] getArmourSet(ArmourType type) {
		ItemStack[] set = new ItemStack[4];
		
		int i = 0;
		for (ArmourSlot slot : ArmourSlot.values()) {
			ItemStack item = getArmour(type, slot);
			set[i++] = item;
		}
		
		return set;
	}
	
	public static ItemStack[] getArmourSet(ArmourType type, ItemStack hand) {
		ItemStack[] set = new ItemStack[5];
		
		int i = 0;
		for (ArmourSlot slot : ArmourSlot.values()) {
			ItemStack item = getArmour(type, slot);
			set[i++] = item;
		}
		
		set[i++] = hand;
		
		return set;
	}
	
	public static ItemStack getArmour(ArmourType type, ArmourSlot slot) {
		return new ItemStack(getArmourMaterial(type, slot));
	}
	
	public static Material getArmourMaterial(ArmourType type, ArmourSlot slot) {
		String left = type.name();
		String right = slot.name();
		String materialString = left + "_" + right;
		
		return Material.valueOf(materialString);
	}
	
	public static void equipNoHand(EntityEquipment e, ItemStack[] armour) {
		e.setBoots(armour[0]);
		e.setLeggings(armour[1]);
		e.setChestplate(armour[2]);
		e.setHelmet(armour[3]);
	}
	
	public static void equip(EntityEquipment e, ItemStack[] armour) {
		equipNoHand(e, armour);
		if (armour.length > 4) {
			e.setItemInHand(armour[4]);
		}
	}
	
	public static void equip(LivingEntity entity, ItemStack[] armour) {
		equip(entity.getEquipment(), armour);
	}
	
	public static enum ArmourSlot {
		BOOTS("Boots", 36),
		LEGGINGS("Leggings", 37),
		CHESTPLATE("Chestplate", 38),
		HELMET("Helmet", 39);
		
		public static ArmourSlot from(Material material) {
			// Format is "(MATERIAL)_(SLOT)", ex: LEATHER_HELMET
			String name = material.name();
			String[] split = name.split("_");
			
			if (split.length != 2) {
				return null;
			}
			
			String slotString = split[1];
			
			return UtilJava.parseEnum(ArmourSlot.class, slotString);
		}
		
		private String name;
		private int slot;
		
		private ArmourSlot(String name, int slot) {
			this.name = name;
			this.slot = slot;
		}
		
		public int getSlot() {
			return slot;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public static enum ArmourType {
		LEATHER("Leather"),
		GOLD("Gold"),
		CHAINMAIL("Chainmail"),
		IRON("Iron"),
		DIAMOND("Diamond");
		
		public static ArmourType from(Material material) {
			// Format is "(MATERIAL)_(SLOT)", ex: LEATHER_HELMET
			String name = material.name();
			String[] split = name.split("_");
			
			if (split.length != 2) {
				return null;
			}
			
			String materialString = split[0];
			
			return UtilJava.parseEnum(ArmourType.class, materialString);
		}
		
		private String name;
		
		private ArmourType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
	
}
