package net.minegage.common.token;


import org.bukkit.Sound;


public class SoundToken {
	
	private Sound sound;
	private float pitch;
	private float volume;
	
	public SoundToken(Sound sound, float volume, float pitch) {
		this.sound = sound;
		this.pitch = pitch;
		this.volume = volume;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public float getVolume() {
		return volume;
	}
	
}
