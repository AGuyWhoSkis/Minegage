package net.minegage.build.mob.builders;


import net.minegage.build.mob.MobBuilder;
import net.minegage.common.data.Data;
import net.minegage.core.mob.command.manager.CreateToken;
import net.minegage.minigame.kit.KitManager;
import org.bukkit.plugin.java.JavaPlugin;


public class KitBuilder
		extends MobBuilder {
		
	public KitBuilder(JavaPlugin plugin) {
		super(plugin, "KitMob", "mobkit", KitManager.FILE_NAME);
	}
	
	@Override
	public CreateToken createToken() {
		return new CreateToken() {
			{
				addRequiredProperty("kit", Data.STRING);
			}
		};
	}
	
}
