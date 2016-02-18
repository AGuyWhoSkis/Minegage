package net.minegage.core.mob.command.manager;


import net.minegage.common.data.Data;
import net.minegage.common.util.UtilCommand;
import net.minegage.common.C;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CreateToken {
	
	public Location post;
	
	protected Map<String, Data<?>> requiredProperties = new HashMap<>();
	protected Map<String, String> properties = new HashMap<>();
	
	public void addRequiredProperty(String name, Data<?> dataType) {
		requiredProperties.put(name, dataType);
	}
	
	public boolean processCommand(Player player, List<String> args) {
		this.post = player.getLocation();
		
		// Map the properties defined by player
		Map<String, String> definedProperties = new HashMap<>();
		for (String arg : args) {
			if (!arg.contains("=")) {
				continue;
			}
			
			String[] propertyValueSplit = arg.split("=", 2);
			definedProperties.put(propertyValueSplit[0], propertyValueSplit[1]);
		}
		
		// Make sure all required properties are mapped
		for (String property : requiredProperties.keySet()) {
			String value = definedProperties.getOrDefault(property, null);

			if (value == null) {
				C.pMain(player, "Mob", "Missing property " + C.fElem(property));
				return false;
			}
		}

		for (String property : definedProperties.keySet()) {
			String value = definedProperties.getOrDefault(property, null);

			Data<?> parser = requiredProperties.get(property);
			if (UtilCommand.failedParse(parser, value, player, "Mob", "Invalid property " + C.fElem("\"" + value + "\""))) {
				return false;
			}

			properties.put(property, parser.getData()
					.toString());
		}
		
		return true;
	}
	
	
}
