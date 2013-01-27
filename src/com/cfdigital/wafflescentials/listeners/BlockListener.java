package com.cfdigital.wafflescentials.listeners;

import net.minecraft.server.v1_4_6.WorldChunkManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import com.cfdigital.wafflescentials.WaffleScentials;

public class BlockListener implements Listener	{
	
	final WaffleScentials plugin;
	
	public BlockListener(WaffleScentials instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhsyics(BlockPhysicsEvent event)
	{	
		
		Block block = event.getBlock();
		Material mat = event.getChangedType();
		if (mat.equals(Material.SAND)) event.setCancelled(true);
		int x = event.getBlock().getX();
		int z = event.getBlock().getZ();
		WorldChunkManager biomeManager = ((CraftWorld) event.getBlock().getWorld()).getHandle().getWorldChunkManager();	
		String biome = biomeManager.getBiome(x, z).y;
		if (biome.equalsIgnoreCase("thescorch") || biome.equalsIgnoreCase("phoenix")) {
			if (event.getBlock().getType() == Material.WATER) {
            	event.setCancelled(true);
        	}
		}
		
	}
	
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    	int x = event.getPlayer().getLocation().getBlockX();
    	int z = event.getPlayer().getLocation().getBlockZ();
		WorldChunkManager biomeManager = ((CraftWorld) event.getPlayer().getLocation().getWorld()).getHandle().getWorldChunkManager();
		String biome = biomeManager.getBiome(x, z).y;
		if (biome.equalsIgnoreCase("thescorch") || biome.equalsIgnoreCase("phoenix")) {
			if (event.getBucket() == Material.WATER_BUCKET) {
				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BURP,0,0);
            	event.setCancelled(true);
            	event.getPlayer().sendMessage(ChatColor.GRAY + "Silly human, this place is too hot for water!");
        	}
		}
    }
	@EventHandler(priority = EventPriority.HIGH)
    public void onBlockFade(BlockFadeEvent event) {
		int x = event.getBlock().getX();
		int z = event.getBlock().getZ();
		WorldChunkManager biomeManager = ((CraftWorld) event.getBlock().getWorld()).getHandle().getWorldChunkManager();
		String biome = biomeManager.getBiome(x, z).y;
		if (biome.equalsIgnoreCase("thescorch") || biome.equalsIgnoreCase("phoenix")) {
			if (event.getBlock().getType() == Material.ICE) {
            	event.setCancelled(true);
        	}
		}
   }
}