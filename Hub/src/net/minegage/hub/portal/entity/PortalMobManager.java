package net.minegage.hub.portal.entity;


import net.minegage.common.server.ping.PingToken;
import net.minegage.common.server.ping.ServerCounter;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.C;
import net.minegage.core.mob.MobManager;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class PortalMobManager
		extends MobManager<PortalMob> {
		
	public static final String FILE_NAME = "mobportals.txt";

	private ServerCounter counter;

	public PortalMobManager(ServerCounter counter) {
		super("Portal Manager", counter.getPlugin(), FILE_NAME);
		this.counter = counter;
	}
	
	@Override
	public PortalMob deserializeMob(UUID uid, Location post, Map<String, String> properties) {
		String server = properties.get("server");
		String name = properties.get("name");
		return new PortalMob(uid, post, server, name);
	}
	
	@Override
	public void serializeMob(PortalMob mob, Map<String, String> properties) {
		properties.put("server", mob.server);
		properties.put("name", mob.name);
	}

	@EventHandler
	public void tickUpdateCount(TickEvent event) {
		if (event.isNot(Tick.SEC_1)) {
			return;
		}

		for (PortalMob mob : getMobs()) {
			if (!mob.isEntityLoaded()) {
				continue;
			}

			PingToken token = counter.getPingToken(mob.server);
			if (token == null) {
				continue;
			}

			List<ArmorStand> stands = UtilEntity.getTags(mob.getEntity());
			if (stands.size() > 0) {
				String name = C.cAqua + token.onlinePlayers + " online";

				if (token.offline) {
					name = C.cRed + "Offline";
				}

				stands.get(0).setCustomName(name);
			}
		}
	}
}
