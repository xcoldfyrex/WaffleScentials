package com.cfdigital.wafflescentials.commands;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WafflePlayer;
import com.cfdigital.wafflescentials.WaffleScentials;

public class Time implements CommandExecutor {
	
	 WaffleScentials plugin;

	public Time(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) { 
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		if (commandName.equalsIgnoreCase("time") && trimmedArgs.length == 1) {
			Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				return true;
			}
			if (plugin.hasPermissions(player, "wscent.world.time")) {
				long time = 0;
				if (trimmedArgs[0].equalsIgnoreCase("day")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ "Changed time to day");
					time = 0;
				} else if (trimmedArgs[0].equalsIgnoreCase("night")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ "Changed time to night");
					time = 14000;
				}

				else {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ " [day] | [night]");
				}
				player.getWorld().setTime(time);
			}
			return true;
		}
		return true;
	}
}
