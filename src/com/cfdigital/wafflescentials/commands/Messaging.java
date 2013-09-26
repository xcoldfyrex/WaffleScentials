package com.cfdigital.wafflescentials.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.chat.ChatClass;


public class Messaging implements CommandExecutor {

	WaffleScentials plugin;

	public Messaging(WaffleScentials plugin) {
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

		// /msg
		if (commandName.equalsIgnoreCase("msg") && trimmedArgs.length >= 2) {
			if (plugin.getWafflePlayer(player.getName()).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this");
			} else {
				if (plugin.hasPermissions(player, "wscent.chat.msg")) {
					if((plugin.getPlayer(trimmedArgs[0]) != null) &&  plugin.getPlayer(trimmedArgs[0]).isOnline()) {
						Player targetPlayer = WaffleScentials.plugin.getPlayer(trimmedArgs[0]);
						targetPlayer.sendMessage(ChatColor.AQUA + "! ! " + ChatColor.GRAY + player.getDisplayName() + " -> me:  " + ChatClass.join(trimmedArgs, " ",1));
						player.sendMessage(ChatColor.AQUA + "! ! " + ChatColor.GRAY + "me -> " +  targetPlayer.getDisplayName() + ": " +  ChatClass.join(trimmedArgs, " ", 1));
						plugin.getWafflePlayer(targetPlayer.getName()).setLastMessager(player.getName());
						return true;
					}
					else {
						player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Cannot message offline players");
						return true;
					}
				}
			}
		}

		// /reply
		if ((commandName.equalsIgnoreCase("r") || commandName.equalsIgnoreCase("reply")) && trimmedArgs.length >= 1) {
			if (plugin.getWafflePlayer(plugin.getPlayerName(player.getDisplayName())).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this");
			} else {
				if (plugin.hasPermissions(player, "wscent.chat.msg")) {
					String lastMessager = null;
					if ((lastMessager = plugin.getWafflePlayer(player.getDisplayName()).getLastMessager()) != null) {
						if (plugin.getPlayer(lastMessager).isOnline()) {
							Player targetPlayer = WaffleScentials.plugin.getPlayer(lastMessager);
							targetPlayer.sendMessage(ChatColor.AQUA + "! ! " + ChatColor.GRAY + player.getDisplayName() + " -> me: " + ChatClass.join(trimmedArgs, " ",0));
							player.sendMessage(ChatColor.AQUA + "! ! " + ChatColor.GRAY + "me -> " +  targetPlayer.getDisplayName() + ": " + ChatClass.join(trimmedArgs, " ", 0));
							plugin.getWafflePlayer(targetPlayer.getDisplayName()).setLastMessager(player.getDisplayName());
							return true;
						} else {
							player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Player is not online");
							return true;
						}

					} else {
						player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Noone has messaged you yet");
						return true;
					}
				}
			}
		}
		// /me
		if (commandName.equalsIgnoreCase("me") && trimmedArgs.length >= 1) {
			if (plugin.getWafflePlayer(player.getName()).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this!");
				return true;
			} else {
				if (plugin.hasPermissions(player, "wscent.chat.msg")) {
					String myPrefix = plugin.getPrefix(player);
					myPrefix = myPrefix.replace("&", "§");
					plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getDisplayName() + "§f " + ChatClass.join(trimmedArgs, " ", 0));
					return true;
				}
			}
		}

		// /say
		if (commandName.equalsIgnoreCase("say") && trimmedArgs.length >= 1) {
			if (plugin.hasPermissions(player, "wscent.chat.broadcast")) {
				String message = ChatClass.join(trimmedArgs, " ", 0);
				String pn = "CONSOLE";
				if (!(player == null))pn = player.getName();
				plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + pn + ChatColor.LIGHT_PURPLE + "] " + message);
			}
			return true;
		}
		return true;

	}
	

}