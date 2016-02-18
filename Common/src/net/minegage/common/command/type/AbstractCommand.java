package net.minegage.common.command.type;

import net.minegage.common.C;
import net.minegage.common.command.BukkitCommandManager;
import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.data.DataEnum;
import net.minegage.common.data.DataNull;
import net.minegage.common.data.DataParseException;
import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.util.UtilJava;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

abstract class AbstractCommand<T extends CommandSender>
		extends Command {

	protected Set<AbstractCommand> subCommands = new HashSet<>();

	// Flag argument types, for automated argument parse checks
	protected SafeMap<String, Data<?>> flagTypes = new SafeMap<>();

	// Suggested flag values, for tab completing
	protected SafeMap<String, List<String>> flagValues = new SafeMap<>();

	protected AbstractCommand(String name, String... aliases) {
		super(name);

		setPermissionMessage(C.cRed + "You don't have permission to use that command!");

		List<String> aliasList = new ArrayList<>();

		for (String alias : aliases) {
			if (alias != null && alias.length() > 0) {
				aliasList.add(alias.toLowerCase());
			}
		}

		setAliases(aliasList);
	}

	/* Tab completion */

	@Override
	public List<String> tabComplete(CommandSender commandSender, String label, String[] args) {
		if (!(commandSender instanceof Player)) {
			return null;
		}

		List<String> arguments = new ArrayList<>();
		for (String str : args) {
			arguments.add(str);
		}

		String message = label + " " + UtilJava.joinArray(args, " ");

		List<String> suggestions = new ArrayList<>();
		handleTabComplete((Player) commandSender, arguments, message, suggestions);

		if (suggestions.size() == 0) {
			return null;
		}

		Collections.sort(suggestions, String.CASE_INSENSITIVE_ORDER);

		return suggestions;
	}

	public void handleTabComplete(Player player, List<String> args, String raw, List<String> suggestions) {
		String sub = "";
		if (args.size() > 0) {
			sub = args.get(0).toLowerCase();
		}

		// Recursive subcommand handling
		for (AbstractCommand command : subCommands) {
			if (command.getAliases()
					.contains(sub)) {

				args.remove(0);
				command.handleTabComplete(player, args, raw, suggestions);
			}
		}

		if (!hasPermission((T) player)) {
			return;
		}

		if (args.size() > 1) {
			String prevArg = args.get(args.size() - 2).toLowerCase();

			// Suggest flag values
			if (prevArg.startsWith("-")) {
				prevArg = prevArg.substring(1, prevArg.length());

				String currentArg = args.get(args.size() - 1).toLowerCase();

				Data<?> flagData = flagTypes.get(prevArg);
				if (flagData != null && !(flagData instanceof DataNull)) {

					for (String flagValue : flagValues.get(prevArg)) {
						if (flagValue.startsWith(currentArg)) {
							suggestions.add("-" + flagValue);
						}
					}

					return;
				}
			}
		} else if (args.size() > 0) {
			String currentArg = args.get(args.size() - 1).toLowerCase();

			if (currentArg.startsWith("-") && flagTypes.size() > 0) {
				// Suggest possible flags

				// Remove dash
				currentArg = currentArg.substring(1, currentArg.length());

				for (String flag : flagTypes.keySet()) {
					if (flag.startsWith(flag)) {
						suggestions.add("-" + flag);
					}
				}

				return;
			}

			if (subCommands.size() > 0) {
				for (AbstractCommand command : subCommands) {
					if (command.hasPermission(player)) {
						if (command.getName().startsWith(currentArg)) {
							suggestions.add(command.getName());
						} else for (String alias : command.getAliases()) {
							if (alias.startsWith(currentArg)) {
								suggestions.add(alias);
							}
						}
					}
				}
			}
		}

		try {
			onTabComplete(player, args, raw, suggestions);
		} catch (Exception ex) {
			return;
		}
	}

	public void onTabComplete(Player player, List<String> args, String raw, List<String> suggestions) {
		// Optional override
	}

	/* Command execution */

	@Override
	public boolean execute(CommandSender commandSender, String label, String[] args) {
		List<String> arguments = new ArrayList<>();
		for (String str : args) {
			arguments.add(str);
		}

		String message = label + " " + UtilJava.joinArray(args, " ");

		T castedSender = castSender(commandSender);
		if (castedSender == null) {
			String neededType;
			if (commandSender instanceof Player) {
				neededType = "as console";
			} else if (commandSender instanceof ConsoleCommandSender) {
				neededType = "as a player";
			} else {
				return true;
			}

			commandSender.sendMessage(C.cRed + "You need to send that command " + neededType + "!");
			return true;
		}

		handleCommand(castedSender, message, arguments);

		return true;
	}

	public abstract void onCommand(T sender, List<String> args, String raw, Flags flags);

	protected void handleCommand(T sender, String message, List<String> args) {
		String sub = "";
		if (args.size() > 0) {
			sub = args.get(0).toLowerCase();
		}

		// Recursive subcommand handling
		for (AbstractCommand command : subCommands) {
			if (command.getName().equalsIgnoreCase(sub) || command.getAliases().contains(sub)) {
				args.remove(0);
				command.handleCommand(sender, message, args);
				return;
			}
		}

		if (!hasPermission(sender)) {
			sender.sendMessage(getPermissionMessage());
			return;
		}

		SafeMap<String, String> definedFlags = getFlags(args);

		// Check data types against all flags
		for (Entry<String, String> entry : definedFlags.entrySet()) {
			String flag  = entry.getKey();
			String value = entry.getValue();

			Data<?> valueType = flagTypes.getOrDefault(flag, Data.STRING);

			try {
				valueType.parse(value);
			} catch (DataParseException ex) {
				C.pMain(sender, "Flag", "Invalid value \"" + value + "\" for flag \"" + flag + "\"");
				return;
			}
		}

		Flags flags = new Flags(definedFlags);

		try {
			onCommand(sender, args, message, flags);
		} catch (Exception ex) {
			C.pMain(sender, "System", "Something went wrong when running that command!");
			L.error(ex, "Unable to process command \"" + message + "\" sent by " + sender.getName());
		}
	}

	/* Flags */

	private SafeMap<String, String> getFlags(List<String> args) {
		SafeMap<String, String> ret = new SafeMap<>();

		Iterator<String> argsIt = args.iterator();

		while (argsIt.hasNext()) {
			String flag = argsIt.next();

			// Skip arguments which don't start with dashes, and skip arguments which are only
			// dashes (filter non-flags)
			if (!flag.startsWith("-") || flag.length() < 2) {
				continue;
			}

			// Remove the dash
			flag = flag.substring(1, flag.length());

			Data<?> type = flagTypes.get(flag);
			// Skip flags which aren't registered
			if (type == null) {
				continue;
			}

			String value = null;

			// Get the value, if any
			if (!type.equals(Data.NULL) && argsIt.hasNext()) {
				argsIt.remove();
				value = argsIt.next();
			}

			ret.put(flag, value);
			argsIt.remove();
		}

		return ret;
	}

	protected void addFlag(String flag, Data<?> argType, String... values) {
		flagTypes.put(flag, argType);

		if (values.length > 0) {
			List<String> valueList = new ArrayList<>();
			for (String str : values) {
				valueList.add(str);
			}

			flagValues.put(flag, valueList);
		}
	}

	protected <T extends Enum<T>> void addFlag(String flag, Class<T> enumClass, String... other) {
		int enumLength = enumClass.getEnumConstants().length;

		String[] constants = new String[enumLength + other.length];
		for (int i = 0; i < enumLength; i++) {
			constants[i] = enumClass.getEnumConstants()[i].name().toLowerCase();
		}

		for (int i = 0; i < other.length; i++) {
			constants[enumLength + i] = other[i];
		}

		addFlag(flag, new DataEnum<T>(enumClass), constants);
	}

	protected void addSubCommand(AbstractCommand command) {
		subCommands.add(command);
	}

	public BukkitCommandManager getCommandManager() {
		return BukkitCommandManager.instance;
	}

	protected abstract boolean hasPermission(T sender);

	protected abstract T castSender(CommandSender sender);

}
