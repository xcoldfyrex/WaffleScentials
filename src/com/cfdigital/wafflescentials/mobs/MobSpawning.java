package com.cfdigital.wafflescentials.mobs;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.WaffleScentials;

public class MobSpawning{

	private static void spawnMob(EntityType et, Location loc) {
		WaffleScentials.plugin.getServer().getWorld(loc.getWorld().getName()).spawnEntity(loc, et);
	}
	
	private static boolean safeToSpawn(Block checkBlock) {
		//don't spawn in air
		if (checkBlock.getType().equals(Material.AIR)) return false;
		//check for room above
		if (!checkBlock.getRelative(BlockFace.UP, 1).getType().equals(Material.AIR)) return false;
		if (!checkBlock.getRelative(BlockFace.UP, 2).getType().equals(Material.AIR)) return false;
		if (!checkBlock.getRelative(BlockFace.UP, 3).getType().equals(Material.AIR)) return false;
		if (!((checkBlock.getRelative(BlockFace.UP).getLightFromBlocks() < 7) && (checkBlock.getRelative(BlockFace.UP).getLightLevel() < 7))) return false;

		return true;
	}
	private static Location randomLocation(Location baseLocation, World world) {
		int basex = baseLocation.getBlockX();
		int basey = baseLocation.getBlockY();
		int basez = baseLocation.getBlockZ();
		int randx = 0;
		int randy = 0;
		int randz = 0;
		
		randx = -20 + (int)(Math.random() * ((20 - -20) +1));
		randy = -5 + (int)(Math.random() * ((5 - -5) +1));
		//don't spawn above or below world
		if (randy < 3) randy = basey;
		if (randy > 255) randy = basey;
		randz = -20 + (int)(Math.random() * ((20 - -20) +1));
		int targetx = randx + basex;
		int targety = randy + basey;
		int targetz = randz + basez;
		
		Block checkBlock = world.getBlockAt(targetx, targety, targetz);

		//try to place on solid ground if we get air which eventually we should
		while (checkBlock.getType().equals(Material.AIR) || checkBlock.getType().equals(Material.WATER) || checkBlock.getType().equals(Material.LAVA)) {
			if (targety < 3) return null;
			targety--;
			checkBlock = world.getBlockAt(targetx, targety, targetz);
		}
		//make sure there is room to spawn them and stop spawning them in walls
		
		/*
		if (checkBlock.getRelative(BlockFace.UP, 1).getType().equals(Material.AIR)) {
			if (checkBlock.getRelative(BlockFace.UP, 2).getType().equals(Material.AIR)) {
				if (Config.debug){
					Log.warn("Light: " + checkBlock.getRelative(BlockFace.UP).getLightFromBlocks() + " " + (checkBlock.getRelative(BlockFace.UP).getLightLevel()));
				}
				//if (((checkBlock.getRelative(BlockFace.UP).getLightFromBlocks() < 7) && (checkBlock.getRelative(BlockFace.UP).getLightLevel() < 7)) || checkBlock.getWorld().isThundering() ) {
				if (((checkBlock.getRelative(BlockFace.UP).getLightFromBlocks() < 7) && (checkBlock.getRelative(BlockFace.UP).getLightLevel() < 7))) {
					Location spawnLoc = new Location(world,targetx, targety, targetz);
					//Log.warn(basex + " " + randx + " " + basey + " " + randy + " " + basez + " " + randz);
			   		return spawnLoc;
			   	}
			}
		}
		*/
		if (safeToSpawn(checkBlock)) {
			Location spawnLoc = new Location(world,targetx + .5, targety + 1, targetz + .5);
	   		return spawnLoc;
		}
   		return null;  		
	}
	
	public static void doSpawn() {
		for (Player player : WaffleScentials.plugin.getServer().getOnlinePlayers()) {
			String world = player.getWorld().getName();
			//only deal with worlds listed in config
			if (!(world.equalsIgnoreCase(Config.spawnControlWorlds))) { 
				continue;
			}
			//for (Chunk chunk : world.getLoadedChunks()) {
				int chunkMonsterCount = 0;
				for (Entity entity : player.getWorld().getEntities()) {
					//get number of monster per chunk
					if (entity instanceof Monster) chunkMonsterCount++;

				}
				//check to ensure we don't have too many per chunk
				if (chunkMonsterCount >= Config.maxChunkMobs) continue;
				Location spawnLoc = randomLocation(player.getLocation(), player.getWorld());
				//no suitable block found last run, bail
				if (spawnLoc == null) continue;

				Random rand = new Random();
				int min = 0, max = 3;

				// nextInt is normally exclusive of the top value,
				// so add 1 to make it inclusive
				int randMob = rand.nextInt(max - min + 1) + min;
				
				switch (randMob) {
					case 0:
						spawnMob(EntityType.ZOMBIE, spawnLoc);
						break;
					case 1:
						spawnMob(EntityType.SKELETON, spawnLoc);
						break;
					case 2:
						spawnMob(EntityType.CAVE_SPIDER, spawnLoc);
						break;
					case 3:
						spawnMob(EntityType.PIG_ZOMBIE, spawnLoc);
						break;
				}
				

			//}
		}
	}
}