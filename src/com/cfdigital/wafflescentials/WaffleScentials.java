package com.cfdigital.wafflescentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.commands.*;
import com.cfdigital.wafflescentials.listeners.*;
import com.cfdigital.wafflescentials.util.WaffleLogger;
import com.cfdigital.wafflescentials.warp.WarpCommands;
import com.cfdigital.wafflescentials.warp.WarpList;
import com.cfdigital.wafflescentials.warp.WarpSettings;
import com.cfidigital.lib.PatPeter.SQLibrary.Database;
import com.cfidigital.lib.PatPeter.SQLibrary.SQLite;

public class WaffleScentials extends JavaPlugin {

	public static WaffleScentials plugin;

	public WarpList warpList;
	public static String Prefix = "§6[§aWaffleScentials§6]§f ";

	public static HashMap<String, Kits> kits = new HashMap<String, Kits>();
	public static List<Player> dispenserWaiting = new ArrayList<Player>(); 

	public static Database db = null;

	private final Logger log = Logger.getLogger("Minecraft");

	public WaffleScentials() {
		plugin = this;
	}

	private final PlayerListener playerListener = new PlayerListener(this);
	private final BlockListener blockListener = new BlockListener(this);

	@Override
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);

		Config config = new Config();
		if (config.loadSettings()) {
			config.loadFilters();
			WarpSettings.loadSettings();
			getCommand("warp").setExecutor(new WarpCommands(this));
			getCommand("kit").setExecutor(new Kit(this));
			getCommand("whois").setExecutor(new Whois(this));
			getCommand("home").setExecutor(new Home(this));
			getCommand("spawn").setExecutor(new Spawn(this));
			getCommand("enchant").setExecutor(new Enchant(this));
			getCommand("msg").setExecutor(new Messaging(this));
			getCommand("reply").setExecutor(new Messaging(this));

			warpList = new WarpList(getServer());

			db = new SQLite(log, "WaffleScentials","wafflescentials","plugins/WaffleScentials/");
			String tables = "CREATE TABLE IF NOT EXISTS `homes` (`name` TEXT NOT NULL,`x` DOUBLE NOT NULL,`y` DOUBLE NOT NULL,`z` DOUBLE NOT NULL,`world` TEXT NOT NULL);";
			db.query(tables);
			Schedulers.setupTasks();
		} else {
			WaffleLogger.severe("Can not load config file!");
		}

		for (Player player : getServer().getOnlinePlayers()) {
			String myPrefix = getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			String pn = player.getDisplayName();
			if (pn.length() > 14) pn = pn.substring(0, 13);
			player.setPlayerListName(myPrefix+pn);
			LivingEntity le = (LivingEntity) player;
			le.setCustomName(getRank(player) + " " + player.getDisplayName());
		}
	}

	@Override
	public void onDisable() {
		ChatClass.chatFilters.clear();
		plugin.getServer().getScheduler().cancelTasks(plugin);
	}

	public void broadcastToPrivilaged(String message, String perms) {
		for (Player player : getServer().getOnlinePlayers()) {
			if (hasPermissionsSilent(player, perms)) {
				player.sendMessage(message);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (commandName.equalsIgnoreCase("mute") && trimmedArgs.length == 1) {
			if (hasPermissions(player, "wscent.chat.mute")) {
				if (getPlayer(trimmedArgs[0]) != null) {
					if (!getWafflePlayer(getPlayerName(trimmedArgs[0])).isMuted()) {
						getPlayer(trimmedArgs[0]).sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have been muted");
						sender.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "ou have muted " + getPlayer(trimmedArgs[0]).getName());
						getWafflePlayer(getPlayerName(trimmedArgs[0])).mutePlayer();
						return true;
					}
					else {
						getPlayer(trimmedArgs[0]).sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have been unmuted");
						sender.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have unmuted " + getPlayerName(trimmedArgs[0]));
						getWafflePlayer(getPlayerName(trimmedArgs[0])).unmutePlayer();
						return true;
					}
				}

			} else {
				return true;
			}
		}

		// /list
		if (commandName.equalsIgnoreCase("list")) {
			String onlineList = "";
			for (Player person : getServer().getOnlinePlayers()) {
				String prefix = getPrefix(person);
				prefix=prefix.replace("&", "§");
				String playerName = person.getDisplayName();
				String AFK = "";
				if (getWafflePlayer(playerName).isPlayerAFK()) {
					AFK = "[AFK] ";
				}
				onlineList = onlineList + AFK + prefix + playerName + "§f, ";
			}
			sender.sendMessage("§6-------There are §b" + getServer().getOnlinePlayers().length + " §6creepers online-------");
			sender.sendMessage(onlineList);
			return true;
		}



		// /me
		if (commandName.equalsIgnoreCase("me") && trimmedArgs.length >= 1) {
			if (getWafflePlayer(player.getName()).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this!");
				return true;
			} else {
				if (hasPermissions(player, "wscent.chat.msg")) {
					String myPrefix = getPrefix(player);
					myPrefix = myPrefix.replace("&", "§");
					getServer().broadcastMessage("§e** " + myPrefix + player.getDisplayName() + "§f " + ChatClass.join(trimmedArgs, " ", 0));
					return true;
				}
			}
		}

		// /say
		if (commandName.equalsIgnoreCase("say") && trimmedArgs.length >= 1) {
			if (hasPermissions(player, "wscent.chat.broadcast")) {
				String message = ChatClass.join(trimmedArgs, " ", 0);
				String pn = "CONSOLE";
				if (!(player == null))pn = player.getName();
				this.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + pn + ChatColor.LIGHT_PURPLE + "] " + message);
			}
			return true;
		}

		// /weather
		if (commandName.equalsIgnoreCase("weather") && trimmedArgs.length == 1) {
			if (hasPermissions(player, "wscent.world.weather")) {
				boolean newState = false;
				if (trimmedArgs[0].equalsIgnoreCase("clear")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Changed weather to clear!");
					newState = false;
				}
				else if (trimmedArgs[0].equalsIgnoreCase("rain")) {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Changed weather to storm! You dick!");
					newState = true;
				}

				else {
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + " [rain] | [clear]");
				}
				player.getWorld().setStorm(newState);
			}
			return true;
		}

		// /skull
		if (commandName.equalsIgnoreCase("skull") && trimmedArgs.length == 1) {
			if (hasPermissions(player, "wscent.misc.skull")) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
				ItemMeta itemMeta = item.getItemMeta();
				((SkullMeta) itemMeta).setOwner(trimmedArgs[0]);
				item.setItemMeta(itemMeta);
				player.getWorld().dropItemNaturally(player.getLocation(), item);
			}
			return true;
		}

		// /heal
		if (commandName.equalsIgnoreCase("heal")) {
			if (player == null) return true;
			if (hasPermissions(player, "wscent.heal")) {
				long lh = getWafflePlayer(player.getName()).getLastHeal();
				long ch = System.currentTimeMillis() / 1000L;
				if ((ch - lh) <= 300) {
					sender.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You cannot heal this often");
					return true;
				}
				player.setHealth(20);
				player.setFoodLevel(20);
				getWafflePlayer(player.getDisplayName()).setLastHeal(ch);
				sender.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Healed");
			}
			return true;
		}


		if (commandName.equalsIgnoreCase("afk")) {
			if (getWafflePlayer(player.getDisplayName()).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You are muted and cannot do this");
				return true;
			} else {
				if (hasPermissions(player, "wscent.chat.msg")) {
					String message = "AFK";
					if (trimmedArgs.length >= 1) {
						message = ChatClass.join(trimmedArgs, " ", 0);
					}
					String myPrefix = getPrefix(player);
					myPrefix = myPrefix.replace("&", "§");
					ChatClass.setTabName(player, "[AFK]"+myPrefix);
					getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY + player.getName() + " is now AFK: " + message);
					getWafflePlayer(player.getDisplayName()).setAFK(message);

					return true;
				}
			}
		}


		//unlimited dispenser
		if (commandName.equalsIgnoreCase("ud")) {
			if (!hasPermissions(player, "wscent.misc.ud")) return true;
			if (dispenserWaiting.contains(player)) {
				player.sendMessage(Prefix+"§dStill waiting to register dispenser");
				return true;
			}
			dispenserWaiting.add(player);
			player.sendMessage(Prefix+"§dPunch the dispenser to register it");
			return true;
		} 
		return false;
	}

	public boolean hasPermissions(Player p, String s) {
		if (p == null) return true;
		if (WaffleLib.perms != null) {
			if (WaffleLib.perms.has(p, s)) {
				return true;
			} else {
				p.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Insufficient permission for this command");
				return false;
			}
		} else {
			p.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Insufficient permission for this command");
			return false;
		}
	}
	
	public boolean hasPermissionsSilent(Player p, String s) {
		if (p == null) return true;
		if (WaffleLib.perms != null) {
			return WaffleLib.perms.has(p, s);
		} else {
			return false;
		}
	}

	public Player getPlayer(String playerName) {
		Player targetPlayer = getServer().getPlayer(playerName);
		if (targetPlayer != null) return targetPlayer;
		return null;
	}

	public PlayerClass getWafflePlayer(String playerName) {
		if (WaffleLib.wafflePlayers.containsKey(playerName)) {
			return WaffleLib.wafflePlayers.get(playerName);
		}
		return null;
	}

	public String getPlayerName(String playerName) {
		String targetPlayer = getServer().getPlayer(playerName).getDisplayName();
		if (targetPlayer != null) return targetPlayer;
		return null;
	}

	public static String getPrefix(Player player) {
		if (WaffleLib.chat.getPlayerPrefix(player) == null) return "";
		return WaffleLib.chat.getPlayerPrefix(player);
	}

	public String getRank(Player player) {
		return WaffleLib.chat.getPrimaryGroup(player);
	}

}
