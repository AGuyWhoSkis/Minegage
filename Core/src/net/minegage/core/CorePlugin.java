package net.minegage.core;


import net.minegage.common.CommonPlugin;
import net.minegage.common.command.BukkitCommandManager;
import net.minegage.common.datafile.WorldDataManager;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.move.MoveManager;
import net.minegage.common.server.ServerManager;
import net.minegage.common.ticker.Ticker;
import net.minegage.core.chat.ChatManager;
import net.minegage.core.command.CoreCommandManager;
import net.minegage.core.condition.VisibilityManager;
import net.minegage.core.event.EventManager;
import net.minegage.core.spawn.SpawnManager;
import net.minegage.core.stats.StatManager;
import net.minegage.core.vault.VaultHook;


public abstract class CorePlugin
		extends CommonPlugin {

	public static CorePlugin PLUGIN;

	protected WorldDataManager worldDataManager;
	protected BukkitCommandManager commandManager;
	protected ServerManager serverManager;
	protected MoveManager moveManager;
	protected VisibilityManager visibilityManager;
	protected EventManager eventManager;
	protected SpawnManager spawnManager;
	protected VaultHook vaultHook;
	protected StatManager statManager;
	protected ChatManager chatManager;
	protected MenuManager menuManager;
	protected Ticker ticker;
	protected CoreCommandManager coreCommandManager;

	@Override
	public void onEnable() {
		super.onEnable();
		PLUGIN = this;

		this.vaultHook = new VaultHook(this);
		this.worldDataManager = new WorldDataManager(this);
		this.serverManager = new ServerManager(this);
		this.eventManager = new EventManager(this);

		this.visibilityManager = new VisibilityManager(this);
		this.moveManager = new MoveManager(this);
		this.spawnManager = new SpawnManager(this);

		this.coreCommandManager = new CoreCommandManager(this);
		this.chatManager = new ChatManager(this);

		this.statManager = new StatManager(this);
		this.menuManager = new MenuManager(this);

		this.ticker = new Ticker(this);
	}

	public BukkitCommandManager getCommandManager() {
		return commandManager;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public MoveManager getMoveManager() {
		return moveManager;
	}

	public VisibilityManager getVisibilityManager() {
		return visibilityManager;
	}

	public WorldDataManager getWorldDataManager() {
		return worldDataManager;
	}

	public VaultHook getVaultHook() {
		return vaultHook;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public SpawnManager getSpawnManager() {
		return spawnManager;
	}

	public StatManager getStatManager() {
		return statManager;
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public MenuManager getMenuManager() {
		return menuManager;
	}

	public Ticker getTicker() {
		return ticker;
	}

	public CoreCommandManager getCoreCommandManager() {
		return coreCommandManager;
	}
}
