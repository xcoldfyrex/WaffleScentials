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

public class Weather implements CommandExecutor {
	
	 WaffleScentials plugin;

	public Weather(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) { 
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		if (commandName.equalsIgnoreCase("weather") && trimmedArgs.length == 1) {
			Player player = null;
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				return true;
			}
			if (plugin.hasPermissions(player, "wscent.world.weather")) {
				boolean newState = false;
				if (trimmedArgs[0].equalsIgnoreCase("clear")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ "Changed weather to clear!");
					newState = false;
				} else if (trimmedArgs[0].equalsIgnoreCase("rain")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ "Changed weather to storm! You dick!");
					newState = true;
				}

				else {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
							+ " [rain] | [clear]");
				}
				player.getWorld().setStorm(newState);
			}
			return true;
		}
		return true;
	}
}
