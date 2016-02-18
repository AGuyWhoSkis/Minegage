package net.minegage.creative;


import net.minegage.core.CorePlugin;


public class CreativePlugin
		extends CorePlugin {
		
	private Creative creative;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		this.creative = new Creative(this);
	}
	
	public Creative getCreative() {
		return creative;
	}
	
}
