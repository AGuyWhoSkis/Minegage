package net.minegage.core.combat;


import net.minegage.core.event.EventCancellable;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;


public class KillAssistEvent
		extends EventCancellable {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private OfflinePlayer player;
	private double damageDealt;
	private double totalDamage;
	private double damagePercentage;
	private double reward = 2;
	
	public KillAssistEvent(OfflinePlayer player, double damageDealt, double totalDamage) {
		this.player = player;
		this.damageDealt = damageDealt;
		this.totalDamage = totalDamage;
		this.damagePercentage = damageDealt / totalDamage;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public double getDamageDealt() {
		return damageDealt;
	}
	
	public double getTotalDamage() {
		return totalDamage;
	}
	
	public double getDamagePercentage() {
		return damagePercentage;
	}
	
	public double getReward() {
		return reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}
	
}
