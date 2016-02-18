package net.minegage.minigame.game;


import net.minegage.minigame.game.games.kitpvp.GameKitPVP;
import net.minegage.minigame.game.games.oitq.GameOITQ;
import net.minegage.minigame.game.games.paintball.GamePaintball;
import net.minegage.minigame.game.games.riot.GameRiot;
import net.minegage.minigame.game.games.skywars.GameSkywars;
import net.minegage.minigame.game.games.spleef.GameSpleef;
import net.minegage.minigame.game.games.spleefrun.GameSpleefRun;
import net.minegage.minigame.game.games.survivalgames.GameSurvivalGames;
import net.minegage.minigame.game.games.xpwars.GameXPWars;


public enum GameType {
	
	XP_WARS("XP Wars", "XPW", GameXPWars.class),
	ONE_IN_THE_QUIVER("One in the Quiver", "OITQ", GameOITQ.class),
	SKYWARS("Skywars", "SKY", GameSkywars.class),
	SURVIVAL_GAMES("Survival Games", "SG", GameSurvivalGames.class),
	KIT_PVP("Kit PVP", "KIT", GameKitPVP.class),
	SPLEEF("Spleef", "SPL", GameSpleef.class),
	SPLEEF_RUN("Spleef Run", "RUN", GameSpleefRun.class),
	RIOT("Riot", "RIOT", GameRiot.class),
	PAINTBALL("Paintball", "PNT", GamePaintball.class),
	
	;
	private String nick;
	private String name;
	private Class<? extends Game> clazz;
	
	GameType(String name, String nick, Class<? extends Game> clazz) {
		this.name = name;
		this.nick = nick;
		this.clazz = clazz;
	}
	
	public String getNick() {
		return nick;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<? extends Game> getClazz() {
		return clazz;
	}
	
}
