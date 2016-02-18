package net.minegage.hub.portal.entity;


import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.C;
import net.minegage.core.event.EventClickEntity;
import net.minegage.core.mob.Mob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;



/**
 * Mob which is used to connect to servers
 */
public class PortalMob
		extends Mob {

	public String server;
	public String name;
	
	public PortalMob(UUID uid, Location location, String server, String name) {
		super(uid, location);
		
		this.server = server;
		this.name = name;
	}
	
	@Override
	public void load(LivingEntity entity) {
		super.load(entity);

		UtilEntity.setAI(entity, false);
		UtilEntity.setSilent(entity, true);

		this.tags.add(C.cAqua + "? online");
		this.tags.add(name);
	}
	
	@Override
	public void onClick(EventClickEntity event) {
		ServerManager.instance.connect(event.getClicker(), server);
	}
	
}
