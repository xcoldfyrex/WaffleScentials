package com.cfdigital.wafflescentials.listeners;


import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.mobs.Mobs;

public class ServerListener implements Listener	{

	final WaffleScentials plugin;
	public java.util.Vector<Location> dispenserList = new java.util.Vector<Location>();

	public ServerListener(WaffleScentials instance) {
		plugin = instance;
		//PlayerList pl;
		//pl.players.add("HELLO");
	}



	@EventHandler(priority = EventPriority.NORMAL)
	public void creatureDeath(EntityDeathEvent event) {
		LivingEntity le = event.getEntity();
		if (Mobs.mob(le) != null ){
			List<MetadataValue> values = le.getMetadata("class");
			String mobClass = null;
			for(MetadataValue value : values){
				mobClass = value.value().toString();
			}
			if (mobClass != null){
				if (Mobs.getMobClass(mobClass).getDrops().size() > 0) {
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
					if (!(le.getKiller() == null)) {
						event.getDrops().add(Mobs.getMobClass(mobClass).getDrops().get(0));
						float reward = Mobs.getMobClass(mobClass).getReward();
						le.getKiller().sendMessage(ChatColor.DARK_GRAY + "You will gain " + reward + " for killing this.");
						String name = le.getKiller().getName();
						if (WaffleScentials.economy.isEnabled()) { 
							if (WaffleScentials.economy.hasAccount(name)) {
								WaffleScentials.economy.bankDeposit(name, reward);
							}
						}
					}
				} else {
					//somehow there is metadata but no hashmap entry
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
				}
				
			} else {
				//stop drops from all mobs not in a class in spawn control worlds
				String spawnworld = event.getEntity().getWorld().getName();
				if (spawnworld == null) return;
				if (spawnworld.equalsIgnoreCase(Config.spawnControlWorlds)) {
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
				}
				
			}
		}
		Mobs.delMob(le);
		return;
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