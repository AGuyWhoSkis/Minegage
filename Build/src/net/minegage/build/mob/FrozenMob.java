package net.minegage.build.mob;


import net.minegage.common.util.UtilEntity;
import net.minegage.core.mob.Mob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;


public class FrozenMob
		extends Mob {

	private String name;

	protected FrozenMob(UUID uid, Location location, Map<String, String> properties) {
		super(uid, location);
		
		// Set the nametag of the mob to reflect the extra properties set by the player
		
		String name = properties.get("mobTypeName");
		
		Map<String, String> copy = new HashMap<>(properties);
		
		// Ignore the default properties
		copy.remove("uid");
		copy.remove("post");
		copy.remove("mobTypeName");
		
		Iterator<Entry<String, String>> propertiesIt = copy.entrySet()
				.iterator();
		
		while (propertiesIt.hasNext()) {
			Entry<String, String> entry = propertiesIt.next();
			String property = entry.getKey();
			
			String value = entry.getValue();
			
			this.tags.add(property + " = " + value);
		}

		this.tags.add(name + " Mob");
	}
	
	@Override
	public void load(LivingEntity entity) {
		super.load(entity);
		
		UtilEntity.setAI(entity, false);
		UtilEntity.setSilent(entity, true);

		entity.setFireTicks(0);
	}
	
}
