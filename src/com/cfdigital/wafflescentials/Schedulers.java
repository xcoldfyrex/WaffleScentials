package com.cfdigital.wafflescentials;

import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.mobs.MobSpawning;
import com.cfdigital.wafflescentials.mobs.Mobs;

public class Schedulers {
	
	private static final Plugin plugin = WaffleScentials.plugin;

	public static void setupTasks() {
        //AFK timer
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                	if (player == null) continue;
                	if (!WaffleScentials.wafflePlayers.containsKey(player.getName())) continue;
           			PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(player.getName());
                    if (afkPlayer.isPlayerAFK())
                        continue;

                    if (new Date().getTime() - (Config.afkTimer * 1000) > afkPlayer.getLastActive()) {
                    	String myPrefix = WaffleScentials.getPrefix(player);
    	           		myPrefix = myPrefix.replace("&", "§");
    	        		ChatClass.setTabName(player, "[AFK]"+myPrefix);
    	        		plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getName() + "§f is now AFK from being idle after " + Config.afkTimer + " seconds");   
               			afkPlayer.setAFK("AutoAFK");
               		} 
                }
            }
        }, 1L * 5, 1L * 5);
        
        // mobs!
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
            	Mobs.doIterate();
            }
        }, 10L * 5, 10L * 5);
        
		//moss grow
		/*
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
            public void run() {
            	Blo
            	BlockTransmutate.growMoss(srcBlock)
            }
        }, 10L * 5, 10L * 5);
		*/
		
        //clear hashmap
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
            	for (Iterator< LivingEntity > it = Mobs.entityList.keySet().iterator(); it.hasNext(); )
            	{
            		if (it.hasNext()) {
            			try {
            				LivingEntity le = it.next();
           	    			if (!(le.isValid())) {
           	    				it.remove();
           	    			}
           	    		
            			} catch (ConcurrentModificationException e) {
            				break;
            			}
            		}
            	}
            }
           
        }, 10L * 5, 10L * 5);
		
		//custom mobspawning
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
            	MobSpawning.doSpawn();
            	//WaffleScentials.plugin.getServer().getWorld("world").spawnCreature(arg0, arg1)
            }
           
        }, 10L * 5, 10L * 5);
	}
}