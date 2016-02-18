package net.minegage.build;


import net.minegage.core.CorePlugin;


public class Build
		extends CorePlugin {
		
	private BuildManager buildManager;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		this.buildManager = new BuildManager(this);
		
		getChatManager().setFilterEnabled(false);
		getSpawnManager().overrideSpawns = false;
	}
	
	public BuildManager getBuildManager() {
		return buildManager;
	}
	
}
