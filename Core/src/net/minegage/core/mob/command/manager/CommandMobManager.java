package net.minegage.core.mob.command.manager;


import net.minegage.common.command.Flags;
import net.minegage.common.java.SafeMap;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilPos;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.mob.Mob;
import net.minegage.core.mob.MobManager;
import net.minegage.core.rank.Rank;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;


public abstract class CommandMobManager<T extends MobManager<M>, M extends Mob>
		extends RankedCommand
		implements Listener {
		
	protected T manager;
	protected String name;
	protected SafeMap<UUID, CreateToken> attachBuffer = new SafeMap<>();
	protected Set<UUID> unattachBuffer = new HashSet<>();
	
	public CommandMobManager(T manager, String name) {
		super(Rank.ADMIN, name);
		
		this.manager = manager;
		this.name = name;
		
		manager.registerEvents(this);
		
		addSubCommand(new CommandAttach<T, M>(this));
		addSubCommand(new CommandClear(this));
		addSubCommand(new CommandUnattach(this));
	}
	
	public abstract CreateToken newToken();
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, name + " attach [args...]", "Creates a new " + name);
		C.pHelp(player, name + " unattach", "Deletes a " + name);
		C.pHelp(player, name + " clear", "Deletes all " + name + "s in the current world");
	}
	
	@EventHandler
	public void clearReference(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		
		attachBuffer.remove(uid);
		unattachBuffer.remove(uid);
	}
	
	public void attach(Player player, LivingEntity entity, CreateToken token) {
		Map<String, String> properties = token.properties;
		try {
			properties.put("post", UtilPos.serializeLocation(token.post));
			properties.put("uid", entity.getUniqueId()
					.toString());
					
			// Serialize the mob properties into a single string
			String serialized = "";
			Iterator<Entry<String, String>> propertiesIt = properties.entrySet()
					.iterator();
			while (propertiesIt.hasNext()) {
				Entry<String, String> next = propertiesIt.next();
				String property = next.getKey();
				String value = next.getValue();
				
				serialized += property + "=" + value;
				
				if (propertiesIt.hasNext()) {
					serialized += "|";
				}
			}
			
			// Write extra line to file
			File file = manager.getFile(player.getWorld());
			file.createNewFile();
			
			List<String> lines = FileUtils.readLines(file);
			lines.add(serialized);
			
			FileUtils.writeLines(file, lines);
			
			
			// Load the mob into the mob manager
			M mob = manager.deserializeMob(entity.getUniqueId(), token.post, properties);
			manager.registerMob(mob);
			manager.loadMob(mob, entity);
			
			C.pMain(player, name, "Entity attached");
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to attach portal");
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void clickAttach(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		
		if (!attachBuffer.containsKey(uid) && !unattachBuffer.contains(uid)) {
			return;
		}
		
		Entity entity = event.getRightClicked();
		if (!( entity instanceof LivingEntity )) {
			C.pMain(player, "NPC", "The portal must be insentient");
			return;
		}
		
		LivingEntity livingEntity = (LivingEntity) entity;
		CreateToken token = attachBuffer.get(uid);
		
		if (token != null) {
			attach(player, livingEntity, token);
			attachBuffer.remove(uid);
		} else if (unattachBuffer.contains(uid)) {
			M mob = manager.getMob(livingEntity);
			
			if (mob != null) {
				try {
					manager.deleteMob(mob);
					UtilEntity.setAI(livingEntity, true);
					UtilEntity.setSilent(livingEntity, false);

					UtilEntity.removePassengers(livingEntity);
					
					C.pMain(player, name, "Entity unattached");
				} catch (IOException ex) {
					C.pErr(ex, player, "Unable to unattach enitty");
				}
			}
			
			unattachBuffer.remove(uid);
		}
		
		event.setCancelled(true);
	}
	
	public void bufferAttach(Player player, CreateToken token) {
		attachBuffer.put(player.getUniqueId(), token);
	}
	
	public void bufferUnattach(Player player) {
		unattachBuffer.add(player.getUniqueId());
	}
	
}
