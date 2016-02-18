package net.minegage.core.command;


import net.minegage.common.C;
import net.minegage.common.command.type.PlayerCommand;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.entity.Player;


public abstract class RankedCommand
		extends PlayerCommand {
	
	protected Rank minRank;

	protected Rank[] include;
	
	public RankedCommand(Rank minRank, String name, String... aliases) {
		super(name, aliases);

		this.minRank = minRank;

		setPermissionMessage(C.fMain("System", C.sBody + "That command requires " + minRank.getDisplayName() + C.sBody + " rank!"));
	}

	public RankedCommand(Rank minRank, Rank[] include, String name, String... aliases) {
		this(minRank, name, aliases);

		this.include = include;
	}

	@Override
	protected boolean hasPermission(Player sender) {
		Rank rank = Rank.get(sender);

		for (Rank included : include) {
			if (included == rank) {
				return true;
			}
		}

		return RankManager.instance.hasPermission(rank, minRank);
	}
	

}
