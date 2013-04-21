package com.cfdigital.wafflescentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;

public class Spawn implements CommandExecutor {
	
	 WaffleScentials plugin;

	public Spawn(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		if (commandName.equalsIgnoreCase("spawn")) {
			Player player = (Player) sender;
			player.teleport(player.getWorld().getSpawnLocation());
		}
		return true;
	}
}
