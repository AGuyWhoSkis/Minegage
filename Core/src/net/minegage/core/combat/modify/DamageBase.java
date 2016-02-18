package net.minegage.core.combat.modify;


public abstract class DamageBase {
	
	protected double value;
	
	public DamageBase(double value) {
		this.value = value;
	}
	
	public abstract double modify(double damage);
	
}
