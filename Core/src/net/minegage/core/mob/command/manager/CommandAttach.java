package net.minegage.core.mob.command.manager;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.mob.Mob;
import net.minegage.core.mob.MobManager;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandAttach<T extends MobManager<M>, M extends Mob>
		extends RankedCommand {
		
	protected CommandMobManager<T, M> manager;
	
	public CommandAttach(CommandMobManager<T, M> manager) {
		super(Rank.ADMIN, "attach");
		this.manager = manager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		CreateToken token = manager.newToken();
		
		boolean success = token.processCommand(player, args);
		
		if (success) {
			manager.bufferAttach(player, token);
			C.pMain(player, manager.getName(), "Right click an portal to continue");
		}
	}
	
}
