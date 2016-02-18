package net.minegage.core.npc;


import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.RandomPositionGenerator;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.util.Vector;


public class PathfinderGoalRandomStrollCustom
		extends PathfinderGoal {
		
	private Vector post;
	private double radius;
	
	private EntityCreature creature;
	
	private double x;
	private double y;
	private double z;
	
	private double speed;
	private int f;
	private boolean g;
	
	public PathfinderGoalRandomStrollCustom(EntityCreature creature, double speed, Vector post, double radius) {
		this.creature = creature;
		this.speed = speed;
		this.f = 120;
		
		this.post = post;
		this.radius = radius;
		
		super.a(1);
	}
	
	@Override
	public boolean a() {
		if (!this.g) {
			if (this.creature.bh() >= 100) {
				return false;
			}
			if (this.creature.bc()
					.nextInt(this.f) != 0) {
				return false;
			}
		}
		
		Vec3D vector3d = RandomPositionGenerator.a(this.creature, 10, 7);
		if (vector3d == null) {
			return false;
		}
		
		Vector target = new Vector(vector3d.a, vector3d.b, vector3d.c);
		
		Vector postToTarget = target.clone()
				.subtract(post);
				
		double distFromPost = postToTarget.length();
		
		if (distFromPost > radius) {
			Vector dir = postToTarget.clone()
					.normalize()
					.multiply(radius);
					
			target = post.clone()
					.add(dir);
		}
		
		this.x = target.getX();
		this.y = target.getY();
		this.z = target.getZ();
		
		this.g = false;
		return true;
	}
	
	@Override
	public boolean b() {
		return !this.creature.getNavigation()
				.m();
	}
	
	@Override
	public void c() {
		this.creature.getNavigation()
				.a(this.x, this.y, this.z, this.speed);
	}
	
	public void f() {
		this.g = true;
	}
	
	public void b(int i) {
		this.f = i;
	}
}
