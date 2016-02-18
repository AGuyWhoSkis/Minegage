package net.minegage.common.util;


import net.minegage.common.token.SoundToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class UtilSound {
	
	/**
	 * Plays a sound to only one player. The location of the sound is the player location.
	 */
	public static void playLocal(Player player, Sound sound, float volume, float pitch) {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
	
	public static void playLocal(Player player, SoundToken token) {
		playLocal(player, token.getSound(), token.getVolume(), token.getPitch());
	}
	
	/**
	 * Plays a sound only audible to the specified player.
	 */
	public static void playGlobal(Sound sound, float volume, float pitch) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			playLocal(player, sound, volume, pitch);
		}
	}
	
	public static void playGlobal(SoundToken token) {
		playGlobal(token.getSound(), token.getVolume(), token.getPitch());
	}
	
	/**
	 * Plays a physical sound
	 */
	public static void playPhysical(Location location, Sound sound, float volume, float pitch) {
		location.getWorld()
				.playSound(location, sound, volume, pitch);
	}
	
	public static void playPhysical(Location location, SoundToken token) {
		playPhysical(location, token.getSound(), token.getVolume(), token.getPitch());
	}
	
}
