package net.minegage.core.command.misc;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilString;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;


public class CommandItem
		extends RankedCommand {
		
	public CommandItem() {
		super(Rank.BUILDER, "item", "what");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		ItemStack hand = player.getItemInHand();
		if (hand == null) {
			C.pMain(player, "Item", "You are not holding anything in your hand");
			return;
		}
		
		MaterialData itemData = hand.getData();
		Material type = itemData.getItemType();
		int typeId = type.getId();
		byte data = itemData.getData();
		short durability = hand.getDurability();
		short maxDurability = type.getMaxDurability();
		int amount = hand.getAmount();
		
		String itemName = UtilItem.getName(hand);
		String itemType = UtilString.format(type.name());
		
		C.pRaw(player, "");
		C.pRaw(player, "Name: " + C.sOut + itemName);
		C.pRaw(player, "Type: " + C.sOut + itemType);
		C.pRaw(player, "Type Id: " + C.sOut + typeId);
		C.pRaw(player, "Data: " + C.sOut + data);
		C.pRaw(player, "Durability: " + C.sOut + durability + "/" + maxDurability);
		C.pRaw(player, "Amount: " + C.sOut + amount);
		C.pRaw(player, "");
	}
	
}
