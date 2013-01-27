package com.cfdigital.wafflescentials.listeners;

import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.PlayerClass;
import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class PlayerListener implements Listener	{

	final WaffleScentials plugin;

	public PlayerListener(WaffleScentials instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (event.isCancelled()) return;
		final Player player = event.getPlayer();
		final String kickReason = event.getReason();
		event.setLeaveMessage("§e" + player.getName() + " was kicked! Reason: " + kickReason);		
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{	
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
					}

					//replace
					if (filterAction.equalsIgnoreCase("REPLACE")) {
						message = ChatClass.replacePattern(message, regex, ChatClass.chatFilters.get(regex).filterReplace);
					}
				}
			}
		}


		PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(player.getName());
		if (afkPlayer == null) return;
		WaffleScentials.wafflePlayers.get(player.getName()).setLastActive(new Date().getTime());
		if (afkPlayer.isPlayerAFK()) {
			String myPrefix = WaffleScentials.getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			ChatClass.setTabName(player, myPrefix);
			WaffleScentials.plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getName() + "§f is no longer AFK");
			afkPlayer.unsetAFK();
		}
		if (WaffleScentials.wafflePlayers.get(player.getName()).isMuted()){
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

		String playerName = player.getName();
		String prefix = WaffleScentials.getPrefix(player);
		String chatFormat = Config.chatFormat;
		String playerRank = plugin.getRank(player);

		chatFormat = chatFormat.replace("%player%", playerName);
		chatFormat = chatFormat.replace("%prefix%", prefix);
		chatFormat = chatFormat.replace("%rank%", playerRank);
		chatFormat = chatFormat.replace("&", "§");
		chatFormat = chatFormat.replace("%message%", message);


		event.setFormat(chatFormat);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		if (event.isCancelled()) return;
		PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(player.getName());
		if (afkPlayer == null) return;
		afkPlayer.setLastActive(new Date().getTime());
		String command = event.getMessage();
		if (command.startsWith("/tell") || command.startsWith("/me") || command.startsWith("/msg") || command.startsWith("/reply")) {
			if (WaffleScentials.wafflePlayers.get(player.getName()).isMuted()){
				player.sendMessage(WaffleScentials.Prefix+"You are muted and cannot do this");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(event.getPlayer().getName());
		afkPlayer.unsetAFK();

	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent Event) 
	{
		Player player = Event.getPlayer();
		String myPrefix = WaffleScentials.getPrefix(player);
		if (myPrefix == null) return;
		myPrefix = myPrefix.replace("&", "§");
		ChatClass.setTabName(player, myPrefix);
		String playerName = player.getName();
		if (!WaffleScentials.wafflePlayers.containsKey(playerName)) {
			PlayerClass wafflePlayer = new PlayerClass(player);
			WaffleScentials.wafflePlayers.put(playerName, wafflePlayer);
			WaffleScentials.wafflePlayers.get(playerName).setLastActive(new Date().getTime());
		}
		else {
			WaffleScentials.wafflePlayers.get(playerName).setLastActive(new Date().getTime());
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPLayerMove(PlayerMoveEvent Event) {
		if (Event.isCancelled()) return;
		final Player player = Event.getPlayer();
		PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(player.getName());
		if (afkPlayer == null) return;
		afkPlayer.setLastActive(new Date().getTime());
		if (afkPlayer.isPlayerAFK()) {
			String myPrefix = WaffleScentials.getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			ChatClass.setTabName(player, myPrefix);
			WaffleScentials.plugin.getServer().broadcastMessage("§e** " + myPrefix + player.getName() + "§f is no longer AFK");
			afkPlayer.unsetAFK();
		}

	}




	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity().getPlayer();
		String pn = player.getDisplayName();
		String me = WaffleScentials.wafflePlayers.get(pn).getMobClassAttacker();
		if (me != "") {
			event.setDeathMessage(pn + " was slain by " + ChatColor.GREEN + me);
		}
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		ItemStack item =  event.getItem().getItemStack();
		List<String> im = item.getItemMeta().getLore();
		if (im == null) return;
		if (!im.isEmpty()) {
			if (im.get(1).contains(event.getPlayer().getName())) return;
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to pick it up.");
			event.getPlayer().sendMessage(ChatColor.GRAY + "The item will now despawn.");
			event.getItem().remove();
		}
	} 

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		PlayerClass afkPlayer = WaffleScentials.wafflePlayers.get(player.getName());
		afkPlayer.setLastActive(new Date().getTime());
		if (plugin.hasPermissions(player, "wscent.iconomy.death")) {
			String name = player.getName();
			if (WaffleScentials.economy.hasAccount(name)) {
				float rate = Config.getDeathTax(player.getWorld());
				if (rate < 1) {
					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + "There is no death penalty for this world.");
					return;
				}
				double amount = 0;
				amount = rate * WaffleScentials.economy.getBalance(name) / 100;
				if (WaffleScentials.economy.has(name, amount)) {
					WaffleScentials.economy.bankWithdraw(name, amount);

					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + WaffleScentials.economy.format(amount) + " (" + rate + "%) was taken from your account because you died.");
				} else {
					player.sendMessage(WaffleScentials.Prefix + ChatColor.RED + "Can't take money from you because you don't have enough money.");
				}
			} 
		}
	}

	@EventHandler (priority = EventPriority.NORMAL)
	public void onInventoryClick (InventoryClickEvent event) {
		if (!event.isCancelled()) {
			//Log.warn(event.getSlot() + " "+ event.getSlotType() + " " +event.getRawSlot());
			if (event.getSlotType().equals(SlotType.ARMOR)) return;
			if (event.getSlot() != event.getRawSlot()) {
				if (!((event.getRawSlot() - 36) == event.getSlot())) {
					if(check_kit(event, false)) return;
				}	
			} 
			if (event.getRawSlot() < 9 && event.getSlotType().equals(SlotType.CONTAINER)) {
				if(check_kit(event, true)) return;
			}
		}
	}
	
	private boolean check_kit(InventoryClickEvent event, boolean despawn) {
		Player player = (Player)event.getWhoClicked();
		if (player == null) return false;
		if (event.getCursor().getTypeId() == 0 && event.getCurrentItem().getTypeId() != 0) {
			ItemMeta item = event.getCurrentItem().getItemMeta();
			if (item == null) return true;
			List<String> im = item.getLore();
			if (im == null) return true;
			if (!im.isEmpty()) {
				if (despawn) {
					ItemStack is = new ItemStack(Material.AIR);
					event.setCurrentItem(is);
					player.sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to store it.");
				} else {
					player.sendMessage(ChatColor.GRAY + "This is a kit item and you are not permitted to store it.");
				}
				event.setCancelled(true);
				return true;
			}
		}
		return false;
	}
}

