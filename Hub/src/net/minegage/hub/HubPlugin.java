package net.minegage.hub;


import net.minegage.core.CorePlugin;


public class HubPlugin
		extends CorePlugin {
		
	private Hub hub;
	
	@Override
	public void onEnable() {
		super.onEnable();

		this.hub = new Hub(this);
	}

}
