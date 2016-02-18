package net.minegage.core.vault;


import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.core.rank.RankManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;


public class VaultHook
		extends PluginModule {
		
	public static VaultHook instance;
	
	private RegisteredServiceProvider<Permission> permService;
	private RegisteredServiceProvider<Economy> econService;
	private RegisteredServiceProvider<Chat> chatService;
	
	private RankManager rankManager;
	
	public VaultHook(JavaPlugin plugin) {
		super("Vault Hook", plugin);

		instance = this;

		this.rankManager = new RankManager(plugin);
	}

	@Override
	protected void onEnable() {
		if (getServer().getPluginManager()
				    .getPlugin("Vault") == null) {
			L.severe("Vault plugin not loaded; no permissions, economy, or chat services hooked!");
			return;
		}

		ServicesManager services = getServer().getServicesManager();

		permService = services.getRegistration(Permission.class);
		econService = services.getRegistration(Economy.class);
		chatService = services.getRegistration(Chat.class);

		if (permService == null) {
			logWarn("Unable to hook permission service");
		} else {
			logInfo("Hooked permission service \"" + permService.getPlugin()
					.getName() + "\"");
		}

		if (econService == null) {
			logWarn("Unable to hook economy service");
		} else {
			logInfo("Hooked economy service \"" + econService.getPlugin()
					.getName() + "\"");
		}

		if (chatService == null) {
			logWarn("Unable to hook chat service");
		} else {
			logInfo("Hooked chat service \"" + chatService.getPlugin()
					.getName() + "\"");
		}
	}

	public Permission getPermission() {
		if (permService == null) {
			return null;
		}
		
		return permService.getProvider();
	}
	
	public net.milkbowl.vault.economy.Economy getEconomy() {
		if (econService == null) {
			return null;
		}
		
		return econService.getProvider();
	}
	
	public Chat getChat() {
		if (chatService == null) {
			return null;
		}
		
		return chatService.getProvider();
	}
	
	public RankManager getRankManager() {
		return rankManager;
	}
	
}
