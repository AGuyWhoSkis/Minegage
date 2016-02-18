package net.minegage.minigame.kit;


import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilPos;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.block.BlockTracker;
import net.minegage.core.mob.MobType;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.kit.mob.MobKit;
import net.minegage.minigame.kit.mob.MobKitManager;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;


public class KitManager
		extends PluginModule {
		
	public static final String FILE_NAME = "mobkits.txt";
	
	private BlockTracker blockTracker = new BlockTracker();
	private GameManager gameManager;
	
	private final Vector centreKitPos = new Vector(0, 105, -20);
	private final Vector kitIterateDir = UtilPos.fromBlockFace(BlockFace.EAST);
	private final Vector facingDir = UtilPos.fromBlockFace(BlockFace.SOUTH);
	private final int spacing = 6;
	private final int teamSpacing = 8;
	
	private MobKitManager mobManager;
	
	public KitManager(GameManager gameManager) {
		super("Kit Manager", gameManager.getPlugin());
		
		this.gameManager = gameManager;
		this.mobManager = new MobKitManager(gameManager);
	}
	
	/**
	 * @param location
	 *        The middle location of the kit stand
	 */
	@SuppressWarnings("deprecation")
	public void createKitStand(Kit kit, Location location, DyeColor color) {
		
		// Bottom layer
		int faceIndex = 0;
		for (Vector vec : botLayer) {
			Location relLoc = location.clone()
					.add(vec)
					.add(0, -1, 0);
					
			double x = Math.abs(vec.getX());
			double z = Math.abs(vec.getZ());
			
			Material type;
			byte data = (byte) 0;
			
			if (x + z == 3.0) {
				// Out layer, corner block
				type = Material.QUARTZ_BLOCK;
			} else if (x + z == 2.0) {
				// Outer layer, but not corner
				
				type = Material.QUARTZ_STAIRS;
				data = UtilBlock.getStairFacing(botStairs.get(faceIndex));
			} else if (x + z == 1.0) {
				// Inner layer
				
				type = Material.WOOL;
				data = color.getWoolData();
			} else {
				throw new Error("Unresolved compilation problem; relative kit stand block locations are incorrect");
			}
			
			setBlock(relLoc, type, data);
			faceIndex++;
		}
		
		// Middle layer
		for (Vector vec : defaultLayer) {
			Location relLoc = location.clone()
					.add(vec);
					
			setBlock(relLoc, Material.STAINED_GLASS, color.getWoolData());
		}
		
		// Top layer
		for (Vector vec : defaultLayer) {
			Location relLoc = location.clone()
					.add(vec)
					.add(0, 1, 0);
			setBlock(relLoc, Material.STEP, (byte) 7);
		}
		
		Location mobLoc = location.add(0, 1.5, 0);
		
		mobLoc.setDirection(facingDir);
		MobType mobType = kit.getMobType();
		
		LivingEntity entity = mobType.spawn(location);
		MobKit       mobKit = new MobKit(entity.getUniqueId(), location, kit);
		
		mobManager.registerMob(mobKit);
		mobManager.loadMob(mobKit, entity);
	}
	
	public void buildLobbyKitStands() {
		Game game = gameManager.getGame();
		
		Location base = centreKitPos.toLocation(UtilWorld.getMainWorld());
		Vector offsetDir = kitIterateDir.clone()
				.multiply(-1);
				
		if (game.teamUniqueKits) {
			List<GameTeam> teams = new ArrayList<>(game.getTeams());
			teams.remove(game.getSpectatorTeam());
			
			int totalTeams = teams.size();
			int totalKits = teams.stream()
					.mapToInt(team -> team.getKits()
							.size())
					.sum();
					
			// Leftmost kit location
			// Subtract 2 to get total distance between the middle of the left/rightmost kits
			int totalWidth = 2 * ( totalKits - 1 ) + teamSpacing * ( totalTeams - 1 ) - 2;
			
			Location baseLoc = base.add(offsetDir.clone()
					.multiply(totalWidth / 2));
					
			Vector offsetAdd = kitIterateDir.clone();
			
			for (int teamNum = 0; teamNum < teams.size(); teamNum++) {
				GameTeam team = teams.get(teamNum);
				List<Kit> kits = team.getKits();
				
				buildKits(baseLoc.clone(), kits);
				
				// Add kit offset + team spacing to get leftmost kit location of next team
				int offsetLen = 2 * ( kits.size() - 1 );
				baseLoc.add(offsetAdd.clone()
						.multiply(offsetLen + teamSpacing));
			}
			
		} else {
			List<Kit> kits = game.getGlobalKits();
			
			double offsetLen = ( spacing / 2.0 ) * ( kits.size() - 1 );
			Vector offset = offsetDir.multiply(offsetLen);
			
			Location buildLoc = base.add(offset);
			buildKits(buildLoc, kits);
		}
	}
	
	public void buildKits(Location iterStart, List<Kit> kits) {
		for (int kitNum = 0; kitNum < kits.size(); kitNum++) {
			Vector offsetAdd = kitIterateDir.clone()
					.multiply(kitNum * spacing);
					
			Location kitLoc = iterStart.clone()
					.add(offsetAdd);
					
			createKitStand(kits.get(kitNum), kitLoc, DyeColor.BLUE);
		}
	}
	
	public void restore() {
		blockTracker.restoreAll();
		mobManager.clearMobs(UtilWorld.getMainWorld());
	}
	
	@SuppressWarnings("deprecation")
	private void setBlock(Location location, Material type, byte data) {
		blockTracker.track(location.getBlock());
		location.getBlock().setType(type);
		location.getBlock().setData(data);
	}
	
	private static List<Vector> botLayer = new ArrayList<>();
	private static List<Vector> defaultLayer = new ArrayList<>();
	
	private static List<BlockFace> botStairs = new ArrayList<>();
	
	static {
		/* 4x4 grid of relative block positions for the kit stand */
		
		// Stair blocks
		botLayer.add(new Vector(-1.5, 0, -0.5));
		botLayer.add(new Vector(-1.5, 0, +0.5));
		
		botLayer.add(new Vector(-0.5, 0, -1.5));
		botLayer.add(new Vector(+0.5, 0, -1.5));
		
		botLayer.add(new Vector(+1.5, 0, +0.5));
		botLayer.add(new Vector(+1.5, 0, -0.5));
		
		botLayer.add(new Vector(-0.5, 0, +1.5));
		botLayer.add(new Vector(+0.5, 0, +1.5));
		
		// Corner blocks
		botLayer.add(new Vector(-1.5, 0, +1.5));
		botLayer.add(new Vector(+1.5, 0, +1.5));
		botLayer.add(new Vector(+1.5, 0, -1.5));
		botLayer.add(new Vector(-1.5, 0, -1.5));
		
		// Inner blocks
		botLayer.add(new Vector(-0.5, 0, +0.5));
		botLayer.add(new Vector(-0.5, 0, -0.5));
		botLayer.add(new Vector(+0.5, 0, -0.5));
		botLayer.add(new Vector(+0.5, 0, +0.5));
		
		
		botStairs.add(BlockFace.WEST);
		botStairs.add(BlockFace.WEST);
		
		botStairs.add(BlockFace.NORTH);
		botStairs.add(BlockFace.NORTH);
		
		botStairs.add(BlockFace.EAST);
		botStairs.add(BlockFace.EAST);
		
		botStairs.add(BlockFace.SOUTH);
		botStairs.add(BlockFace.SOUTH);
		
		defaultLayer.add(new Vector(-0.5, 0, -0.5));
		defaultLayer.add(new Vector(-0.5, 0, +0.5));
		defaultLayer.add(new Vector(+0.5, 0, -0.5));
		defaultLayer.add(new Vector(+0.5, 0, +0.5));
	}
	
}
