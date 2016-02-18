package net.minegage.minigame.game;


import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.team.GameTeamSizeComparator;
import net.minegage.minigame.winnable.TeamComparator;
import net.minegage.minigame.winnable.Winnable;
import net.minegage.minigame.winnable.WinnableTeam;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class GameTDM
		extends Game {

	public GameTDM(MinigameManager manager, GameType type, String[] description, Kit... kits) {
		super(manager, type, description, kits);
		
		// Assume that kits will be defined by team
		if (kits.length == 0) {
			teamUniqueKits = true;
		}
		
	}
	
	private GameTeamSizeComparator comparator = new GameTeamSizeComparator();
	
	@Override
	public void assignTeam(Player player) {
		List<GameTeam> teams = getTeamsVisible();
		teams.sort(comparator);
		teams.get(0)
				.addPlayer(player);
	}
	
	@Override
	protected List<Winnable<?>> getWinnerPlaces() {
		// Cast from <GameTeam> to <?>
		return getWinners().stream()
				.map(team -> new WinnableTeam(team))
				.collect(Collectors.toList());
	}
	
	protected abstract List<GameTeam> getWinners();
	
	protected boolean endCheckSurvival() {
		int survivingTeams = 0;
		for (GameTeam team : getTeams()) {
			if (team.getPlayersIn()
					.size() > 0) {
				survivingTeams += 1;
			}
		}
		
		return survivingTeams <= 1;
	}
	
	protected List<GameTeam> getWinnersSurvival() {
		List<GameTeam> winners = new ArrayList<>();
		
		for (GameTeam team : getTeams()) {
			if (team.isAlive()) {
				winners.add(team);
			}
		}
		
		// Can't have 2 winners
		if (winners.size() > 1) {
			winners.clear();
		}
		
		return winners;
	}

	protected List<GameTeam> getWinnersStat(String stat) {
		List<GameTeam> winners = new ArrayList<>();

		for (GameTeam team : getTeamsVisible()) {
			int score = getStatTracker().get(team, stat);
			if (score > 0) {
				winners.add(team);
			}
		}

		winners.sort(new TeamComparator(getStatTracker(), stat));
		return winners;
	}
	
}
