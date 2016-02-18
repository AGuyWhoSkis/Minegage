package net.minegage.minigame.command;


import net.minegage.common.command.Flags;
import net.minegage.common.data.DataInteger;
import net.minegage.common.util.UtilCommand;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandStat
		extends RankedCommand {
		
	private GameManager manager;
	
	public CommandStat(GameManager gameManager) {
		super(Rank.ADMIN, "stat");
		this.manager = gameManager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		Game game = manager.getGame();
		if (game == null) {
			C.pWarn(player, "Stat", "A game must be running");
			return;
		}
		
		if (args.size() < 1) {
			C.pMain(player, "Stat", "Please specify a stat type - " + C.fElem("player/team"));
			return;
		}
		
		String type = args.get(0);
		
		if (!type.equalsIgnoreCase("player") && !type.equalsIgnoreCase("team")) {
			C.pMain(player, "Stat", "Invalid stat type; must be \"player\" or \"team\"");
			return;
		}
		
		String target;
		if (args.size() < 2) {
			C.pMain(player, "Stat", "Please specify a target (team/player)");
			return;
		}
		
		target = args.get(1);
		
		if (( type.equalsIgnoreCase("team") && game.getTeam(target) == null ) || ( type.equalsIgnoreCase("player") && Bukkit
				.getPlayer(target) == null )) {
			C.pMain(player, "Stat", "Target not found");
			return;
		}
		
		if (args.size() < 3) {
			C.pMain(player, "Stat", "Please specify a stat");
			return;
		}
		
		String stat = args.get(2);
		
		if (args.size() < 4) {
			C.pMain(player, "Stat", "Please specify a value");
			return;
		}
		
		DataInteger value = new DataInteger();
		if (UtilCommand.failedParse(value, args.get(3), player, "Stat", "Invalid value, \"" + args.get(3) + "\"")) {
			return;
		}
		
		int val = value.getData();
		
		if (type.equalsIgnoreCase("player")) {
			Player targetPlayer = Bukkit.getPlayer(target);
			game.getStatTracker()
					.set(targetPlayer, stat, val);
		} else if (type.equalsIgnoreCase("team")) {
			GameTeam targetTeam = game.getTeam(target);
			game.getStatTracker()
					.set(targetTeam, stat, val);
		}
		
		C.pMain(player, "Stat", "Set stat " + C.fElem(stat) + " to " + C.fElem(val + "") + " for " + type.toLowerCase() + " " + C
				.fElem(target));
	}
	
}
