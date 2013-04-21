package com.cfdigital.wafflescentials.commands;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.cfdigital.wafflelib.PlayerClass;
import com.cfdigital.wafflescentials.WaffleScentials;

public class Whois implements CommandExecutor {
	
	 WaffleScentials plugin;

	public Whois(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		if (commandName.equalsIgnoreCase("whois")) {
			if (trimmedArgs.length == 1) {
				PlayerClass pc = plugin.getWafflePlayer(trimmedArgs[0]);
				sender.sendMessage(ChatColor.GOLD + "Player information for " + ChatColor.GREEN + trimmedArgs[0]);
				if (pc != null) {
					Long lm = pc.getLastActive();
					Long now = new Date().getTime();
					Long idle = (now - lm);
					int seconds = (int) (idle / 1000) % 60 ;
					int minutes = (int) ((idle / (1000*60)) % 60);
					int hours   = (int) ((idle / (1000*60*60)) % 24);
					sender.sendMessage(ChatColor.GOLD + "Idle time: " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
					if (pc.isPlayerAFK()) {
						sender.sendMessage(ChatColor.GOLD + "AFK reason: "+ pc.getAFKReason());
					}
				}
				OfflinePlayer bukkitPlayer = plugin.getServer().getOfflinePlayer(trimmedArgs[0]);
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
				Long ls = bukkitPlayer.getLastPlayed();
				if (ls == 0) {
					sender.sendMessage(ChatColor.GOLD + "Player has hever joined! Did you spell the name right?");
					return true;
				}
				String date = sdf.format(ls);
				sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
				sender.sendMessage(ChatColor.GOLD + "Last seen: " + date);
				ls = bukkitPlayer.getFirstPlayed();
				date = sdf.format(ls);
				sender.sendMessage(ChatColor.GOLD + "First seen: " + date);				
				sender.sendMessage(ChatColor.GOLD + "Banned: " + bukkitPlayer.isBanned());			
				return true;
			}
			
		}
		return true;
	}
}
