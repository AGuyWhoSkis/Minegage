package net.minegage.minigame.kit.attrib;


import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilPlayer;
import net.minegage.core.CorePlugin;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;


public class AttribThrowableTNT
		extends Attrib {
		
	private double power;
	
	public AttribThrowableTNT(double power) {
		super("Throwable TNT");
		
		this.power = power;
	}
	
	public AttribThrowableTNT() {
		this(1.0);
	}
	
	@EventHandler
	public void throwTNT(PlayerInteractEvent event) {
		if (!UtilEvent.isLeftClick(event)) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!UtilPlayer.isHolding(player, Material.TNT)) {
			return;
		}
		
		if (!appliesTo(player)) {
			return;
		}
		
		Location loc = player.getEyeLocation()
				.add(0, -0.2, 0);
				
		Vector velocity = player.getLocation()
				.getDirection()
				.multiply(power);
				
		TNTPrimed tnt = (TNTPrimed) loc.getWorld()
				.spawnEntity(loc, EntityType.PRIMED_TNT);
				
		tnt.setVelocity(velocity);
		tnt.setMetadata("shooter", new FixedMetadataValue(CorePlugin.PLUGIN, player.getUniqueId()));
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			UtilPlayer.consumeItem(player, 1);
		}
	}
	
	@Override
	public void apply(Player player) {
	
	}
	
}
