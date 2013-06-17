package com.cfdigital.wafflescentials.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.gameitems.SetEnchant;

public class Enchant implements CommandExecutor {

	WaffleScentials plugin;

	public Enchant(WaffleScentials plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		Player player = null;
		if (commandName.equalsIgnoreCase("enchant")) {
			if (sender instanceof Player) {
				player = (Player) sender;
			} else {
				return true;
			} 

			if (plugin.hasPermissions(player, "wscent.misc.enchant")) {
				Player targetPlayer = plugin.getPlayer(trimmedArgs[0]);
				String enchantment = trimmedArgs[1];
				int level = Integer.valueOf(trimmedArgs[2]);
				SetEnchant.addEnchant(targetPlayer, enchantment, level);
			}
		}
		return true;
	}
}
