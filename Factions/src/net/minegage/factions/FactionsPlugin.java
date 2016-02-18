package net.minegage.factions;


import net.minegage.core.CorePlugin;


public class FactionsPlugin
		extends CorePlugin {
		
	private Factions factions;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		factions = new Factions(this);

		getSpawnManager().overrideSpawns = false;
	}
	
	public Factions getFactions() {
		return factions;
	}
	
}
