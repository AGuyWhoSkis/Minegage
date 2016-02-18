package net.minegage.minigame.kit;


import net.minegage.common.misc.Click;
import net.minegage.common.misc.Click.ClickButton;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilSound;
import net.minegage.common.C;
import net.minegage.core.mob.MobType;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.PlayerState;
import net.minegage.minigame.kit.attrib.Attrib;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class Kit
		extends Descriptive
		implements Listener {
		
	protected Game game;
	protected Set<Attrib> attributes = new HashSet<>();
	protected MobType mobType = MobType.ZOMBIE;
	
	protected ItemStack mobHand = null;
	
	protected ItemStack mobHelm = null;
	protected ItemStack mobChest = null;
	protected ItemStack mobLegs = null;
	protected ItemStack mobBoots = null;
	
	/* Game isn't passed immediately because compiler prevents it */
	public Kit(String name, String[] description, Attrib... attributes) {
		super(name, description);
		getDescription().add("");
		addAttribute(attributes);
	}
	
	protected abstract void giveItems(PlayerInventory inv);
	
	/**
	 * Equips the items and applies attributes to the player
	 */
	public final void equip(Player player) {
		UtilPlayer.reset(player);
		
		giveItems(player.getInventory());
		
		player.updateInventory();
		
		for (Attrib attrib : attributes) {
			attrib.apply(player);
		}
	}
	
	/**
	 * Called through MobKit interaction
	 */
	public void click(Player player, Click click) {
		if (!game.canInteract(player)) {
			return;
		}
		
		if (!Timer.instance.use(player, "Kit", "Click Kit", 500L, false)) {
			return;
		}
		
		// Ignore the click if the player isn't on the right team
		if (game.teamUniqueKits && game.isPlaying()) {
			GameTeam team = game.getTeam(player);
			if (!team.getKits()
					.contains(this)) {
				return;
			}
		}
		
		sendInfo(player);
		if (click.getButton() == ClickButton.RIGHT) {
			select(player, true);
		} else {
			C.pRaw(player, C.t1 + C.sOut + "Right click to equip!");
		}
		
		UtilSound.playLocal(player, Sound.ORB_PICKUP, 1F, 1F);
	}
	
	/**
	 * Sets the kit of the player, and equips it if necessary
	 */
	public void select(Player player, boolean notify) {
		game.setKit(player, this);
		
		if (game.getState(player) == PlayerState.IN) {
			if (!game.inLobby()) {
				equip(player);
			}
			
			if (notify) {
				C.pMain(player, "Kit", "You equipped " + C.sOut + getName());
			}
			
			SelectKitEvent event = new SelectKitEvent(player, this);
			UtilEvent.call(event);
		}
	}
	
	public void sendInfo(Player player) {
		C.pRaw(player, "");
		C.pRaw(player, C.t1 + C.fGen("Kit", C.cGreen + C.cBold + getName()));
		C.pRaw(player, "");
		for (String desc : getDescription()) {
			C.pRaw(player, C.t1 + desc);
		}
		C.pRaw(player, "");
	}
	
	protected void giveMobItems(PlayerInventory inv) {
		inv.setHeldItemSlot(0);
		giveItems(inv.getHolder()
				.getEquipment());
	}
	
	/**
	 * Shortcut for setting mob items
	 */
	public void setMobItems(ItemStack[] armour) {
		this.mobBoots = armour[0];
		this.mobLegs = armour[1];
		this.mobChest = armour[2];
		this.mobHelm = armour[3];
		
		if (armour.length > 4) {
			this.mobHand = armour[4];
		}
	}
	
	protected <T extends HumanEntity> void colourTeamArmour(T player, ItemStack... armour) {
		GameTeam team = game.getTeam((Player) player);
		Color colour = team.getArmourColour();
		UtilArmour.colourArmour(colour, armour);
	}
	
	public void giveItems(EntityEquipment equipment) {
		UtilArmour.equip(equipment, new ItemStack[] { mobBoots, mobLegs, mobChest, mobHelm, mobHand });
	}
	
	public void addAttribute(Attrib... attribs) {
		for (Attrib attrib : attribs) {
			if (attrib.hasDescription()) {
				
				List<String> description = getDescription();
				description.add(attrib.getName());
				description.addAll(attrib.getDescription());
			}
			
			attrib.setKit(this);
			attributes.add(attrib);
		}
	}
	
	public MobType getMobType() {
		return mobType;
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setGame(Game game) {
		this.game = game;
		game.registerEvents(this);
		
		for (Attrib attrib : attributes) {
			game.registerEvents(attrib);
		}
	}
	
	public void dispose() {
		if (game != null) {
			game.unregisterEvents(this);
			
			for (Attrib attrib : attributes) {
				game.unregisterEvents(attrib);
			}
			
			game = null;
		}
	}
	
	
	
}
