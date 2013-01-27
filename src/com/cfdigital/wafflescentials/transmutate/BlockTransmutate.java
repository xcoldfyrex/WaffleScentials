package com.cfdigital.wafflescentials.transmutate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.cfdigital.wafflescentials.WaffleScentials;

public class BlockTransmutate {
	
	private static void Mutate (Block srcBlock, Material targetMat) {
		srcBlock.setType(targetMat);
	}
	
	public static void growMoss(Block srcBlock) {
		switch (srcBlock.getType()) {
			case MOSSY_COBBLESTONE:
				break;
			case SMOOTH_BRICK:
				if (srcBlock.getData() == 1) {
					break;
				}
				return;
			default:
				return;
		}
		if (srcBlock.getLightLevel() < 3 ) {
			//moss will only grow upwards and light level under 3
			if (srcBlock.getY() > 255) return;
		}
		final Location blockLoc = srcBlock.getLocation();
		final String srcWorld = blockLoc.getWorld().getName();
		Location targetLoc = blockLoc;
		targetLoc.setY(targetLoc.getY()+1);
		Block targetBlock = WaffleScentials.plugin.getServer().getWorld(srcWorld).getBlockAt(targetLoc);
		switch (targetBlock.getType()) {
			case MOSSY_COBBLESTONE:
				break;
			case SMOOTH_BRICK:
				if (srcBlock.getData() == 1) {
					break;
				}
				return;
			default:
				return;
		}
		Mutate (targetBlock, Material.MOSSY_COBBLESTONE);
		//else {
		//	return;
		//}
	}
	
}