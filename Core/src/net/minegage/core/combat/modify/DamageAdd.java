package net.minegage.core.combat.modify;


public class DamageAdd
		extends DamageBase {
		
	public DamageAdd(double value) {
		super(value);
	}
	
	@Override
	public double modify(double damage) {
		return damage += value;
	}
	
}
