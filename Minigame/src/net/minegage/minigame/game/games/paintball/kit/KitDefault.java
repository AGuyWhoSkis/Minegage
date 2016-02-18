package net.minegage.minigame.game.games.paintball.kit;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.C;
import net.minegage.minigame.game.games.paintball.AttribPaintballGun;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitDefault
		extends Kit {
		
	public KitDefault() {
		super("Marksman", new String[] {"+Accuracy, +Range", "-Fire rate" }, new AttribPaintballGun());
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		GameTeam team = getGame().getTeam((Player) inv.getHolder());

		
		ItemStack item = ItemBuild.create(Material.IRON_BARDING)
				.name(team.getPrefix() + C.cBold + "Paintball " + C.cReset + team.getPrefix() + "(click to shoot)")
				.item();
				
		ItemStack[] armour = UtilArmour.getArmourSet(ArmourType.LEATHER);
		colourTeamArmour(inv.getHolder(), armour);
		UtilArmour.equip(inv.getHolder(), armour);
		
		inv.addItem(item);
	}
	
}
