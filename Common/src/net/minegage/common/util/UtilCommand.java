package net.minegage.common.util;


import net.minegage.common.C;
import net.minegage.common.data.Data;
import org.bukkit.entity.Player;


public class UtilCommand {
	
	public static boolean failIfTrue(boolean condition, Player player, String head, String message) {
		if (condition) {
			C.pMain(player, head, message);
			return true;
		}
		
		return false;
	}
	
	public static boolean isNull(Object object, Player player, String head, String message) {
		return failIfTrue(object == null, player, head, message);
	}
	
	public static boolean notInstance(Object obj, Class<?> b, Player player, String head, String message) {
		return failIfTrue(!b.isInstance(obj), player, head, message);
	}
	
	public static <E extends Enum<E>> boolean notEnum(Class<E> enumClass, String name, Player player, String head, String message) {
		return failIfTrue(!UtilJava.isEnum(enumClass, name), player, head, message);
	}
	
	@Deprecated
	public static <E extends Enum<E>> boolean failedEnumParse(E enumerator, Class<E> enumClass, String name, Player player, String head, String message) {
		enumerator = UtilJava.parseEnum(enumClass, name);
		return failIfTrue(enumerator == null, player, head, message);
	}
	
	public static <T extends Data<U>, U> boolean failedParse(T data, String unparsed, Player player, String head, String message) {
		boolean failedParse = false;
		try {
			data.setData(data.parse(unparsed));
		} catch (Exception ex) {
			failedParse = true;
		}
		
		return failIfTrue(failedParse, player, head, message);
	}
}
