package net.minegage.minigame;


import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilUI;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.Game.PlayerState;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;


public class PlayerManager
		extends PluginModule {
		
	private MinigameManager manager;
	
	public PlayerManager(MinigameManager manager) {
		super("Player Manager", manager);
		this.manager = manager;
	}
	
	public Game getGame() {
		return manager.getGame();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void loadWorld(WorldLoadEvent event) {
		World world = event.getWorld();
		world.setDifficulty(Difficulty.HARD);
	}
	
	@EventHandler
	public void setJoinLocation(PlayerSpawnLocationEvent event) {
		Game game = getGame();
		Player player = event.getPlayer();
		
		if (game == null || !game.inMap() || !game.canInteract(player) || game.getMap() == null) {
			event.setSpawnLocation(UtilWorld.getMainWorld()
					.getSpawnLocation());
		} else {
			event.setSpawnLocation(game.getMap()
					.getSpawnLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void joinGame(PlayerJoinEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		Player player = event.getPlayer();
		UtilPlayer.reset(player);
		
		PlayerState state;
		if (game.inLobby()) {
			state = PlayerState.IN;
		} else if (!game.joinOut) {
			state = PlayerState.IN;
			game.assignTeam(player);
		} else {
			state = PlayerState.OUT;
			game.out(player, false);
		}
		
		if (game.teamUniqueKits) {
			for (GameTeam team : game.getTeams()) {
				if (team.isVisible()) {
					team.defaultKit.select(player, false);
				}
			}
		} else {
			game.defaultKit.select(player, false);
		}
		
		game.setState(player, state);
		UtilUI.sendTabText(player, head, foot);
		
		game.respawn(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void quitGame(PlayerQuitEvent event) {
		Game game = getGame();
		if (game != null) {
			game.clearReference(event.getPlayer());
		}
	}
	
	private String head;
	private String foot = C.cBold + " Check out " + C.sOut + C.cBold + "minegage.net" + C.cWhite + C.cBold + " for shop, forums, and more! ";
	
	@EventHandler
	public void updateHead(GameStateChangeEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		if (event.getNewState() != GameState.LOADING) {
			return;
		}
		
		head = UtilUI.getServerDisplay(game.getName());
		
		for (Player player : UtilServer.players()) {
			UtilUI.sendTabText(player, head, foot);
		}
	}
	
	
}
