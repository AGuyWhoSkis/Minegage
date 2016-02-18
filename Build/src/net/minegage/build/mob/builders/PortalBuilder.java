package net.minegage.build.mob.builders;


import net.minegage.build.mob.MobBuilder;
import net.minegage.common.data.Data;
import net.minegage.core.mob.command.manager.CreateToken;
import org.bukkit.plugin.java.JavaPlugin;


public class PortalBuilder
		extends MobBuilder {
		
	public PortalBuilder(JavaPlugin plugin) {
		super(plugin, "Portal", "mobportal", "mobportals.txt");
	}
	
	@Override
	public CreateToken createToken() {
		return new CreateToken() {
			{
				addRequiredProperty("server", Data.STRING);
				addRequiredProperty("name", Data.STRING);
			}
		};
	}
	
}
