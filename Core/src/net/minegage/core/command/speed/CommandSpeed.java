package net.minegage.core.command.speed;


import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.data.DataFloat;
import net.minegage.common.util.UtilCommand;
import net.minegage.common.util.UtilPlayer;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandSpeed
		extends RankedCommand {
		
	public static final float MAX_SPEED = 10F;
	public static final float MIN_SPEED = 0F;
	
	
	public CommandSpeed() {
		super(Rank.MODERATOR, "speed");
		addFlag("all", Data.STRING);
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (UtilCommand.failIfTrue(args.size() == 0, player, "Speed", "Please specify a speed value, or reset")) {
			return;
		}
		
		String unparsed = args.get(0);
		
		String message = null;
		
		boolean fly = player.isFlying();
		boolean walk = !fly;
		
		if (unparsed.equalsIgnoreCase("reset")) {
			fly = true;
			walk = true;
			
			UtilPlayer.resetSpeed(player);
			
			message = "reset";
		} else {
			
			DataFloat speed = new DataFloat();
			if (UtilCommand.failedParse(speed, unparsed, player, "Speed", "Invalid speed value \"" + unparsed
			                                                              + "\", must be a number or \"reset\"")) {
				return;
			}
			
			float userSpeed = speed.getData();
			
			if (userSpeed < MIN_SPEED || userSpeed > MAX_SPEED) {
				C.pMain(player, "Speed", "Value \"" + userSpeed + "\" out of range; must be equal to or between " + MIN_SPEED
				                         + " and " + MAX_SPEED);
				return;
			}
			
			float defaultSpeed = ( fly ) ? 0.05F : 0.1F;
			float speedValue;
			if (userSpeed < 1f) {
				speedValue = defaultSpeed * userSpeed;
			} else {
				float ratio = ( ( userSpeed - 1 ) / 9 ) * ( 1F - defaultSpeed );
				speedValue = ratio + defaultSpeed;
			}
			
			if (flags.has("a")) {
				fly = walk = true;
			}
			
			if (fly) {
				UtilPlayer.setFlySpeed(player, speedValue);
			}
			if (walk) {
				UtilPlayer.setWalkSpeed(player, speedValue);
			}
			
			message = "set to " + speed.getData();
		}
		
		String set = null;
		if (fly == walk) {
			set = "Fly and walk";
		} else {
			set = fly ? "Fly" : "Walk";
		}
		
		C.pMain(player, "Speed", set + " speed " + message);
	}
	
}
