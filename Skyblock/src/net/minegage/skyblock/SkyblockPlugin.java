package net.minegage.skyblock;


import net.minegage.core.CorePlugin;


public class SkyblockPlugin
		extends CorePlugin {
		
	private Skyblock skyblock;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		this.skyblock = new Skyblock(this);
	}
	
	public Skyblock getSkyblock() {
		return skyblock;
	}
	
}
