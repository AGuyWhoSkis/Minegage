package net.minegage.factions;


import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.massivecore.ps.PS;
import net.milkbowl.vault.economy.Economy;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilUI;
import net.minegage.core.board.BoardManager;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import net.minegage.core.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


public class Factions
		extends PluginModule {

	private FactionsPlugin plugin;
	private BoardManager boardManager;

	public Factions(FactionsPlugin plugin) {
		super("Factions", plugin);

		boardManager = new BoardManager(plugin);
		boardManager.setRankMode(false);

		this.plugin = plugin;
	}

	int zoneRow = -1;
	int factionPowerRow = -1;
	int balanceRow = -1;

	@EventHandler (priority = EventPriority.MONITOR)
	public void giveBoard(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Board  board  = new Board();

		ObjectiveSide side = board.setSideObjective();
		side.setHeader(UtilUI.getServerDisplay("Factions"));

		side.addRow("");
		side.addRow(C.cAqua + C.cBold + "Faction");
		zoneRow = side.addRow(getZone(player));
		side.addRow("");
		side.addRow(C.cRed + C.cBold + "Faction Power");
		factionPowerRow = side.addRow(getFactionPower(player));
		side.addRow("");
		side.addRow(C.cGreen + C.cBold + "Balance");
		balanceRow = side.addRow(getBalance(player));
		side.addRow("");
		side.addRow(C.cAqua + "minegage.net");

		boardManager.setBoard(player, board);

		// Update the prefix of the joining player for everyone else
		Faction faction = getFaction(player);
		updateName(player, faction);

		// Update the prefixes of others from the perspective of the joining player
		for (Player other : Bukkit.getOnlinePlayers()) {
			Faction otherFaction = getFaction(other);
			updateNameOf(other, otherFaction, player);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void updateMemberChange(EventFactionsMembershipChange event) {
		if (event.isCancelled()) {
			return;
		}

		MembershipChangeReason reason = event.getReason();

		Faction fac = getWilderness();
		if (reason == MembershipChangeReason.CREATE || reason == MembershipChangeReason.JOIN) {
			fac = event.getNewFaction();
		}

		updateName(event.getMPlayer().getPlayer(), fac);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void updateRelationshipChange(EventFactionsRelationChange event) {
		if (event.isCancelled()) {
			return;
		}

		for (Player player : event.getFaction().getOnlinePlayers()) {
			updateName(player, event.getFaction());
		}

		for (Player player : event.getOtherFaction().getOnlinePlayers()) {
			updateName(player, event.getOtherFaction());
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void updateNameChange(EventFactionsNameChange event) {
		if (event.isCancelled()) {
			return;
		}

		for (Player player : event.getFaction().getOnlinePlayers()) {
			updateName(player, event.getFaction());
		}
	}

	@EventHandler
	public void disableWeather(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void updateValues(TickEvent event) {
		if (event.isNot(Tick.TICK_10)) {
			return;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			ObjectiveSide side = boardManager.getBoard(player)
					.getSideObjective();

			side.updateRow(balanceRow, getBalance(player));
			side.updateRow(zoneRow, getZone(player));
			side.updateRow(factionPowerRow, getFactionPower(player));
		}
	}

	@EventHandler
	public void setRelativeChatColour(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Faction faction     = getFaction(event.getPlayer());
		String  factionName = "";
		if (!isWilderness(faction)) {
			factionName = faction.getName() + " ";
		}

		Rank rank = RankManager.instance.getRank(event.getPlayer());

		for (Player player : Bukkit.getOnlinePlayers()) {
			Faction otherFaction = getFaction(player);

			String relativeColour = getRelativeColour(faction, otherFaction);
			String message = " " + relativeColour + factionName + rank.getChatPrefix() + event.getPlayer()
					.getName() + C.cReset + " " + event.getMessage();

			player.sendMessage(message);
		}

		event.setMessage(null);
	}

	private final String COLOUR_SELF = C.cPink;
	private final String COLOUR_ALLY = C.cGreen;
	private final String COLOUR_ENEMY = C.cRed;
	private final String COLOUR_NEUTRAL = C.cAquaD;

	private void updateName(Player player, Faction faction) {
		for (Player other : Bukkit.getOnlinePlayers()) {
			updateNameOf(player, faction, other);
		}
	}

	/**
	 * Update the name of the player to the other player
	 */
	private void updateNameOf(Player p, Faction pFaction, Player to) {
		UUID   uid = p.getUniqueId();

		String fac = pFaction.getId();

		runSyncDelayed(5L, () -> {
			Player player = Bukkit.getPlayer(uid);

			if (player == null) {
				return;
			}

			Faction playerFaction = Faction.get(fac);

			if (playerFaction == null) {
				return;
			}

			String factionName = "";
			if (playerFaction != null && !isWilderness(playerFaction)) {
				factionName = playerFaction.getName() + " ";
			}

			String relativeColour = getRelativeColour(playerFaction, getFaction(to));

			Board board = boardManager.getBoard(to);
			board.setPrefix(player, relativeColour + factionName + C.cReset);

			player.setDisplayName(COLOUR_NEUTRAL + factionName + player.getName() + C.cReset);
		});
	}

	private String getRelativeColour(Faction faction1, Faction faction2) {
		if (faction1 == null || faction2 == null || isWilderness(faction1) || isWilderness(faction2)) {
			return COLOUR_NEUTRAL;
		} else if (faction1.getId()
				.equals(faction2.getId())) {
			return COLOUR_SELF;
		} else {
			Rel relationship = faction1.getRelationTo(faction2);

			if (relationship == Rel.NEUTRAL) {
				return COLOUR_NEUTRAL;
			} else if (relationship == Rel.ALLY || relationship == Rel.TRUCE) {
				return COLOUR_ALLY;
			} else if (relationship == Rel.ENEMY) {
				return COLOUR_ENEMY;
			} else {
				return COLOUR_NEUTRAL;
			}
		}
	}

	private boolean isWilderness(Faction faction) {
		return getWilderness()
				.getId()
				.equals(faction.getId());
	}

	private Faction getWilderness() {
		return FactionColl.get().getNone();
	}

	private String getZone(Player player) {
		Faction faction = BoardColl.get()
				.getFactionAt(PS.valueOf(player.getLocation()));

		return faction.getName();

	}

	private String getFactionPower(Player player) {
		MPlayer mPlayer = MPlayer.get(player);

		Faction faction = mPlayer.getFaction();
		if (faction == null || isWilderness(faction)) {
			return "None";
		}

		return faction.getPowerRounded() + "/" + faction.getPowerMaxRounded();
	}

	private String getBalance(Player player) {
		Economy eco = VaultHook.instance.getEconomy();
		double  bal = eco.getBalance(player);
		return "$" + bal;
	}

	private Faction getFaction(Player player) {
		return MPlayer.get(player)
				.getFaction();
	}

	@Override
	public JavaPlugin getPlugin() {
		return super.getPlugin();
	}

	public BoardManager getBoardManager() {
		return boardManager;
	}

}
