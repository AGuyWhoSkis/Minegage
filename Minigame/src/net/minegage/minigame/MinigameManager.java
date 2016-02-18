package net.minegage.minigame;


import net.minegage.common.module.PluginModule;
import net.minegage.common.block.BlockManager;
import net.minegage.common.block.ExplosionManager;
import net.minegage.core.combat.CombatManager;
import net.minegage.core.equippable.EquipManager;
import net.minegage.core.move.AFKManager;
import net.minegage.core.stats.StatManager;
import net.minegage.minigame.command.CommandDebug;
import net.minegage.minigame.command.CommandDie;
import net.minegage.minigame.command.CommandStat;
import net.minegage.minigame.command.game.CommandGame;
import net.minegage.minigame.command.state.CommandState;
import net.minegage.minigame.event.GameEventManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.item.ItemManager;
import net.minegage.minigame.lobby.LobbyManager;
import net.minegage.minigame.map.MapManager;


public class MinigameManager
		extends PluginModule {
		
	private PlayerManager playerManager;
	private ItemManager itemManager;

	private StatManager statManager;
	private MapManager mapManager;
	
	private GameManager gameManager;
	private GameEventManager eventManager;
	private LobbyManager lobbyManager;
	
	private CombatManager combatManager;

	private BlockManager blockManager;
	private ExplosionManager explosionManager;

	private EquipManager equipManager;

	private AFKManager afkManager;

	public MinigameManager(Minigame minigame) {
		super("Minigame Manager", minigame);
		
		this.playerManager = new PlayerManager(this);
		this.itemManager = new ItemManager(this);

		this.statManager = new StatManager(plugin);
		this.mapManager = new MapManager(plugin);

		this.combatManager = new CombatManager(plugin);

		this.blockManager = new BlockManager(plugin);
		this.explosionManager = new ExplosionManager(blockManager);

		this.gameManager = new GameManager(this);
		this.eventManager = new GameEventManager(this);
		this.lobbyManager = new LobbyManager(gameManager);

		this.equipManager = new EquipManager(minigame);

		this.afkManager = new AFKManager(minigame.getMoveManager(), 45);

		addCommand(new CommandDebug(this));
		addCommand(new CommandDie());
		addCommand(new CommandGame(gameManager));
		addCommand(new CommandState(gameManager));
		addCommand(new CommandStat(gameManager));
	}

	public Game getGame() {
		return getGameManager().getGame();
	}
	
	public Minigame getMinigame() {
		return (Minigame) getPlugin();
	}
	
	public MapManager getMapManager() {
		return mapManager;
	}
	
	public StatManager getStatManager() {
		return statManager;
	}
	
	public GameManager getGameManager() {
		return gameManager;
	}
	
	public GameEventManager getEventManager() {
		return eventManager;
	}
	
	public LobbyManager getLobbyManager() {
		return lobbyManager;
	}

	
	public CombatManager getCombatManager() {
		return combatManager;
	}
	
	public BlockManager getBlockManager() {
		return blockManager;
	}

	public ExplosionManager getExplosionManager() {
		return explosionManager;
	}

	public ItemManager getItemManager() {
		return itemManager;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public EquipManager getEquipManager() {
		return equipManager;
	}
	
}
