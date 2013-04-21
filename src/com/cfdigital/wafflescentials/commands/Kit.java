package com.cfdigital.wafflescentials.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cfdigital.wafflescentials.WaffleScentials;

public class Kit implements CommandExecutor {
	
	 WaffleScentials plugin;

	public Kit(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (commandName.equalsIgnoreCase("kit")) {
			if (trimmedArgs.length == 0) {
				if (plugin.hasPermissions(player, "wscent.kits")) {
					sender.sendMessage(WaffleScentials.Prefix + "Kits:");
					for (String kit : WaffleScentials.kits.keySet()) {
						sender.sendMessage(kit);
					}
				}
			} 
			else {
				if (WaffleScentials.kits.containsKey(trimmedArgs[0])) {
					if (plugin.hasPermissions(player, "wscent.kits."+trimmedArgs[0])) {
						long lastUsed = plugin.getWafflePlayer(player.getName()).getKitLastUsed(trimmedArgs[0]);
						int coolDown = WaffleScentials.kits.get(trimmedArgs[0]).getCoolDown();
						if ((System.currentTimeMillis() / 1000L - lastUsed) < coolDown) {
							sender.sendMessage(WaffleScentials.Prefix + "§dYou spawned this kit too recently!");
							return true;
						}
						plugin.getWafflePlayer(player.getDisplayName()).setKitLastUsed(trimmedArgs[0], System.currentTimeMillis() / 1000L);
						List<ItemStack> items = new ArrayList<ItemStack>();
						items.addAll(WaffleScentials.kits.get(trimmedArgs[0]).getItemStack());
						int x = 0;
						while (x != items.size()) {
							ItemStack is = new ItemStack(items.get(x));
							ItemMeta meta = is.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(ChatColor.BOLD + "" + ChatColor.RED + "KIT ITEM");
							lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "Owned by: " + sender.getName());
							long now = System.currentTimeMillis() / 1000L;
							Date d = new Date(now);  
							lore.add(ChatColor.BOLD + "" + ChatColor.GOLD + "Spawned at: " + d);
							lore.add(ChatColor.BOLD + "" + ChatColor.RED + "IF THIS ITEM DOES NOT BELONG TO YOU");
							lore.add(ChatColor.BOLD + "" + ChatColor.RED + "THEN DISCARD THIS SPAWNED ITEM.");
							meta.setLore(lore);
							is.setItemMeta(meta);
							player = (Player) sender;
							player.getInventory().addItem(is);
							x++;
						}
						sender.sendMessage(WaffleScentials.Prefix + "§dYou have been given kit: " + trimmedArgs[0]);
					}
					else {
						sender.sendMessage(WaffleScentials.Prefix + "§dNo permission to spawn this kit!");
					}
				}
				else {
					sender.sendMessage(WaffleScentials.Prefix + "§dNo such kit!");
				}
			}
			return true;
		}
		return true;
	}
	
}
	