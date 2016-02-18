package net.minegage.common.util;


import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;


public class UtilParticle {
	
	public static void send(Collection<? extends Player> players, Particle particleType, Vector pos, Vector off,
			int numParticles, float speed, boolean longRender) {
		WrapperPlayServerWorldParticles particle = create(particleType, pos, off, numParticles, speed, longRender);
		
		for (Player player : players) {
			particle.sendPacket(player);
		}
	}
	
	public static void send(Particle particleType, Location loc, Vector off, int numParticles, float speed,
			boolean longRender) {
		
		send(loc.getWorld().getPlayers(), particleType, loc.toVector(), off, numParticles, speed, longRender);
	}
	
	public static WrapperPlayServerWorldParticles create(Particle particleType, Vector pos, Vector off,
			int numParticles, float speed, boolean longRender) {
		
		float posX = (float) pos.getX();
		float posY = (float) pos.getY();
		float posZ = (float) pos.getZ();
		
		float offX = (float) off.getX();
		float offY = (float) off.getY();
		float offZ = (float) off.getZ();
		
		return create(particleType, posX, posY, posZ, offX, offY, offZ, numParticles, speed, longRender);
	}
	
	public static WrapperPlayServerWorldParticles create(Particle particleType, float posX, float posY, float posZ,
			float offX, float offY, float offZ, int numParticles, float speed, boolean longRender) {
		WrapperPlayServerWorldParticles particle = new WrapperPlayServerWorldParticles();
		
		particle.setX(posX);
		particle.setY(posY);
		particle.setZ(posZ);
		
		particle.setOffsetX(offX);
		particle.setOffsetY(offY);
		particle.setOffsetZ(offZ);		
		
		particle.setParticleData(speed);
		particle.setLongDistance(longRender);
		particle.setNumberOfParticles(numParticles);
		
		particle.setParticleType(particleType);
		
		return particle;
	}
	
	
}
