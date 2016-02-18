package net.minegage.core.mob;


import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;


public enum MobType {
	
	CREEPER(Creeper.class),
	SKELETON(Skeleton.class),
	SKELETON_WITHER(Skeleton.class) {
		@Override
		public LivingEntity spawn(Location location) {
			Skeleton skeleton = (Skeleton) super.spawn(location);
			skeleton.setSkeletonType(SkeletonType.WITHER);
			return skeleton;
		}
		
		@Override
		public boolean isType(Entity other) {
			if (!super.isType(other)) {
				return false;
			}
			
			Skeleton skeleton = (Skeleton) other;
			return skeleton.getSkeletonType() == SkeletonType.WITHER;
		}
	},
	
	SPIDER(Spider.class),
	GIANT(Giant.class),
	ZOMBIE(Zombie.class) {
		@Override
		public LivingEntity spawn(Location location) {
			Zombie zombie = (Zombie) super.spawn(location);
			zombie.setVillager(false);
			return zombie;
		}
		
		@Override
		public boolean isType(Entity other) {
			if (!super.isType(other)) {
				return false;
			}
			
			Zombie zombie = (Zombie) other;
			return !zombie.isVillager();
		}
	},
	ZOMBIE_VILLAGER(Zombie.class) {
		@Override
		public LivingEntity spawn(Location location) {
			Zombie zombie = (Zombie) super.spawn(location);
			zombie.setVillager(true);
			return zombie;
		}
		
		@Override
		public boolean isType(Entity other) {
			if (!super.isType(other)) {
				return false;
			}
			
			Zombie zombie = (Zombie) other;
			return zombie.isVillager();
		}
	},
	SLIME(Slime.class),
	GHAST(Ghast.class),
	PIG_ZOMBIE(PigZombie.class),
	ENDERMAN(Enderman.class),
	CAVE_SPIDER(CaveSpider.class),
	SILVERFISH(Silverfish.class),
	BLAZE(Blaze.class),
	MAGMA_CUBE(MagmaCube.class),
	ENDER_DRAGON(EnderDragon.class),
	WITHER(Wither.class),
	BAT(Bat.class),
	WITCH(Witch.class),
	ENDERMITE(Endermite.class),
	GUARDIAN(Guardian.class),
	PIG(Pig.class),
	COW(Cow.class),
	CHICKEN(Chicken.class),
	SHEEP(Sheep.class),
	SQUID(Squid.class),
	WOLF(Wolf.class),
	MUSHROOM_COW(MushroomCow.class),
	SNOWMAN(Snowman.class),
	OCELOT(Ocelot.class),
	IRON_GOLEM(IronGolem.class),
	HORSE(Horse.class),
	RABBIT(Rabbit.class),
	VILLAGER(Villager.class),
	PLAYER(Player.class);
	
	
	private EntityType type;
	private Class<? extends Entity> clazz;
	
	private MobType(Class<? extends Entity> clazz) {
		this.clazz = clazz;
		
		for (EntityType entityType : EntityType.values()) {
			if (entityType.getEntityClass()
					.equals(clazz)) {
				this.type = entityType;
				break;
			}
		}
		
		if (this.type == null) {
			throw new NullPointerException("Invalid EntityType \"" + clazz + "\"");
		}
	}
	
	public LivingEntity spawn(Location location) {
		LivingEntity entity = (LivingEntity) location.getWorld()
				.spawn(location, clazz);
				
		if (entity instanceof Ageable) {
			( (Ageable) entity ).setAdult();
		} else if (entity instanceof Zombie) {
			( (Zombie) entity ).setBaby(false);
		}
		
		if (!( entity instanceof Skeleton )) {
			entity.getEquipment()
					.clear();
		}
		
		return entity;
	}
	
	public boolean isType(Entity other) {
		return type == other.getType();
	}
	
}
