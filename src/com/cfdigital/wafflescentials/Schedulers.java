package com.cfdigital.wafflescentials;

import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.cfdigital.wafflelib.PlayerClass;
import com.cfdigital.wafflescentials.chat.ChatClass;

public class Schedulers {

	private static final Plugin plugin = WaffleScentials.plugin;

	public static void setupTasks() {
		//AFK timer
		if (!plugin.isEnabled()) return;
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					if (player == null) continue;
					if (WaffleScentials.plugin.getWafflePlayer(player.getName()) == null) continue;
					PlayerClass afkPlayer = WaffleScentials.plugin.getWafflePlayer(player.getName());
					if (afkPlayer.isPlayerAFK()) continue;
					if (new Date().getTime() - (Config.afkTimer * 1000) > afkPlayer.getLastActive()) {
						String myPrefix = WaffleScentials.getPrefix(player);
						myPrefix = myPrefix.replace("&", "§");
						ChatClass.setTabName(player, "[AFK]"+myPrefix);
						plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + player.getName() + " is now AFK from being idle after " + Config.afkTimer + " seconds");   
						afkPlayer.setAFK("AutoAFK");
					} 
				}
			}
		}, 1L * 5, 1L * 5);
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				for (Player player : plugin.getServer().getOnlinePlayers()) {
					WaffleScentials.plugin.getWafflePlayer(player.getDisplayName()).decrMessageCount();
					//Log.warn(WaffleScentials.plugin.getWafflePlayer(player.getDisplayName()) + " " + WaffleScentials.plugin.getWafflePlayer(player.getDisplayName()).getMessageCount());
				}
			}
		}, 1L * 20, 1L * 20);

	}
}