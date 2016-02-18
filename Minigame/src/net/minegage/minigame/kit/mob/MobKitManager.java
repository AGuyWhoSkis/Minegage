package net.minegage.minigame.kit.mob;


import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import net.minegage.minigame.GameManager;
import net.minegage.minigame.kit.Kit;

import net.minegage.core.mob.MobManager;


public class MobKitManager
		extends MobManager<MobKit> {
		
	public static final String FILE_NAME = "mobkits.txt";
	
	private GameManager manager;
	
	public MobKitManager(GameManager manager) {
		super("Mob Kit Manager", manager.getPlugin(), FILE_NAME);
		this.manager = manager;
	}
	
	@Override
	public void serializeMob(MobKit mob, Map<String, String> properties) {
		properties.put("kit", mob.kit.getName());
	}
	
	@Override
	public MobKit deserializeMob(UUID uid, Location post, Map<String, String> properties) {
		Kit kit = manager.getGame()
				.getKit(properties.get("kit"));
		return new MobKit(uid, post, kit);
	}
	
	
}
