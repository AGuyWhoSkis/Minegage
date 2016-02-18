package net.minegage.minigame.game;


import net.minegage.common.C;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.event.PlayerStateChangeEvent;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.stats.Stat;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.winnable.PlayerComparator;
import net.minegage.minigame.winnable.Winnable;
import net.minegage.minigame.winnable.WinnablePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class GameFFA
		extends Game {
		
	/* Index corresponds to place in leaderboard */
	protected LinkedList<Player> places = new LinkedList<>();

	protected PlayerComparator winnerComparator;

	public GameFFA(MinigameManager manager, GameType type, String[] description, Kit... kits) {
		super(manager, type, description, kits);

		this.winnerComparator = new PlayerComparator(getStatTracker(), Stat.KILLS);
		this.damagePlayerVsSelfTeam = true;
	}
	
	@Override
	public void createTeams() {
		createTeam("Players", C.cYellow);
	}
	
	@Override
	public void assignTeam(Player player) {
		getTeam().addPlayer(player);
	}
	
	@EventHandler
	public void setPlace(PlayerStateChangeEvent event) {
		// Null prev state means the player has just joined
		if (event.getPrevState() != null && event.getNewState() == PlayerState.OUT) {
			places.addFirst(event.getPlayer());
		}
	}
	
	@Override
	public void clearReference(Player player) {
		super.clearReference(player);
		places.remove(player);
	}
	
	@Override
	public boolean endCheck() {
		return getPlayers(PlayerState.IN).size() < 2;
	}
	
	@Override
	public void end() {
		super.end();
	}
	
	/* Common end checks */
	
	/**
	 * @param state
	 *        The {@link PlayerState} to check for
	 * @param players
	 *        The number of players with the specified {@link PlayerState} which should result in
	 *        the game ending
	 * @return If the game should end
	 */
	protected boolean endCheckState(PlayerState state, int players) {
		return getPlayers(state).size() <= players;
	}
	
	protected boolean endCheckTime(int ticks) {
		return getStateTicks() >= ticks;
	}
	
	public GameTeam getTeam() {
		return getTeams().get(0);
	}
	
	@Override
	protected List<Winnable<?>> getWinnerPlaces() {
		if (deathOut) {
			// There can only be one
			List<Player> alive = getPlayersIn();
			if (alive.size() > 1) {
				places.clear();
			} else if (alive.size() == 1) {
				places.addFirst(alive.get(0));
			}
			
			return places.stream()
					.map(pl -> new WinnablePlayer(pl))
					.collect(Collectors.toList());
		} else {
			List<Player> alive = getPlayersIn();
			alive.sort(winnerComparator);
			
			return alive.stream()
					.map(player -> new WinnablePlayer(player))
					.collect(Collectors.toList());
		}
	}
	
}
