package net.minegage.minigame.kit.attrib;


import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.kit.Descriptive;
import net.minegage.minigame.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;


/**
 * Represents an attribute of a kit; a property or ability unique to a kit. Can be a potion effect, ability, etc.
 */
public abstract class Attrib
		extends Descriptive
		implements Listener {

	protected Set<Player> applied = new HashSet<>();
	protected Kit kit;

	// If false, appliesTo() will return false if the game is explaining
	protected boolean explainUse = true;

	public Attrib(String name, String... desc) {
		super(C.cBold + name, desc);
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public abstract void apply(Player player);

	protected Game getGame() {
		return kit.getGame();
	}

	/**
	 * @return If this attribute is applicable to the player
	 */
	protected boolean appliesTo(Player player) {
		if (!isActive()) {
			return false;
		}

		Game game = getGame();

		if (!game.canInteract(player)) {
			return false;
		}

		return kit.equals(game.getKit(player));

	}

	protected boolean isActive() {
		Game game = getGame();
		if (game == null || !game.isPlaying()) {
			return false;
		}

		return !(!explainUse && game.explaining);

	}


}
