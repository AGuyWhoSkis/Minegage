package net.minegage.minigame.kit.attrib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;


public class AttribEffect
		extends Attrib {
		
	private List<PotionEffect> effects = new ArrayList<>();
	
	public AttribEffect(String name, String[] desc, PotionEffect... effects) {
		super(name, desc);
		this.effects = new ArrayList<>(Arrays.asList(effects));
	}
	
	public AttribEffect(String name, PotionEffect... effect) {
		this(name, new String[0], effect);
	}
	
	public List<PotionEffect> getEffect() {
		return effects;
	}
	
	public void addPotion(PotionEffect effect) {
		effects.add(effect);
	}
	
	@Override
	public void apply(Player player) {
		for (PotionEffect effect : effects) {
			effect.apply(player);
		}
	}
	
}
