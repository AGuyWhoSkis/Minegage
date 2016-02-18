package net.minegage.minigame.kit.mob;


import net.minegage.common.util.UtilEntity;
import net.minegage.common.C;
import net.minegage.core.event.EventClickEntity;
import net.minegage.core.mob.Mob;
import net.minegage.minigame.kit.Kit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;


public class MobKit
		extends Mob {
		
	public Kit kit;
	
	public MobKit(UUID uid, Location location, Kit kit) {
		super(uid, location);

		this.kit = kit;
		this.tags.add(C.cBold + "Kit - " + C.cGreen + C.cBold + kit.getName());
	}
	
	@Override
	public void load(LivingEntity entity) {
		super.load(entity);
		
		UtilEntity.setAI(entity, false);
		UtilEntity.setSilent(entity, true);
		
		kit.giveItems(entity.getEquipment());
	}
	
	@Override
	public void onClick(EventClickEntity event) {
		Player player = event.getClicker();
		kit.click(player, event.getClick());
	}
	
}
