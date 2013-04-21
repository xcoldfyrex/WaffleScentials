package com.cfdigital.wafflescentials.listeners;

import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cfdigital.wafflelib.PlayerClass;
import com.cfdigital.wafflelib.WaffleLib;
import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class PlayerListener implements Listener	{

	final WaffleScentials plugin;

	public PlayerListener(WaffleScentials instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) return;
		final Player player = event.getPlayer();
		final String kickReason = event.getReason();
		event.setLeaveMessage("§e" + player.getDisplayName() + " was kicked! Reason: " + kickReason);		
	}

	public void checkPlayerSpam(AsyncPlayerChatEvent event){
		//Player player = event.getPlayer();
		//String message = event.getMessage();
		//long lastMessage = plugin.getWafflePlayer(player.getDisplayName()).getLastMessageSent();
		//String

	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {	

		String message;
		Player player = event.getPlayer();
		message = event.getMessage();

		//start with any chat filtering, first
		boolean matched = false;
		if (!(ChatClass.chatFilters.size() == 0)) { 
			for (String regex : ChatClass.chatFilters.keySet()) {
				matched = ChatClass.matchPattern(message, regex);
				if (matched) {
					String filterAction = ChatClass.chatFilters.get(regex).filterAction;
					WaffleLogger.warning("FILTERED " + message);				
					//just deny it
					if (filterAction.equalsIgnoreCase("DENY")) {
						event.setCancelled(true);
					}

					//kick them
					if (filterAction.equalsIgnoreCase("KICK")) {
						event.setCancelled(true);
						player.kickPlayer("Chat filter");
					}

					//ban them
					if (filterAction.equalsIgnoreCase("BAN")) {
						event.setCancelled(true);
						player.kickPlayer("Banned - Chat filter");
						player.setBanned(true);
					}

					//replace
					if (filterAction.equalsIgnoreCase("REPLACE")) {
						message = ChatClass.replacePattern(message, regex, ChatClass.chatFilters.get(regex).filterReplace);
					}
				}
			}
		}


		PlayerClass afkPlayer = plugin.getWafflePlayer(player.getDisplayName());
		if (afkPlayer == null) return;
		plugin.getWafflePlayer(player.getDisplayName()).setLastActive(new Date().getTime());
		if (afkPlayer.isPlayerAFK()) {
			String myPrefix = WaffleScentials.getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			ChatClass.setTabName(player, myPrefix);
			WaffleScentials.plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getDisplayName() + "§f is no longer AFK");
			afkPlayer.unsetAFK();
		}
		if (plugin.getWafflePlayer(player.getDisplayName()).isMuted()){
			player.sendMessage(WaffleScentials.Prefix+"You are muted and cannot do this");
			event.setCancelled(true);
		}
		if (event.isCancelled()) return;
		afkPlayer.setLastActive(new Date().getTime());
		message = message.replace("§", "&");
		message = message.replace("%", "%%");		

		if (player.hasPermission("wscent.chat.colors")) {
			message = message.replace("&", "§");
		}

		String playerName = player.getDisplayName();
		String prefix = WaffleScentials.getPrefix(player);
		String chatFormat = Config.chatFormat;
		String playerRank = plugin.getRank(player);
		int playerLevelTemp = WaffleScentials.getLevel(player.getName());
		String playerLevel = String.valueOf(playerLevelTemp);

		chatFormat = chatFormat.replace("%player%", playerName);
		chatFormat = chatFormat.replace("%prefix%", prefix);
		chatFormat = chatFormat.replace("%rank%", playerRank);
		chatFormat = chatFormat.replace("[waffle]", playerLevel);
		chatFormat = chatFormat.replace("&", "§");
		chatFormat = chatFormat.replace("%message%", message);

		event.setFormat(chatFormat);
		//if (event.isCancelled()) return;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (event.isCancelled()) return;
		PlayerClass afkPlayer = plugin.getWafflePlayer(player.getDisplayName());
		if (afkPlayer == null) return;
		afkPlayer.setLastActive(new Date().getTime());
		String command = event.getMessage();
		if (command.startsWith("/tell") || command.startsWith("/me") || command.startsWith("/msg") || command.startsWith("/reply")) {
			if (plugin.getWafflePlayer(player.getDisplayName()).isMuted()){
				player.sendMessage(WaffleScentials.Prefix+"You are muted and cannot do this");
				event.setCancelled(true);
			}
		}

		if (command.startsWith("/register") || command.startsWith("/help") || command.startsWith("/rules") || command.startsWith("/vote") || command.startsWith("/?") ) {
			player.sendMessage("§e####################################################");
			player.sendMessage("§a   *** C H A R G E D   C R E E P E R S   M I N E C R A F T ***");
			player.sendMessage("");
			player.sendMessage("§aThis server requires registration and account activation on");
			player.sendMessage("§aour website for builder. §dchargedcreepers.com/user/register");
			player.sendMessage("");
			player.sendMessage("§aFor player commands, visit §dchargedcreepers.com/commands");
			player.sendMessage("§aFor rules, visit §dchargedcreepers.com/rules");
			player.sendMessage("§aTo vote, visit §dhttp://votefor.cc");
			player.sendMessage("");
			player.sendMessage("§e####################################################");
			event.setCancelled(true);
		}
		
		if (command.startsWith("/pl") || command.startsWith("/plugins")) {
			player.sendMessage("Plugins (5): " + ChatColor.GREEN + "WaffleLib" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleShops" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleScentials" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleMobs" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleIRC");
			event.setCancelled(true);
		}
		
		if (command.startsWith("/version") || command.startsWith("/ver")) {
			player.sendMessage("This server is running on gerbils");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		PlayerClass afkPlayer = plugin.getWafflePlayer(event.getPlayer().getDisplayName());
		if (afkPlayer == null) {
			WaffleLogger.severe(event.getPlayer().getDisplayName() + " Quit but was not in hashmap!");
		} else {
			afkPlayer.unsetAFK();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent Event) {
		Player player = Event.getPlayer();
		String myPrefix = WaffleScentials.getPrefix(player);
		if (myPrefix == null) return;
		myPrefix = myPrefix.replace("&", "§");
		ChatClass.setTabName(player, myPrefix);
		LivingEntity le = (LivingEntity) player;
		le.setCustomName(WaffleScentials.plugin.getRank(player) + " " + player.getDisplayName());
		if (plugin.getWafflePlayer(player.getName()) != null ) {
			plugin.getWafflePlayer(player.getName()).setLastActive(new Date().getTime());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent Event) {
		if (Event.isCancelled()) return;
		final Player player = Event.getPlayer();
		PlayerClass afkPlayer = plugin.getWafflePlayer(player.getDisplayName());
		if (afkPlayer == null) return;
		afkPlayer.setLastActive(new Date().getTime());
		if (afkPlayer.isPlayerAFK()) {
			String myPrefix = WaffleScentials.getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			ChatClass.setTabName(player, myPrefix);
			plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getDisplayName() + "§f is no longer AFK");
			afkPlayer.unsetAFK();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		ItemStack item =  event.getItem().getItemStack();
		List<String> im = item.getItemMeta().getLore();
		if (im == null) return;
		if (!im.isEmpty()) {
			if (im.get(1).contains(event.getPlayer().getDisplayName())) return;
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to pick it up.");
			event.getPlayer().sendMessage(ChatColor.GRAY + "The item will now despawn.");
			event.getItem().remove();
		}
	} 

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerClass afkPlayer = plugin.getWafflePlayer(player.getDisplayName());
		afkPlayer.setLastActive(new Date().getTime());
		if (plugin.hasPermissions(player, "wscent.iconomy.death")) {
			String name = player.getDisplayName();
			if (WaffleLib.economy.hasAccount(name)) {
				float rate = Config.getDeathTax(player.getWorld());
				if (rate < 1) {
					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + "There is no death penalty for this world.");
					return;
				}
				double amount = 0;
				amount = rate * WaffleLib.economy.getBalance(name) / 100;
				if (WaffleLib.economy.has(name, amount)) {
					WaffleLib.economy.bankWithdraw(name, amount);

					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + WaffleLib.economy.format(amount) + " (" + rate + "%) was taken from your account because you died.");
				} else {
					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + "Can't take money from you because you don't have enough money.");
				}
			} 
		}
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void onInventoryClick (InventoryClickEvent event) {
		//Log.warn(event.getSlot() + " "+ event.getSlotType() + " " +event.getRawSlot() + " " + event.getView().getTitle() + " " +event.getCurrentItem());

		if (!event.isCancelled()) {
			Player player = (Player)event.getWhoClicked();
			//trying to repair
			if (event.getSlotType().equals(SlotType.CRAFTING)){
				if(check_kit(event) == true) {
					player.closeInventory();
					player.sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to repair it.");
					event.setCancelled(true);
					return;
				}

			}
			//they can always place in armor slot
			if (event.getSlotType().equals(SlotType.ARMOR)) return;
			//they are looking at their own inventory
			if (event.getView().getTitle().equals("")) return;
			//looking at anvil
			if (event.getView().getTitle().equals("Repair")) return;
			//crafting table
			if (event.getView().getTitle().equals("container.crafting")) return;
			//none of the allowed things match, just check
			if(check_kit(event)){
				player.sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to store it.");
				event.setCancelled(true);
				return;
			}

		}
	}

	private boolean check_kit(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		if (player == null || event.getCursor() == null || event.getCurrentItem() == null) return false;
		//if (event.getCursor().getTypeId() == 0 && event.getCurrentItem().getTypeId() != 0) {
		ItemMeta item = event.getCurrentItem().getItemMeta();
		if (item == null) return false;
		List<String> im = item.getLore();
		if (im == null) return false;
		if (!im.equals("")) {
			return true;
		}
		//}
		return false;
	}
}

