package net.minegage.minigame.command;


import net.minecraft.server.v1_8_R3.DamageSource;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilEntity;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandDie
		extends RankedCommand {
		
	public CommandDie() {
		super(Rank.ADMIN, "die");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		UtilEntity.damage(player, 5000, DamageSource.GENERIC);
	}
	
}
