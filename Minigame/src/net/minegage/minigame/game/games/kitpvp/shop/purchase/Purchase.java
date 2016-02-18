package net.minegage.minigame.game.games.kitpvp.shop.purchase;


import net.minegage.minigame.kit.attrib.Attrib;


public abstract class Purchase
		extends Attrib {
		
	private int cost;
	
	public Purchase(int cost, String... description) {
		super(null, description);
		this.cost = cost;
	}
	
	public int getCost() {
		return cost;
	}
	
}
