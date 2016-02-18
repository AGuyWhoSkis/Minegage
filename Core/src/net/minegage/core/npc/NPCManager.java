package net.minegage.core.npc;


import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEntity;
import net.minegage.core.mob.MobManager;
import net.minegage.core.npc.command.CommandNPC;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;


public class NPCManager
		extends MobManager<NPC> {
		
	public static final String FILE_PATH = "npcs.txt";
	
	public NPCManager(JavaPlugin plugin) {
		super("NPC Manager", plugin, FILE_PATH);
		
		addCommand(new CommandNPC(this));
	}
	
	@Override
	public void serializeMob(NPC mob, Map<String, String> properties) {
		properties.put("radius", mob.radius + "");
	}
	
	@Override
	public NPC deserializeMob(UUID uid, Location post, Map<String, String> properties) {
		double radius = Double.parseDouble(properties.get("radius"));
		String name = properties.get("name");
		return new NPC(uid, post, name, radius);
	}
	
	@EventHandler
	public void returnToPost(TickEvent event) {
		if (!event.getTick()
				.equals(Tick.TICK_5)) {
			return;
		}
		
		for (NPC npc : mobs) {
			if (npc.getEntity() == null) {
				continue;
			}
			
			if (!npc.isInPost()) {
				npc.returnToPost();
				
				if (npc.failedPostChecks++ > 15) {
					npc.teleportToPost();
					npc.failedPostChecks = 0;
				}
				
			} else {
				npc.failedPostChecks = 0;
				if (npc.isReturning()) {
					UtilEntity.clearTarget(npc.getEntity());
					npc.setReturning(false);
				}
			}
		}
	}
	
}
