package net.minegage.minigame.kit.attrib;

import net.minegage.common.C;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilString;
import net.minegage.core.combat.event.CombatEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class AttribProjectileDeflect
		extends Attrib {

	private double chance;
	private EntityType type;

	public AttribProjectileDeflect(double chance, EntityType type) {
		super("Arrow Deflect", "You have a " + C.fElem(chance + "%") + " chance of deflecting " + UtilString.format(type.name()).toLowerCase() + "s");

		this.type = type;
		this.chance = chance;
	}

	@Override
	public void apply(Player player) {
		// Do nothing
	}

	@EventHandler
	public void deflectArrow(CombatEvent event) {
		if (event.isCancelled()) {
			return;
		}

		EntityType type = event.getDirectDamager().getType();
		if (!(event.getDirectDamager() instanceof Arrow)) {
			return;
		}

		if (!event.isPlayerDamaged()) {
			return;
		}

		Player damaged = event.getPlayerDamaged();
		if (!appliesTo(damaged)) {
			return;
		}

		if (!Rand.chance(chance)) {
			return;
		}

		event.setCancelled(true);

		Projectile proj     = (Projectile) event.getDirectDamager();
		Vector     velocity = proj.getVelocity();
		Location   loc      = proj.getLocation();

		proj.remove();
		Projectile newProj = (Projectile) loc.getWorld().spawnEntity(loc, type);
		Vector newVelocity = velocity.multiply(-0.25);
		newVelocity.setY(Math.abs(newVelocity.getY()));

		UtilSound.playPhysical(loc, Sound.ANVIL_LAND, 2F, Rand.rFloat(1.6F, 2F));
	}

}
