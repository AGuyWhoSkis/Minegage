package net.minegage.minigame.game.games.survivalgames;


import net.minegage.common.datafile.DataFile;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;


public class GameSurvivalGames
		extends GameFFA {
		
	protected GameSurvivalGames(MinigameManager manager) {
		super(manager, GameType.SURVIVAL_GAMES, new String[] { });
	}
	
	@Override
	public void loadWorldData(DataFile worldData) {
		super.loadWorldData(worldData);
		// Load data
	}
	
	@Override
	public boolean endCheck() {
		return false;
	}
	
}
