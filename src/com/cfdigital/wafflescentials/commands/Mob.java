package com.cfdigital.wafflescentials.commands;

import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.mobs.MobClass;
import com.cfdigital.wafflescentials.mobs.MobEntity;
import com.cfdigital.wafflescentials.mobs.Mobs;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class Mob implements CommandExecutor {

	 WaffleScentials plugin;

	public Mob(WaffleScentials plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String[] trimmedArgs = args;
	    Player player = null;
	    if (sender instanceof Player) {
	    	player = (Player) sender;
	    }
	        
	    //make sure they actually did something
	    if (trimmedArgs.length == 0) {
	       	sender.sendMessage(WaffleScentials.Prefix + "Missing sub command!");
	       	return true;
	    }
	    String commandName = trimmedArgs[0];//command.getName().toLowerCase();
	    
	    if (commandName.equalsIgnoreCase("test")) {
	       	if (!plugin.hasPermissions(player, "wscent.mobs.all")) return true;
	       	if (Config.debug) {
	       		Config.debug = false;
	       	}
	       	else {
	       		Config.debug = true;
	       	}
	       	sender.sendMessage(ChatColor.GRAY + "Toggled debug");
	       	return true;
	    }
	    
	    if (commandName.equalsIgnoreCase("test2")) {
	       	if (!plugin.hasPermissions(player, "wscent.mobs.all")) return true;
	    	ItemStack dsword = new ItemStack(Material.DIAMOND_SWORD, 1);
	    	ItemMeta meta = dsword.getItemMeta();
	        ArrayList<String> lore = new ArrayList<String>();
	        lore.add(ChatColor.BOLD + "" + ChatColor.RED + "KIT ITEM");
	        lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "Owned by: " + sender.getName());
	        long now = System.currentTimeMillis() / 1000L;
	        Date d = new Date(now);  
	        lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "Spawned at: " + d);
	        lore.add(ChatColor.BOLD + "" + ChatColor.RED + "IF THIS ITEM DOES NOT BELONG TO YOU");
	        lore.add(ChatColor.BOLD + "" + ChatColor.RED + "THEN DISCARD THIS SPAWNED ITEM.");
	        meta.setLore(lore);
	        dsword.setItemMeta(meta);
	        
	    	player = (Player) sender;
	    	player.getInventory().addItem(dsword);

	    }
	    
	    //spawn bats over people
		if (commandName.equalsIgnoreCase("bats")) {
			if (player == null) return true;
	       	if (plugin.hasPermissions(player, "wscent.mobs.bats")) {
	       		for (Player spawnTarget : plugin.getServer().getOnlinePlayers()) {
	       			Location loc = spawnTarget.getLocation();
	       			loc.setY(loc.getY()+10);
	       			EntityType toSpawn;
	       			toSpawn = EntityType.BAT;
	       			int i = 0;
	       			while (i < 25 ) {
	       				spawnTarget.getWorld().spawnEntity(loc, toSpawn);
	       				i++;
	       			}
	       		}
	       		return true;
	       	}       	
	    }
		
		//show classes
		if (commandName.equalsIgnoreCase("classes")) {
			sender.sendMessage(ChatColor.GREEN + "|Class | Type | Drop |");
			for (String key : Mobs.mobClass.keySet()) {
				MobClass mc = Mobs.mobClass.get(key);
				String compose = ChatColor.GOLD + key + " | " + mc.getMobEntity().toString() + " | " + mc.getDrops();
   				sender.sendMessage(compose);
   			}
			return true;
		}
		
		//spawning mobs classes
		if (commandName.equalsIgnoreCase("spawnclass")) {
       		if (trimmedArgs.length > 1) {
       			String mobClass = trimmedArgs[1];
       			if (Mobs.getMobClass(mobClass) != null){
       	       		if (!plugin.hasPermissions(player, "wscent.mob.spawn"))  return true;
       	       		EntityType toSpawn;
       	       		toSpawn = Mobs.getMobClass(mobClass).getMobEntity();
       	       		if (toSpawn == null ){
       	       			Log.error("Mob entity for class " + mobClass + " is null!");
       	       			return true;
       	       		}
       	       		Entity monster = null;
       	       		monster = player.getWorld().spawnEntity(player.getLocation(),toSpawn);
       	       		MobEntity mob = Mobs.mob((LivingEntity)monster);
       	       		if (!(mob == null)) {
       	       			mob.dressMonster(mobClass);
       	       			mob.setMeta(mobClass);
       	     			mob.setEffect(Mobs.mobClass.get(mobClass).getEffect());
       	       			sender.sendMessage(WaffleScentials.Prefix + "Spawned " + mobClass);
       	       		}
       			}
       			else {
       				sender.sendMessage(WaffleScentials.Prefix+"Missing or invalid mob class! Please see /mob classes");

       			}
       		}
       		else {
       			sender.sendMessage(WaffleScentials.Prefix+"You need to specify the class..");
       		}
        	return true;
		}
		
		if (commandName.equalsIgnoreCase("count")) {
			int leCount = 0;
			int creatureCount = 0;
			int ageCount = 0;
			int npcCount = 0;
			int animalsCount = 0;
			int zaCount = 0;
			
        	for (World world : Bukkit.getServer().getWorlds()) {
        		for (Entity e : world.getEntities()) {
        			if (e instanceof LivingEntity) leCount++;
        			if (e instanceof Creature) creatureCount++;
        			if (e instanceof Ageable) ageCount++;
        			if (e instanceof NPC) npcCount++;
        			if (e instanceof Animals) animalsCount++;
        		}
        		if (leCount > 0) {
        			sender.sendMessage(ChatColor.LIGHT_PURPLE + world.getName() + ":");
        			sender.sendMessage(ChatColor.GREEN + "le: " + leCount + " | creature: " + creatureCount + " | ageable: " + ageCount + " | npc: " + npcCount + " | animal: " + animalsCount);
        		}
    			leCount = 0;
    			creatureCount = 0;
    			ageCount = 0;
    			npcCount = 0;
    			animalsCount = 0;
    			zaCount = 0;
        	}
        	zaCount = Mobs.getMetaTagCount("ZA");
        	sender.sendMessage(ChatColor.RED + "" +ChatColor.BOLD + "tagged: [za: " + zaCount + " | mapSize: " + Mobs.entityList.size() + " ]");
        	return true;
		}
		//spawn a mob with class
		if (commandName.equalsIgnoreCase("death")) {
	      	Location loc;
	       	boolean fromCB = false;
	       	if (sender instanceof Player) {
	       		//came from a player
	       		if (!plugin.hasPermissions(player, "wscent.mob.death"))  return true;
	       		loc = player.getLocation();
	       		//Log.warn(commandName + " " + " " + String.valueOf(trimmedArgs) + " " + loc);
	       	}
	       	else {
	       		//came from console or commandblock
	       		if (trimmedArgs.length < 5) return false;
	       		double x = Double.valueOf(trimmedArgs[1]);
           		double y = Double.valueOf(trimmedArgs[2]);
           		double z = Double.valueOf(trimmedArgs[3]);           		
           		World world = plugin.getServer().getWorld(trimmedArgs[4]);
           		fromCB = true;
           		loc = new Location(world,x,y,z);
           		//Log.warn(commandName + " " + "  " + loc);           		
           	}
       		EntityType toSpawn;
       		
       		toSpawn = EntityType.ZOMBIE;
       		Entity monster;
       		if (trimmedArgs.length >= 2) {
       			//spawn x of them
       			int x = 0;
       			int max;
       			if (fromCB) {
       				max = Integer.parseInt(trimmedArgs[5]);
       			} else {
       				max = Integer.parseInt(trimmedArgs[1]);
       			}
       			if (!fromCB) player.sendMessage("Spawned " + max + " death zombies!" );
       			while (x != max){
       	    		if (!fromCB) {
       	    			monster = player.getWorld().spawnEntity(loc,toSpawn);
       	    		} else {
       	    			monster = plugin.getServer().getWorld(trimmedArgs[4]).spawnEntity(loc, toSpawn);
       	    		}
       	    		MobEntity mob = Mobs.mob((LivingEntity)monster);
       	    		if (!(mob == null)) {
       	    			mob.dressMonster("Soldier");
       	    			//mob.dressMonster(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,Material.DIAMOND_LEGGINGS,Material.DIAMOND_BOOTS, Material.WOOD_SWORD );
               			mob.setMeta("ZA");

       	    		}
       	    		else {
       	    			WaffleLogger.warning("NULL mob tried to dress!");
       	    		}
       	    		
           			x++;
       			}
       		}
       		else {
       			//spawn just one
           		monster = player.getWorld().spawnEntity(loc,toSpawn);
           		MobEntity mob = Mobs.mob((LivingEntity)monster);
	    		mob.dressMonster("Soldier");
       			mob.setMeta("ZA");
       		}   
       		return true;
        }

		if (player == null) return true;
    	player.sendMessage("Missing valid sub command!");
		return true;
	}
}