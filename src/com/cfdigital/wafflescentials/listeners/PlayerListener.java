package com.cfdigital.wafflescentials.listeners;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
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
	private ArrayList<String> fixedLines = new ArrayList<String>();
	private int linesPerPage = 9;

	public PlayerListener(WaffleScentials instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) return;
		final Player player = event.getPlayer();
		final String kickReason = event.getReason();
		event.setLeaveMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + player.getDisplayName() + " was kicked! Reason: " + kickReason);		
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {	

		String message;
		Player player = event.getPlayer();
		message = event.getMessage();
		
		//are they spamming?
		plugin.getWafflePlayer(player.getDisplayName()).incrMessageCount();
		int messageCount = plugin.getWafflePlayer(player.getDisplayName()).getMessageCount();
		if (messageCount >= Config.maxMessages) {
			event.setCancelled(true);
			if (messageCount >= Config.maxMessagesToKick) {
				player.kickPlayer("Chat spamming");
			}
			player.sendMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.RED + "You have been auto muted for spamming");
			Log.warn(messageCount);
			return;
		}
		long currTime = System.currentTimeMillis() / 1000L;


		//then see if any chat filtering
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
			plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + player.getName() + " is no longer AFK");
			afkPlayer.unsetAFK();
		}
		if (plugin.getWafflePlayer(player.getDisplayName()).isMuted()){
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this");
			event.setCancelled(true);
		}
		if (event.isCancelled()) return;
		afkPlayer.setLastActive(new Date().getTime());
		message = ChatColor.stripColor(message);
		//message = message.replace("%", "%%");		

		if (player.hasPermission("wscent.chat.colors")) {
			final char code = '&';
			message = ChatColor.translateAlternateColorCodes(code, message);
		}

		String playerName = player.getDisplayName();
		String prefix = WaffleScentials.getPrefix(player);
		String chatFormat = Config.chatFormat;
		String playerRank = plugin.getRank(player);

		chatFormat = chatFormat.replace("[player]", playerName);
		chatFormat = chatFormat.replace("[prefix]", prefix);
		chatFormat = chatFormat.replace("[rank]", playerRank);
		chatFormat = chatFormat.replace("&", "§");
		
		String format = event.getFormat();
		format = (format.replace("%1$s", chatFormat + ChatColor.RESET));
		
		event.setFormat(format);
		event.setMessage(message);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		int page = 1;
		String[] split = event.getMessage().split(" ");
		int count = split.length;
		int lastInput = count - 1;
		Player player = event.getPlayer();
		if (event.isCancelled()) return;
		PlayerClass afkPlayer = plugin.getWafflePlayer(player.getDisplayName());
		if (afkPlayer == null) return;
		afkPlayer.setLastActive(new Date().getTime());
		String command = event.getMessage();
		if (command.startsWith("/tell") || command.startsWith("/me") || command.startsWith("/msg") || command.startsWith("/reply")) {
			if (plugin.getWafflePlayer(player.getDisplayName()).isMuted()){
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this");
				event.setCancelled(true);
			}
		}

		if (command.equalsIgnoreCase("/pl") || command.startsWith("/plugins")) {
			player.sendMessage("Plugins (5): " + ChatColor.GREEN + "WaffleLib" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleShops" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleScentials" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleMobs" +
					ChatColor.WHITE + ", " + ChatColor.GREEN + "WaffleIRC");
			event.setCancelled(true);
			return;
		}

		if (command.startsWith("/version") || command.startsWith("/ver")) {
			player.sendMessage("This server is running on gerbils");
			event.setCancelled(true);
			return;

		}
		
		fixedLines.clear();
		if (command.startsWith("/help") || command.startsWith("/?") || command.startsWith("/rules") || command.startsWith("/register") || command.startsWith("/vote")) {
			String fileName = split[0] + ".txt";
			ArrayList<String> lines = new ArrayList<String>();
			lines = fileReader(fileName); 
			try{
				page = Integer.parseInt(split[lastInput]);
			}
			catch(Exception ex){
				page = 1;
			}
			linesProcess(player, command, page, false);
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		PlayerClass afkPlayer = plugin.getWafflePlayer(event.getPlayer().getDisplayName());
		event.setQuitMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + event.getPlayer().getName() + " quit ");
		if (afkPlayer == null) {
			WaffleLogger.severe(event.getPlayer().getDisplayName() + " Quit but was not in hashmap!");
		} else {
			afkPlayer.unsetAFK();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent Event) {
		Player player = Event.getPlayer();
		Event.setJoinMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + Event.getPlayer().getName() + " joined ");
		WaffleScentials.plugin.broadcastToPrivilaged(ChatColor.AQUA + ">> " + ChatColor.GRAY + player.getName() + " [" + player.getAddress() + "] logged in" , "wscent.chat.logininfo");
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
			plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY +  player.getName() + " is no longer AFK");
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
		}
	} 

	/*
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
	 */

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

	private ArrayList<String> fileReader(String fileName){

		ArrayList<String> tempLines = new ArrayList<String>();
		String folderName = plugin.getDataFolder().getParent();

		try{
			FileInputStream fis = new FileInputStream(folderName + "/WaffleScentials/text/" + fileName);
			Scanner scanner = new Scanner(fis, "UTF-8");
			while (scanner.hasNextLine()) {
				try{
					fixedLines.add(scanner.nextLine() + " ");
				}
				catch(Exception ex){
					WaffleLogger.severe(ex);
					fixedLines.add(" ");
				}
			}
			scanner.close();
			fis.close();
		}
		catch (Exception ex){
			WaffleLogger.severe(ex);
		}

		return tempLines;

	}

	private void variableSwap(Player player, ArrayList<String> lines){

		//Swapping out some variables with their respective replacement.
		for(String l : lines){

			//Basics
			String fixedLine = "";

			//Time Based

			//--> WorldTime
			double worldTime = player.getWorld().getTime() + 6000;
			double relativeTime = worldTime % 24000;
			long worldHours = (long) (relativeTime / 1000);
			long worldMinutes = (long) (((relativeTime % 1000) * 0.6) / 10); //I'm assuming this is how it works. lel.
			String worldMinutesResult = "";
			String worldTimeResult = "";

			if(worldMinutes < 10){
				worldMinutesResult = "0" + worldMinutes;
			}
			else{
				worldMinutesResult = worldMinutes + "";
			}

			if(worldHours >= 12){
				worldHours -= 12;
				worldTimeResult = worldHours + ":" + worldMinutesResult + " PM";
			}
			else{
				worldTimeResult = worldHours + ":" + worldMinutesResult + " AM";
			}

		}
	}

	private void linesProcess(Player player, String command, int page, boolean motd){
		//Define our page numbers
		int size = fixedLines.size();
		int pages;

		if(size % linesPerPage == 0){
			pages = size / linesPerPage;
		}
		else{
			pages = size / linesPerPage + 1;
		}

		//This here grabs the specified 9 lines, or if it's the last page, the left over amount of lines.
		String commandName = command.replace("/", "");
		commandName = commandName.toUpperCase();
		String header = "§m                                §r§d %current/%count §r§m                                §";

		if(pages != 1){

			header = header.replace("%commandname", commandName);
			header = header.replace("%current", Integer.toString(page));
			header = header.replace("%count", Integer.toString(pages));
			header = header.replace("%command", command);
			header = header + " ";

			player.sendMessage(header);
		}
		//Some math, magic, and wizards.
		int highNum = (page * linesPerPage);
		int lowNum = (page - 1) * linesPerPage;
		for (int number = lowNum; number < highNum; number++){
			if(number >= size){
				if(!motd && pages != 1){
					player.sendMessage(" ");
				}
			}
			else{
				player.sendMessage(fixedLines.get(number).replace("&", "§"));
			}

		}
	}


}

