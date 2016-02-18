package net.minegage.core.combat.modify;


public class DamageMult
		extends DamageBase {
		
	public DamageMult(double value) {
		super(value);
	}
	
	@Override
	public double modify(double damage) {
		return damage *= value;
	}
	
}
