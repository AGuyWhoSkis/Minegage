package net.minegage.build.mob.builders;


import net.minegage.build.mob.MobBuilder;
import net.minegage.common.data.Data;
import net.minegage.core.mob.command.manager.CreateToken;
import net.minegage.core.npc.NPCManager;
import org.bukkit.plugin.java.JavaPlugin;


public class NPCBuilder
		extends MobBuilder {
		
	public NPCBuilder(JavaPlugin plugin) {
		super(plugin, "NPC", "npc", NPCManager.FILE_PATH);
	}
	
	@Override
	public CreateToken createToken() {
		return new CreateToken() {
			{
				addRequiredProperty("name", Data.STRING);
				addRequiredProperty("radius", Data.DOUBLE);
			}
		};
	}
	
	
	
}
