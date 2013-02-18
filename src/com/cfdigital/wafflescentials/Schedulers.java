package com.cfdigital.wafflescentials;

import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.cfdigital.wafflescentials.chat.ChatClass;

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
	}
}