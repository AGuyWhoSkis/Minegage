package net.minegage.common.menu.button;


import net.minegage.common.misc.Click;
import net.minegage.common.token.SoundToken;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public abstract class Button {
	
	public static final SoundToken SUCCESSFUL = new SoundToken(Sound.WOOD_CLICK, 1F, 1F);
	public static final SoundToken UNSUCCESSFUL = new SoundToken(Sound.ITEM_BREAK, 1F, 0.5F);
	
	public SoundToken successful = SUCCESSFUL;
	public SoundToken unsuccessful = UNSUCCESSFUL;
	
	/**
	 * @return True if successful, false otherwise
	 */
	public abstract boolean onClick(Player player, Click click);
	
}
