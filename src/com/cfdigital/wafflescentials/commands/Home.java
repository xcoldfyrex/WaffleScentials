package com.cfdigital.wafflescentials.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;

public class Home implements CommandExecutor {

	WaffleScentials plugin;

	public Home(WaffleScentials plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			return true;
		}

		if (commandName.equalsIgnoreCase("home")) {
			if (trimmedArgs.length == 0) {
				//send to own home
				try {
					ResultSet result = WaffleScentials.db.query("SELECT * FROM `homes` WHERE `name`='"+player.getName()+"'");
					if (!result.next()) {
						player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You don't have a home set type '/home set'");
						return true;
					}
					double x = result.getDouble("x");
					double y = result.getDouble("y");
					double z = result.getDouble("z");
					String worldName = result.getString("world");
					World world = plugin.getServer().getWorld(worldName);
					result.close();
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Welcome back home");
					Location location = new Location(world,x,y,z);
					player.teleport(location);
					return true;

				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				if (trimmedArgs[0].equalsIgnoreCase("set")) {
					if (!plugin.hasPermissions(player, "wscent.home.self")) {
						return true;
					}
					
					try {
						//set the home
						ResultSet result;
						result = WaffleScentials.db.query("SELECT * FROM `homes` WHERE `name`='"+player.getName()+"'");
						if (!result.next()) {
							result.close();
							result =  WaffleScentials.db.query("INSERT INTO homes VALUES('" + player.getName() + "','" + player.getLocation().getX() + "','" + player.getLocation().getY() + "','" + player.getLocation().getZ() + "','" + player.getLocation().getWorld().getName()+ "');");							
						} else {
							result.close();
							result =  WaffleScentials.db.query("UPDATE homes SET `name`='" + player.getName() + "',`x`='" + player.getLocation().getX() + "',y='" + player.getLocation().getY() + "',z='" + player.getLocation().getZ() + "',world='" + player.getLocation().getWorld().getName() + "' WHERE `name` = '" + player.getName() + "';");
						}
						player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Welcome to your new home");
						result.close();
						return true;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				} else {
					if (!plugin.hasPermissions(player, "wscent.home.others")) return true;
					//go to other's home

				}
			}
		}
		return true;
	}
}