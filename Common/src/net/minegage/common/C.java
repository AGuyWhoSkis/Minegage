package net.minegage.common;

import com.google.common.collect.Lists;
import net.minegage.common.log.L;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilRegex;
import net.minegage.common.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

public class C {

	public static final String COLOR_CHAR = String.valueOf(ChatColor.COLOR_CHAR);

	/* Colour strings */
	public static final String cBlack = ChatColor.BLACK.toString();             // 0
	public static final String cBlueD = ChatColor.DARK_BLUE.toString();         // 1
	public static final String cGreenD = ChatColor.DARK_GREEN.toString();       // 2
	public static final String cAquaD = ChatColor.DARK_AQUA.toString();         // 3
	public static final String cRedD = ChatColor.DARK_RED.toString();           // 4
	public static final String cPurple = ChatColor.DARK_PURPLE.toString();      // 5
	public static final String cGold = ChatColor.GOLD.toString();               // 6
	public static final String cGray = ChatColor.GRAY.toString();               // 7
	public static final String cGrayD = ChatColor.DARK_GRAY.toString();         // 8
	public static final String cBlue = ChatColor.BLUE.toString();               // 9
	public static final String cGreen = ChatColor.GREEN.toString();             // a
	public static final String cAqua = ChatColor.AQUA.toString();               // b
	public static final String cRed = ChatColor.RED.toString();                 // c
	public static final String cPink = ChatColor.LIGHT_PURPLE.toString();       // d
	public static final String cYellow = ChatColor.YELLOW.toString();           // e
	public static final String cWhite = ChatColor.WHITE.toString();             // f

	/* Format strings */
	public static final String cScram = ChatColor.MAGIC.toString();             // k
	public static final String cBold = ChatColor.BOLD.toString();               // l
	public static final String cStrike = ChatColor.STRIKETHROUGH.toString();    // m
	public static final String cLine = ChatColor.UNDERLINE.toString();          // n
	public static final String cItalics = ChatColor.ITALIC.toString();          // o

	public static final String cReset = ChatColor.RESET.toString();             // r

	/* Styling */
	public static String sThemeA = cAquaD;
	public static String sThemeB = cWhite;

	public static String sDash = "\u2014";
	public static String sSep = ") ";

	public static String sHead = C.cBold + sThemeA;
	public static String sBody = cWhite;

	public static String sOut = cYellow;
	public static String sOut2 = cPink;

	public static String sItemName = C.cAquaD;
	public static String sItemUsage = C.cGray;
	public static String sItemBody = C.cGray;
	public static String sItemOut1 = C.cGreen;
	public static String sItemOut2 = C.cYellow;

	public static String shHead = cGold;
	public static String shDesc = cWhite;

	public static String sErrHead = cRed + cBold;
	public static String sErrBody = cGold;

	public static String sWarnHead = cRed + cBold;
	public static String sWarnBody = cGold;

	/* Inventory styling */
	public static String iMain = cWhite;

	public static String iOut = cGreen;
	public static String iOut2 = cYellow;
	
	/* Rank styling */

	public static String rDefault = cGray;

	public static String rPro = cYellow;
	public static String rAce = cGreen;
	public static String rMvp = cPink;

	public static String rYoutube1 = cWhite;
	public static String rYoutube2 = cRedD;
	public static String rYoutubeName = cRed;

	public static String rBuilder = cGold;
	public static String rMod = cAqua;

	public static String rAdmin = cRed;
	public static String rDev = cRedD;
	public static String rOwner = cGreenD;

	/* Tabs/indents */
	public static final String t1 = "  ";
	public static final String t2 = "    ";
	public static final String t3 = "      ";
	public static final String t4 = "        ";

	
	/* Formatting */

	public static String fElem(String elem) {
		return C.sOut + elem + C.sBody;
	}

	public static String fElem2(String elem) {
		return C.sOut2 + elem + C.sBody;
	}

	public static String fGen(String head, String body) {
		return head + C.sSep + body;
	}
	
	public static String fItem(String name, String clickUsage) {
		return C.sItemName + name + C.cWhite + " " + C.sItemUsage + "(" + clickUsage + ")";
	}

	public static String fMain(String head, String body) {
		return fGen(sHead + head, sBody + body);
	}

	public static String fWarn(String head, String body) {
		return fGen(sWarnHead + cBold + head, sWarnBody + body);
	}

	public static String fErr(String head, String body) {
		return fGen(C.sErrHead + head, C.sErrBody + body);
	}

	public static String fOut(String out) {
		return C.sOut + out;
	}

	public static String fOut2(String out2) {
		return C.sOut2 + out2;
	}
	
	/* Messaging  */

	public static void pRaw(CommandSender sender, String message) {
		sender.sendMessage(message);
	}

	public static void pGeneral(CommandSender sender, String head, String body) {
		pRaw(sender, fGen(head, body));
	}

	public static void pMain(CommandSender sender, String head, String body) {
		pRaw(sender, fMain(head, body));
	}

	public static void pErr(Exception ex, CommandSender sender, String unableTo) {
		pRaw(sender, C.fGen(C.sErrHead + "Error", unableTo + "; A " + ex.getClass()
				.getSimpleName() + " occurred"));
		L.error(ex, unableTo);
	}

	public static void pWarn(CommandSender sender, String head, String message) {
		pRaw(sender, C.fWarn(head, message));
	}

	/* Command help */
	public static void pHelp(CommandSender sender, String command, String desc) {
		pRaw(sender, C.shHead + "/" + command + cReset + ": " + C.shDesc + desc);
	}

	public static void pHelp(Player player, String command) {
		pRaw(player, C.shHead + "/" + command);
	}
	
	
	/* Broadcast */
	public static void bRaw(String message) {
		UtilServer.broadcast(message);
	}

	public static void bMain(String head, String message) {
		bRaw(fMain(head, message));
	}

	public static void bGeneral(String head, String message) {
		bRaw(fGen(head, message));
	}

	public static void bWarn(String head, String message) {
		bRaw(fWarn(head, message));
	}

	/* Rainbow list */

	private static List<String> rainbow;

	static {
		rainbow = Lists.newArrayList();
		rainbow.add(cRedD);
		rainbow.add(cRed);
		rainbow.add(cGold);
		rainbow.add(cYellow);
		rainbow.add(cGreen);
		rainbow.add(cGreenD);
		rainbow.add(cAqua);
		rainbow.add(cAquaD);
		rainbow.add(cBlue);
		rainbow.add(cBlueD);
		rainbow.add(cPurple);
		rainbow.add(cPink);
	}

	public static List<String> bow() {
		return rainbow;
	}

	public static List<String> rainbow(String start) {
		List<String>     bow   = bow();
		Iterator<String> bowIt = UtilJava.wrappedIterator(bow, bow.indexOf(start));

		return UtilJava.join(bowIt);
	}

	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static String strip(String string) {
		return UtilRegex.strip(string);
	}
}
