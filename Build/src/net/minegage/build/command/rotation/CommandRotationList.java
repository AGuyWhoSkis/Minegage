package net.minegage.build.command.rotation;


import com.google.common.collect.SetMultimap;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilString;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.game.GameType;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Lists game rotation for the specified gametype. If gametype is not specified, lists rotation for
 * all gametypes.
 */
public class CommandRotationList
		extends CommandModule<RotationManager> {
		
	public CommandRotationList(RotationManager manager) {
		super(manager, Rank.ADMIN, "list", "display", "show");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		GameType type = null;
		if (args.size() > 0) {
			String typeString = UtilJava.joinList(args, " ");
			type = UtilJava.parseEnum(GameType.class, typeString);
			if (type == null) {
				C.pMain(player, "Rotation", "Gametype \"" + typeString + "\" is not a valid gametype");
				return;
			}
		}
		
		C.pRaw(player, "");
		
		if (type == null) {
			for (GameType gameType : GameType.values()) {
				listRotation(player, gameType);
			}
		} else {
			listRotation(player, type);
		}
	}
	
	private SetMultimap<GameType, String> getRotation() {
		return plugin.getMapManager()
				.getMapRotation();
	}
	
	private void listRotation(Player player, GameType type) {
		String name = UtilString.format(type.getName());
		
		C.pRaw(player, C.t2 + C.cPink + UtilString.format(name) + C.cGray + " rotation");
		Set<String> maps = getRotation().get(type);
		if (maps == null || maps.isEmpty()) {
			C.pRaw(player, C.t1 + C.cRed + "No maps");
		} else {
			String mapMessage = C.cGreen;
			
			Iterator<String> mapsIt = maps.iterator();
			while (mapsIt.hasNext()) {
				mapMessage += mapsIt.next();
				if (mapsIt.hasNext()) {
					mapMessage += ", ";
				}
			}
			
			C.pRaw(player, C.t1 + mapMessage);
		}
		
		C.pRaw(player, "");
	}
	
}
