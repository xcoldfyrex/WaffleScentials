package com.cfdigital.wafflescentials.listeners;

import java.util.Iterator;
import java.util.List;

import net.minecraft.server.v1_4_6.WorldChunkManager;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cfdigital.wafflescentials.WaffleScentials;

public class BlockListener implements Listener	{
	
	final WaffleScentials plugin;
	public java.util.Vector<Location> dispenserList = new java.util.Vector<Location>();

	
	public BlockListener(WaffleScentials instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPhsyics(BlockPhysicsEvent event)
	{	
		
		Block block = event.getBlock();
		Material mat = event.getChangedType();
		if (mat.equals(Material.SAND)) event.setCancelled(true);
		int x = block.getX();
		int z = block.getZ();
		WorldChunkManager biomeManager = ((CraftWorld) block.getWorld()).getHandle().getWorldChunkManager();	
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
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockDamageEvent(BlockDamageEvent event) {
		final Block block;
		final Player player;
		block = event.getBlock();
		player = event.getPlayer();
		if (WaffleScentials.dispenserWaiting.contains(player)) {
			if (block.getType().equals(Material.DISPENSER)) {
				player.sendMessage(WaffleScentials.Prefix+"Registered dispenser...");
				WaffleScentials.dispenserWaiting.remove(player);
				dispenserList.add(block.getLocation());
				event.setCancelled(true);
				return;
			}
			else {
				player.sendMessage(WaffleScentials.Prefix+"Only works on dispensers...");
				return;
			}
		}
		if (dispenserList.contains(block.getLocation())) {
			player.sendMessage(WaffleScentials.Prefix+"This is an unlimited dispenser!");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Player player;
		player = event.getPlayer();
		if (WaffleScentials.dispenserWaiting.contains(player)) {
			if (block.getType().equals(Material.DISPENSER)) {
				player.sendMessage(WaffleScentials.Prefix+"Registered dispenser...");
				WaffleScentials.dispenserWaiting.remove(player);
				dispenserList.add(block.getLocation());
				event.setCancelled(true);
				return;
			}
			else {
				player.sendMessage(WaffleScentials.Prefix+"Only works on dispensers...");
				return;
			}
		}
		if (dispenserList.contains(block.getLocation())) {
			player.sendMessage(WaffleScentials.Prefix+"Unregistered unlimited dispenser!");
			dispenserList.remove(block.getLocation());
		}
		if (block.getType().equals(Material.DISPENSER)) {
			Dispenser dispenser = (Dispenser) event.getBlock().getState();
			dispenser.getInventory().clear();
		}
	}




	@EventHandler(priority = EventPriority.LOWEST)
	public void onDispense(BlockDispenseEvent event) {
		if (event.isCancelled()) return;
		ItemMeta item = event.getItem().getItemMeta();
		if (item == null) return;
		List<String> im = item.getLore();
		if (im == null) return;
		if (!im.isEmpty()) {
			event.setCancelled(true);
		}

		Iterator<Location> it = dispenserList.iterator();
		while(it.hasNext()) {
			Location currentPosition = it.next();
			if (currentPosition.equals(event.getBlock().getLocation())) {
				Dispenser dispenser = (Dispenser) event.getBlock().getState();
				ItemStack newItemStack = event.getItem().clone();
				dispenser.getInventory().addItem(newItemStack);
				return; 
			}
		}
	}
}