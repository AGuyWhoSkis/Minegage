package net.minegage.build.mob;


import net.minegage.build.mob.command.CommandBuildMob;
import net.minegage.core.mob.MobManager;
import net.minegage.core.mob.command.manager.CreateToken;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;


public abstract class MobBuilder
		extends MobManager<FrozenMob> {
		
	public final String mobType;
	
	public MobBuilder(JavaPlugin plugin, String mobType, String command, String filePath) {
		super(mobType + " Builder", plugin, filePath);
		
		this.mobType = mobType;
		
		addCommand(new CommandBuildMob(this, command));
	}
	
	/**
	 * 
	 * @return A CreateToken containing the appropriate required properties of the mob type.
	 */
	public abstract CreateToken createToken();
	
	@Override
	public FrozenMob deserializeMob(UUID uid, Location post, Map<String, String> properties) {
		// For use in the nametag of the frozen mob
		properties.put("mobTypeName", mobType);
		
		return new FrozenMob(uid, post, properties);
	}
	
	@Override
	public void serializeMob(FrozenMob mob, Map<String, String> properties) {
		// Do nothing
	}
	
}
