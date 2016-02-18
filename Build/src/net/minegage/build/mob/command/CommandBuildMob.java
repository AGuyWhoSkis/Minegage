package net.minegage.build.mob.command;


import net.minegage.build.mob.FrozenMob;
import net.minegage.build.mob.MobBuilder;

import net.minegage.core.mob.command.manager.CommandMobManager;
import net.minegage.core.mob.command.manager.CreateToken;


public class CommandBuildMob
		extends CommandMobManager<MobBuilder, FrozenMob> {
		
	public CommandBuildMob(MobBuilder manager, String name) {
		super(manager, name);
	}
	
	@Override
	public CreateToken newToken() {
		return manager.createToken();
	}
	
}
