package net.minegage.core.npc.command;


import net.minegage.common.data.Data;
import net.minegage.core.mob.command.manager.CommandMobManager;
import net.minegage.core.mob.command.manager.CreateToken;
import net.minegage.core.npc.NPC;
import net.minegage.core.npc.NPCManager;


public class CommandNPC
		extends CommandMobManager<NPCManager, NPC> {
		
	public CommandNPC(NPCManager manager) {
		super(manager, "NPC");
	}
	
	@Override
	public CreateToken newToken() {
		return new NPCToken();
	}
	
	public class NPCToken
			extends CreateToken {
			
		public NPCToken() {
			addRequiredProperty("radius", Data.DOUBLE);
		}
		
	}
	
}
