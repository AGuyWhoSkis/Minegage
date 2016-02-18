package net.minegage.hub;

import net.minegage.common.module.PluginModule;
import net.minegage.common.block.BlockManager;
import net.minegage.core.combat.CombatManager;
import net.minegage.core.combat.DeathMessenger.DeathMessageMode;
import net.minegage.core.db.DBManager;
import net.minegage.core.equippable.EquipManager;
import net.minegage.core.npc.NPCManager;
import net.minegage.hub.menu.HubMenu;
import net.minegage.hub.portal.PortalManager;

public class Hub
		extends PluginModule {

	// TODO: Queue related stuff commented out for now

	private HubPlugin hubPlugin;
	
	// public Queue queue;
	// public StatCache statCache;
	public HubManager hubManager;
	public EquipManager equipManager;
	public HubMenu hubMenu;
	public NPCManager npcManager;
	public DBManager dbManager;
	public CombatManager combatManager;
	public BlockManager blockManager;
	public PortalManager portalManager;

	public Hub(HubPlugin plugin) {
		super("Hub", plugin);

		this.hubPlugin = plugin;


		// this.queue = new Queue(this, serverManager);
		// this.statCache = new StatCache(this);
		this.hubManager = new HubManager(hubPlugin);
		this.equipManager = new EquipManager(hubPlugin);
		this.hubMenu = new HubMenu(hubPlugin, hubPlugin.getServerManager(), hubPlugin.getMenuManager(), equipManager, hubManager);
		this.npcManager = new NPCManager(hubPlugin);
		this.dbManager = new DBManager(hubPlugin);
		this.combatManager = new CombatManager(hubPlugin);
		this.combatManager.getDeathMessenger().mode = DeathMessageMode.NONE;
		this.blockManager = new BlockManager(hubPlugin);
		this.portalManager = new PortalManager(hubPlugin);
	}

	// @Override
	// public void onDisable() {
	// queue.getUpdateQueue()
	// .cancelAllTasks();
	// }

	// public Queue getQueue() {
	// return queue;
	// }
	//

	// public StatCache getStatCache() {
	// return statCache;
	// }

}
