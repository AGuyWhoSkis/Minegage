package net.minegage.core.equippable.gadget;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourSlot;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.gadget.armour.GadgetArmour;
import net.minegage.core.equippable.gadget.armour.GadgetRainbow;
import net.minegage.core.equippable.gadget.item.GadgetFlamethrower;
import net.minegage.core.equippable.gadget.item.GadgetItem;
import net.minegage.core.equippable.gadget.item.GadgetLeap;
import net.minegage.core.equippable.gadget.item.GadgetSnowballLauncher;
import net.minegage.core.rank.Rank;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;
import java.util.UUID;


public class GadgetManager
		extends PluginModule {
		
	public boolean enabled = true;
	private EquippableManager manager;
	private SafeMap<Player, Integer> steps = new SafeMap<>();
	
	/*
	 * The difference in shade "frames" of each armour type. A higher value results in the shade
	 * changing earlier
	 */
	private final SafeMap<ArmourSlot, Integer> rainbowOffset = new SafeMap<>();
	
	public GadgetManager(EquippableManager manager) {
		super("Gadget Manager", manager);
		
		this.manager = manager;
		
		GadgetItem leap = new GadgetLeap(manager, "Leap", UtilItem.create(Material.STICK), UtilItem.getSlot(5, 2), Rank.DEFAULT);
		GadgetItem flamethrower = new GadgetFlamethrower(manager, "Flamethrower", UtilItem.create(Material.BLAZE_ROD), UtilItem
				.getSlot(6, 2), Rank.PRO);
		GadgetItem snowballLauncher = new GadgetSnowballLauncher(manager, "Snowball Launcher", UtilItem.create(Material.IRON_HOE),
		                                                         UtilItem.getSlot(7, 2), Rank.ACE);
				
		manager.addFree(leap, C.iMain + "Take to the sky!");
		manager.addDonator(flamethrower, C.iMain + "Make your enemies burst into flames!");
		manager.addDonator(snowballLauncher, C.iMain + "Unleash a barrage of snowballs!");
		
		int startRow = 1;
		
		int column = 2;
		int row = startRow;
		
		createArmourGadget("Leather Helmet", Material.LEATHER_HELMET, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Leather Chestplate", Material.LEATHER_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Leather Leggings", Material.LEATHER_LEGGINGS, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Leather Boots", Material.LEATHER_BOOTS, UtilItem.getSlot(column, row), Rank.PRO);
		
		column++;
		row = startRow;
		
		createArmourGadget("Chainmail Helmet", Material.CHAINMAIL_HELMET, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Chainmail Chestplate", Material.CHAINMAIL_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Chainmail Leggings", Material.CHAINMAIL_LEGGINGS, UtilItem.getSlot(column, row++), Rank.PRO);
		createArmourGadget("Chainmail Boots", Material.CHAINMAIL_BOOTS, UtilItem.getSlot(column, row), Rank.PRO);
		
		column++;
		row = startRow;
		
		createArmourGadget("Gold Helmet", Material.GOLD_HELMET, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Gold Chestplate", Material.GOLD_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Gold Leggings", Material.GOLD_LEGGINGS, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Gold Boots", Material.GOLD_BOOTS, UtilItem.getSlot(column, row), Rank.ACE);
		
		column++;
		row = startRow;
		
		createArmourGadget("Iron Helmet", Material.IRON_HELMET, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Iron Chestplate", Material.IRON_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Iron Leggings", Material.IRON_LEGGINGS, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourGadget("Iron Boots", Material.IRON_BOOTS, UtilItem.getSlot(column, row), Rank.ACE);
		
		column++;
		row = startRow;
		
		createArmourGadget("Diamond Helmet", Material.DIAMOND_HELMET, UtilItem.getSlot(column, row++), Rank.MVP);
		createArmourGadget("Diamond Chestplate", Material.DIAMOND_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.MVP);
		createArmourGadget("Diamond Leggings", Material.DIAMOND_LEGGINGS, UtilItem.getSlot(column, row++), Rank.MVP);
		createArmourGadget("Diamond Boots", Material.DIAMOND_BOOTS, UtilItem.getSlot(column, row), Rank.MVP);
		
		column++;
		row = startRow;
		
		createArmourRainbow("Rainbow Helmet", Material.LEATHER_HELMET, UtilItem.getSlot(column, row++), Rank.MVP);
		createArmourRainbow("Rainbow Chestplate", Material.LEATHER_CHESTPLATE, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourRainbow("Rainbow Leggings", Material.LEATHER_LEGGINGS, UtilItem.getSlot(column, row++), Rank.ACE);
		createArmourRainbow("Rainbow Boots", Material.LEATHER_BOOTS, UtilItem.getSlot(column, row), Rank.PRO);
		
		rainbowOffset.put(ArmourSlot.BOOTS, 0);
		rainbowOffset.put(ArmourSlot.LEGGINGS, 3);
		rainbowOffset.put(ArmourSlot.CHESTPLATE, 6);
		rainbowOffset.put(ArmourSlot.HELMET, 8);
	}

	private GadgetRainbow createArmourRainbow(String name, Material material, int displaySlot, Rank rank) {
		ItemStack item = UtilItem.create(material);
		
		Color colour = GadgetRainbow.colourSequence.get(0);
		UtilArmour.colourArmour(colour, item);
		
		GadgetRainbow gadget = new GadgetRainbow(manager, name, item, ArmourSlot.from(material), displaySlot, rank);
		manager.addDonator(gadget, new String[0]);
		
		return gadget;
	}
	
	private GadgetArmour createArmourGadget(String name, Material material, int displaySlot, Rank rank) {
		ItemStack item = UtilItem.create(material);
		ArmourSlot equipSlot = ArmourSlot.from(material);
		
		GadgetArmour gadget = new GadgetArmour(manager, name, item, equipSlot, displaySlot, rank);
		manager.addDonator(gadget, new String[0]);
		
		return gadget;
	}
	
	@EventHandler
	public void onGadgetUse(PlayerInteractEvent event) {
		if (!UtilEvent.isClick(event)) {
			return;
		}
		
		ItemStack item = event.getItem();
		if (item == null) {
			return;
		}
		
		Player player = event.getPlayer();
		GadgetItem gadget = manager.getEquipped(player)
				.stream()
				.filter(e -> e instanceof GadgetItem && e.getEquipItem()
						.equals(item))
				.findFirst()
				.map(e -> (GadgetItem) e)
				.orElse(null);
				
		if (gadget == null) {
			return;
		}
		
		if (enabled) {
			gadget.use(player);
		} else {
			C.pMain(player, "Gadget", "Gadgets are disabled right now!");
		}
	}
	
	@EventHandler
	public void onTick(TickEvent event) {
		if (!event.getTick()
				.equals(Tick.TICK_1)) {
			return;
		}
		
		manager.getEquippables()
				.stream()
				.filter(e -> e instanceof GadgetRainbow)
				.map(e -> (GadgetRainbow) e)
				.forEach(gadget -> {
					
					for (UUID uid : gadget.getEquipped()) {
						Player player = plugin.getServer()
								.getPlayer(uid);
								
						ItemStack item = player.getInventory()
								.getItem(gadget.getEquipSlot());
						if (item == null) {
							continue;
						}
						
						colourArmour(item, player);
						
						player.updateInventory();
					}
				});
				
		for (Entry<Player, Integer> entry : steps.entrySet()) {
			int step = entry.getValue();
			if (++step >= GadgetRainbow.colourSequence.size()) {
				step = 0;
			}
			
			entry.setValue(step);
		}
		
	}
	
	@EventHandler
	public void giveStep(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		steps.put(player, 0);
	}
	
	@EventHandler
	public void removeStep(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		steps.remove(player);
	}
	
	public void colourArmour(ItemStack item, Player player) {
		int step = getRainbowStep(player, ArmourSlot.from(item.getType()));
		UtilArmour.colourArmour(GadgetRainbow.colourSequence.get(step), item);
	}
	
	public int getRainbowStep(Player player, ArmourSlot slot) {
		int index = steps.get(player);
		index += rainbowOffset.get(slot);
		return getSafeIndex(index);
	}
	
	private int getSafeIndex(int index) {
		int max = GadgetRainbow.colourSequence.size();
		if (index > max - 1) {
			return index - max;
		} else {
			return index;
		}
	}
	
	public EquippableManager getEquippableManager() {
		return manager;
	}
	
	public SafeMap<Player, Integer> getSteps() {
		return steps;
	}
	
}
