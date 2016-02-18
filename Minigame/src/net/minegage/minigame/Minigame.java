package net.minegage.minigame;


import net.minegage.core.CorePlugin;


public class Minigame
		extends CorePlugin {
		
	private MinigameManager minigameManager;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		this.minigameManager = new MinigameManager(this);
	}
	
	public MinigameManager getMinigameManager() {
		return minigameManager;
	}
	
	
}
