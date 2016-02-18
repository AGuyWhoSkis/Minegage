package net.minegage.common.log;


import net.minegage.common.C;
import net.minegage.common.util.UtilServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;


public class L {
	
	private static Logger logger;

	public static void initialize(JavaPlugin plugin) {
		if (L.logger != null) {
			warn("Logger has been initialized twice");
		}

		L.logger = plugin.getLogger();
	}

	private static String strip(Object message) {
		return C.strip(message.toString());
	}

	public static void log(Level level, Object message) {
		L.logger.log(level, strip(message));
	}

	public static void info(Object message) {
		log(Level.INFO, message);
	}

	public static void warn(Object message) {
		log(Level.WARNING, message);
	}

	public static void severe(Object message) {
		log(Level.SEVERE, message.toString());
	}

	public static void error(Throwable throwable, Object message) {
		logger.log(Level.SEVERE, strip(message), throwable);
	}

	public static void fine(Object message) {
		log(Level.FINE, message);
	}

	public static void finer(Object message) {
		log(Level.FINER, message);
	}

	public static void finest(Object message) {
		log(Level.FINEST, message);
	}

	public static Logger getLogger() {
		return logger;
	}
	
	/**
	 * Broadcast message shortcut
	 */
	public static void d(Object object) {
		UtilServer.getServer()
				.broadcastMessage(object.toString() + "");
	}
	
}
