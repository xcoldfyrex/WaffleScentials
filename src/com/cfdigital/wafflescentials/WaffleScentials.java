package com.cfdigital.wafflescentials;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.commands.Enchant;
import com.cfdigital.wafflescentials.commands.Home;
import com.cfdigital.wafflescentials.commands.Kit;
import com.cfdigital.wafflescentials.commands.Messaging;
import com.cfdigital.wafflescentials.commands.Spawn;
import com.cfdigital.wafflescentials.commands.Time;
import com.cfdigital.wafflescentials.commands.Weather;
import com.cfdigital.wafflescentials.commands.Whois;
import com.cfdigital.wafflescentials.listeners.BlockListener;
import com.cfdigital.wafflescentials.listeners.PlayerListener;
import com.cfdigital.wafflescentials.util.WaffleLogger;
import com.cfdigital.wafflescentials.warp.WarpCommands;
import com.cfdigital.wafflescentials.warp.WarpList;
import com.cfdigital.wafflescentials.warp.WarpSettings;
import com.cfidigital.lib.PatPeter.SQLibrary.Database;
import com.cfidigital.lib.PatPeter.SQLibrary.SQLite;

public class WaffleScentials extends JavaPlugin {

	public static WaffleScentials plugin;

	public static Permission perms = null;
	public static Chat chat = null;
	public static Economy economy = null;

	public WarpList warpList;
	public static String Prefix = "§6[§aWaffleScentials§6]§f ";

	public static HashMap<String, Kits> kits = new HashMap<String, Kits>();
	public static List<Player> dispenserWaiting = new ArrayList<Player>();
	public static HashMap<String, WafflePlayer> wafflePlayers = new HashMap<String, WafflePlayer>();
	public static java.util.Vector<Location> dispenserList = new java.util.Vector<Location>();

	public static Database db = null;

	private final Logger log = Logger.getLogger("Minecraft");

	public WaffleScentials() {
		plugin = this;
	}

	private final PlayerListener playerListener = new PlayerListener(this);
	private final BlockListener blockListener = new BlockListener(this);

	@Override
	public void onEnable() {
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
			getCommand("weather").setExecutor(new Weather(this));
			getCommand("enchant").setExecutor(new Enchant(this));
			getCommand("msg").setExecutor(new Messaging(this));
			getCommand("reply").setExecutor(new Messaging(this));
			getCommand("me").setExecutor(new Messaging(this));
			getCommand("say").setExecutor(new Messaging(this));
			getCommand("time").setExecutor(new Time(this));

			warpList = new WarpList(getServer());

			db = new SQLite(log, "WaffleScentials", "wafflescentials",
					"plugins/WaffleScentials/");
			String tables = "CREATE TABLE IF NOT EXISTS `homes` (`name` TEXT NOT NULL,`x` DOUBLE NOT NULL,`y` DOUBLE NOT NULL,`z` DOUBLE NOT NULL,`world` TEXT NOT NULL);";
			db.query(tables);
			Schedulers.setupTasks();
		} else {
			WaffleLogger.severe("Can not load config file!");
		}

		if (!setupChat()) {
			WaffleLogger.severe("Cannot setup chat!");
		}

		if (!setupEconomy()) {
			WaffleLogger.severe("Cannot setup economy!");
		}

		if (!setupPermissions()) {
			WaffleLogger.severe("Cannot setup permissions!");
		}

		for (Player player : getServer().getOnlinePlayers()) {	
			String myPrefix = getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			String pn = player.getDisplayName();
			if (pn.length() > 15)
				pn = pn.substring(0, 14);
			player.setPlayerListName(myPrefix + pn);
			LivingEntity le = (LivingEntity) player;
			le.setCustomName(getRank(player) + " " + player.getDisplayName());
			WafflePlayer wafflePlayer = new WafflePlayer(player);
			wafflePlayers.put(player.getName(), wafflePlayer);
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

	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		String[] trimmedArgs = args;
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}
		if (commandName.equalsIgnoreCase("mute") && trimmedArgs.length == 1) {
			if (hasPermissions(player, "wscent.chat.mute")) {
				if (getPlayer(trimmedArgs[0]) != null) {
					if (!getWafflePlayer(getPlayerName(trimmedArgs[0]))
							.isMuted()) {
						getPlayer(trimmedArgs[0]).sendMessage(
								ChatColor.AQUA + ">> " + ChatColor.GRAY
								+ "You have been muted");
						sender.sendMessage(ChatColor.AQUA + ">> "
								+ ChatColor.GRAY + "ou have muted "
								+ getPlayer(trimmedArgs[0]).getName());
						getWafflePlayer(getPlayerName(trimmedArgs[0]))
						.mutePlayer();
						return true;
					} else {
						getPlayer(trimmedArgs[0]).sendMessage(
								ChatColor.AQUA + ">> " + ChatColor.GRAY
								+ "You have been unmuted");
						sender.sendMessage(ChatColor.AQUA + ">> "
								+ ChatColor.GRAY + "You have unmuted "
								+ getPlayerName(trimmedArgs[0]));
						getWafflePlayer(getPlayerName(trimmedArgs[0]))
						.unmutePlayer();
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
				prefix = prefix.replace("&", "§");
				String playerName = person.getDisplayName();
				String AFK = "";
				if (getWafflePlayer(playerName).isPlayerAFK()) {
					AFK = "[AFK] ";
				}
				onlineList = onlineList + AFK + prefix + playerName + "§f, ";
			}
			sender.sendMessage("§6-------There are §b"
					+ getServer().getOnlinePlayers().length
					+ " §6creepers online-------");
			sender.sendMessage(onlineList);
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
			if (player == null)
				return true;
			if (hasPermissions(player, "wscent.heal")) {
				long lh = getWafflePlayer(player.getName()).getLastHeal();
				if (!hasEnoughTimePassed(lh, 300)) {
					sender.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY
							+ "You may not heal for another "
							+ getTimeLeft(lh, 300));
					return true;
				}
				player.setHealth(20);
				player.setFoodLevel(20);
				getWafflePlayer(player.getDisplayName()).setLastHeal(
						System.currentTimeMillis() / 1000L);
				sender.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY
						+ "Healed");
			}
			return true;
		}

		if (commandName.equalsIgnoreCase("afk")) {
			if (getWafflePlayer(player.getDisplayName()).isMuted()) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY
						+ "You are muted and cannot do this");
				return true;
			} else {
				if (hasPermissions(player, "wscent.chat.msg")) {
					String message = "AFK";
					if (trimmedArgs.length >= 1) {
						message = ChatClass.join(trimmedArgs, " ", 0);
					}
					String myPrefix = getPrefix(player);
					myPrefix = myPrefix.replace("&", "§");
					ChatClass.setTabName(player, "[AFK]" + myPrefix);
					getServer().broadcastMessage(
							ChatColor.LIGHT_PURPLE + ">> " + ChatColor.GRAY
							+ player.getName() + " is now AFK: "
							+ message);
					getWafflePlayer(player.getDisplayName()).setAFK(message);

					return true;
				}
			}
		}

		// unlimited dispenser
		if (commandName.equalsIgnoreCase("ud")) {
			if (!hasPermissions(player, "wscent.misc.ud"))
				return true;
			if (dispenserWaiting.contains(player)) {
				player.sendMessage(Prefix
						+ "§dStill waiting to register dispenser");
				return true;
			}
			dispenserWaiting.add(player);
			player.sendMessage(Prefix + "§dPunch the dispenser to register it");
			return true;
		}
		return false;
	}

	public boolean hasPermissions(Player p, String s) {
		if (p == null)
			return true;
		if (perms != null) {
			if (perms.has(p, s)) {
				return true;
			} else {
				p.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY
						+ "Insufficient permission for this command");
				return false;
			}
		} else {
			p.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY
					+ "Insufficient permission for this command");
			return false;
		}
	}

	public boolean hasPermissionsSilent(Player p, String s) {
		if (p == null)
			return true;
		if (perms != null) {
			return perms.has(p, s);
		} else {
			return false;
		}
	}

	public Player getPlayer(String playerName) {
		Player targetPlayer = getServer().getPlayer(playerName);
		if (targetPlayer != null)
			return targetPlayer;
		return null;
	}

	public WafflePlayer getWafflePlayer(String playerName) {
		if (wafflePlayers.containsKey(playerName)) {
			return wafflePlayers.get(playerName);
		}
		return null;
	}

	public String getPlayerName(String playerName) {
		String targetPlayer = getServer().getPlayer(playerName)
				.getDisplayName();
		if (targetPlayer != null)
			return targetPlayer;
		return null;
	}

	public static String getPrefix(Player player) {
		if (chat.getPlayerPrefix(player) == null)
			return "";
		return chat.getPlayerPrefix(player);
	}

	public String getRank(Player player) {
		return chat.getPrimaryGroup(player);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	public void setMetadata(Player player, String key, Object value) {
		player.setMetadata(key, new FixedMetadataValue(this, value));
	}

	public Object getMetadata(Player player, String key) {
		List<MetadataValue> values = player.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getDescription().getName()
					.equals(this.getDescription().getName())) {
				return value.value();
			}
		}
		return null;
	}

	public String getTimeLeft(long checkTime, int maxSeconds) {
		long CurrentTime = System.currentTimeMillis() / 1000L;
		long secondsLeft = (CurrentTime - (checkTime + maxSeconds)) * -1;
		int left = 0;
		int ss = 0;
		int mm = 0;
		int hh = 0;
		int dd = 0;
		left = (int) (secondsLeft);
		ss = left % 60;
		left = (int) left / 60;
		if (left > 0) {
			mm = left % 60;
			left = (int) left / 60;
			if (left > 0) {
				hh = left % 24;
				left = (int) left / 24;
				if (left > 0) {
					dd = left;
				}
			}
		}
		String diff = Integer.toString(mm) + "m " + Integer.toString(ss)
				+ "sec";
		return diff;
	}

	public boolean hasEnoughTimePassed(long checkTime, int maxSeconds) {
		long CurrentTime = System.currentTimeMillis() / 1000L;
		return !((CurrentTime - checkTime) <= maxSeconds);
	}

}
