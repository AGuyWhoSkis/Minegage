package net.minegage.minigame.team;


import net.minegage.common.java.SafeMap;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilServer;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.PlayerState;
import net.minegage.minigame.kit.Kit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemFactory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class GameTeam {
	
	protected Game game;
	protected String name;
	protected String prefix;
	protected Color armourColour = CraftItemFactory.instance()
			.getDefaultLeatherColor();
	protected List<Player> players = new ArrayList<>();
	protected List<Location> spawns = new ArrayList<>();
	
	protected List<Kit> kits = new ArrayList<>();
	public Kit defaultKit;
	protected SafeMap<Player, Kit> selectedKits = new SafeMap<>();
	protected boolean visible = true;
	
	public double respawnSeconds = 3.0D;
	
	private double spawnDistanceSquared = 625.0D; // 25 blocks
	
	public GameTeam(Game game, String name, String prefix, Kit... kits) {
		this.game = game;
		this.name = name;
		this.prefix = prefix;
		
		for (Kit kit : kits) {
			if (defaultKit == null) {
				defaultKit = kit;
			}
			
			kit.setGame(game);
			this.kits.add(kit);
		}
		
		if (defaultKit != null) {
			for (Player player : UtilServer.players()) {
				selectedKits.put(player, defaultKit);
			}
		}
	}
	
	/**
	 * @return The spawnpoint with the largest distance to the nearest player
	 */
	public Location nextSpawn() {
		/* Set of locations which fit the spawnDistanceSquared value. I.e, the squared distance from
		 * the location to the nearest player is less than spawnDistanceSquared */
		Set<Location> fit = new HashSet<>();
		
		/* Alternative location with maximum distance from nearest player, used when no locations
		 * fit the spawn distance */
		Location bestAlternative = null;
		
		/* "Best" (maximum) distance away from nearest player */
		double bestDistSquared = Double.MIN_VALUE;
		
		for (Location spawn : spawns) {
			// Find the distance to the nearest player
			
			double minDistSquared = Double.MAX_VALUE;
			
			for (Player player : game.getPlayersNotSpectating()) {
				Location playerLoc = player.getLocation();
				
				double distSquared = UtilMath.offsetSq(spawn, playerLoc);
				if (distSquared < minDistSquared) {
					minDistSquared = distSquared;
				}
			}
			
			if (minDistSquared > spawnDistanceSquared) {
				fit.add(spawn);
			}
			
			if (minDistSquared > bestDistSquared) {
				bestDistSquared = minDistSquared;
				bestAlternative = spawn;
			}
		}
		
		if (fit.size() > 0) {
			return UtilJava.getRandIndex(fit);
		} else if (bestAlternative != null) {
			return bestAlternative;
		} else if (spawns.size() > 0) {
			return UtilJava.getRandIndex(spawns);
		} else {
			return game.getSpecSpawn();
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public void setArmourColour(Color colour) {
		this.armourColour = colour;
	}
	
	public Color getArmourColour() {
		return armourColour;
	}
	
	public String getDisplayName() {
		return getPrefix() + getName();
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public List<Player> getPlayersIn() {
		return getPlayers().stream()
				.filter(pl -> game.getState(pl) == PlayerState.IN)
				.collect(Collectors.toList());
	}
	
	public List<Player> getPlayersOut() {
		return getPlayers().stream()
				.filter(pl -> game.getState(pl) == PlayerState.OUT)
				.collect(Collectors.toList());
	}
	
	public boolean isAlive() {
		return isVisible() && getPlayers().stream()
				.anyMatch(player -> ( game.getState(player) == PlayerState.IN ));
	}
	
	public List<Location> getSpawns() {
		return spawns;
	}
	
	public double getRespawnSeconds() {
		return respawnSeconds;
	}
	
	public double getSpawnDistanceSquared() {
		return spawnDistanceSquared;
	}
	
	public void setSpawnDistance(double spawnDistance) {
		spawnDistanceSquared = ( spawnDistance * spawnDistance );
	}
	
	public boolean contains(Player player) {
		return players.contains(player);
	}
	
	public void addPlayer(Player player) {
		GameTeam team = game.getTeam(player);
		if (team != null) {
			team.removePlayer(player);
		}
		
		players.add(player);
		
		PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(player, this);
		UtilEvent.call(event);
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
		selectedKits.remove(player);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!( obj instanceof GameTeam )) {
			return false;
		} else {
			GameTeam other = (GameTeam) obj;
			return this.name.equals(other.name);
		}
	}
	
	public void setKit(Player player, Kit kit) {
		selectedKits.put(player, kit);
	}
	
	public Kit getKit(Player player) {
		return selectedKits.get(player);
	}
	
	public List<Kit> getKits() {
		return kits;
	}
	
	public Kit getKit(String name) {
		return kits.stream()
				.filter(kit -> kit.getName()
						.equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
	
	public void dispose() {
		for (Kit kit : kits) {
			kit.dispose();
		}
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
