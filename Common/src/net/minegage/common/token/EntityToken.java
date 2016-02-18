package net.minegage.common.token;


import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.UUID;


public class EntityToken
		extends LocToken {
		
	public int entityID;
	public UUID entityUID;
	public String entityName;
	public String entityCustomName;
	public EntityType entityType;
	
	public EntityToken(Entity entity) {
		super(entity.getLocation());
		
		this.entityID = entity.getEntityId();
		this.entityUID = entity.getUniqueId();
		this.entityName = entity.getName();
		this.entityType = entity.getType();
		
		this.entityCustomName = entity.getCustomName();
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (player.getScoreboard() != null) {
				for (Team team : player.getScoreboard()
						.getTeams()) {
						
					if (team.getEntries()
							.contains(player.getName())) {
						this.entityCustomName = team.getPrefix() + player.getName();
					}
				}
			}
		}
		
	}
	
	public Entity getEntity() {
		
		// Search all worlds because the portal can be teleported between them
		for (World world : Bukkit.getWorlds()) {
			Entity entity = world.getEntities()
					.stream()
					.filter(e -> e.getUniqueId()
							.equals(entityUID))
					.findFirst()
					.orElse(null);
					
			if (entity != null) {
				return entity;
			}
		}
		
		return null;
	}
	
	public boolean isPlayer() {
		return entityType == EntityType.PLAYER;
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(entityUID);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!( obj instanceof EntityToken )) {
			return false;
		}
		
		EntityToken other = (EntityToken) obj;
		return this.entityUID.equals(other.entityUID);
	}
	
}
