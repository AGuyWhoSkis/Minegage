package net.minegage.core.equippable.trail;


import net.minegage.common.menu.MenuManager;
import net.minegage.common.misc.Click;
import net.minegage.common.C;
import net.minegage.core.equippable.ButtonEquip;
import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.entity.Player;

import java.util.List;


public class ButtonEquipTrail
		extends ButtonEquip {
		
	private MenuManager menuManager;
	
	public ButtonEquipTrail(EquippableManager equippableManager, MenuManager menuManager, Trail trail) {
		super(equippableManager, trail, false);
		this.menuManager = menuManager;
	}
	
	@Override
	public boolean canEquip(Player player, Click click) {
		List<Equippable> allEquipped = equippableManager.getEquipped(player);
		Rank playerRank = RankManager.instance.getRank(player);
		
		int maxTrails = getMaxTrails(playerRank);
		int trails = 0;
		
		for (Equippable equippable : allEquipped) {
			if (equippable instanceof Trail) {
				/*
				 * Skip trails which are equipped; these will be unequipped. Otherwise ranks which
				 * can only equip 1 trail would not be able to use the unequip button
				 */
				if (!this.equippable.equals(equippable)) {
					trails++;
				}
			}
		}
		
		if (trails + 1 > maxTrails) {
			String plural = maxTrails == 1 ? "trail" : "trails";
			C.pMain(player, "Trail", "Sorry! " + C.cReset + playerRank.getDisplayName() + "s" + C.sBody + " can only equip "
			                         + C.sOut + maxTrails + C.sBody + " " + plural + " at a time");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void equip(Player player) {
		super.equip(player);
		
		MenuTrail menu = new MenuTrail(menuManager, equippableManager, player);
		menu.open(player);
	}
	
	private int getMaxTrails(Rank rank) {
		if (rank.includes(Rank.ADMIN)) {
			return 7;
		} else if (rank.includes(Rank.BUILDER)) {
			return 4;
		} else if (rank.includes(Rank.MVP)) {
			return 3;
		} else if (rank.includes(Rank.ACE)) {
			return 2;
		} else {
			return 1;
		}
	}
	
}
