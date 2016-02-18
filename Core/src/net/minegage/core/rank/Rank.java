package net.minegage.core.rank;


import net.minegage.common.C;
import org.bukkit.entity.Player;


public enum Rank {


	// pro hero elite ultra mvp
	DEFAULT("default", null, C.rDefault),
	
	PRO("pro", C.rPro + C.cBold + "Pro", C.rPro),
	ACE("ace", C.rAce + C.cBold + "Ace", C.rAce),
	MVP("mvp", C.rMvp + C.cBold + "MVP", C.rMvp),
	
	YOUTUBE("youtube", C.cBold + "You" + C.rYoutube2 + C.cBold + "Tube", C.rYoutubeName),
	
	BUILDER("builder", C.rBuilder + C.cBold + "Builder", C.rBuilder),
	MODERATOR("moderator", C.rMod + C.cBold + "Mod", C.rMod),
	ADMIN("admin", C.rAdmin + C.cBold + "Admin", C.rAdmin),
	DEVELOPER("developer", C.rDev + C.cBold + "Dev", C.rDev),
	OWNER("owner", C.rOwner + C.cBold + "Owner", C.rOwner);

	public static Rank highest(Rank... ranks) {
		if (ranks.length == 0) {
			return null;
		}

		if (ranks.length == 1) {
			return ranks[0];
		}

		Rank highest = ranks[0];
		for (Rank rank : ranks) {
			if (!highest.includes(rank)) {
				highest = rank;
			}
		}

		return highest;
	}

	public static Rank lowest(Rank... ranks) {
		if (ranks.length == 0) {
			return null;
		}

		if (ranks.length == 1) {
			return ranks[0];
		}

		Rank lowest = ranks[0];
		for (Rank rank : ranks) {
			if (rank.includes(lowest)) {
				lowest = rank;
			}
		}

		return lowest;
	}

	public static Rank get(Player player) {
		return RankManager.instance.getRank(player);
	}

	private final String permName;
	private final String displayName;
	private String color;
	
	private Rank(String permName, String displayName, String color) {
		this.permName = permName;
		this.displayName = displayName;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getPermName() {
		return permName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getTeamPrefix() {
		if (displayName != null) {
			return displayName + " " + C.cReset;
		} else {
			return color + C.cReset;
		}
	}
	
	public String getChatPrefix() {
		if (displayName != null) {
			return displayName + " " + color;
		} else {
			return color;
		}
	}
	
	public String getTeamName() {
		return name();
	}

	/*
	self - other
	(self - other) >= 0

	self - self = 0



	 */

	/**
	 *
	 * @param rank The rank to compare
	 * @return True if this rank has an equal or lower weight than the other rank
	 * <p>
	 *
	 * </p>
	 */
	public boolean includes(Rank rank) {
		return compareTo(rank) >= 0;
	}


}
