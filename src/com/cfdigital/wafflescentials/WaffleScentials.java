package com.cfdigital.wafflescentials;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.commands.Mob;
import com.cfdigital.wafflescentials.listeners.*;
import com.cfdigital.wafflescentials.mobs.MobConfig;
import com.cfdigital.wafflescentials.mobs.Mobs;
import com.cfdigital.wafflescentials.util.WaffleLogger;
import com.cfdigital.wafflescentials.warp.WarpCommands;
import com.cfdigital.wafflescentials.warp.WarpList;
import com.cfdigital.wafflescentials.warp.WarpSettings;

public class WaffleScentials extends JavaPlugin {

	public static WaffleScentials plugin;
    public static Permission perms = null;
    public static Chat chat = null;
    public static Economy economy = null;
    public WarpList warpList;
    public static String Prefix = "§6[WaffleScentials]§f ";
    
    public static HashMap<String, PlayerClass> wafflePlayers = new HashMap<String, PlayerClass>();
    public static HashMap<String, Kits> kits = new HashMap<String, Kits>();
    public static List<Player> dispenserWaiting = new ArrayList<Player>(); 
	
	public WaffleScentials() {
		plugin = this;
	}
	
	private final PlayerListener playerListener = new PlayerListener(this);
	private final MobListener mobListener = new MobListener(this);
	private final BlockListener blockListener = new BlockListener(this);



	
	public void onEnable()
	{
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);
		pm.registerEvents(mobListener, this);
		pm.registerEvents(blockListener, this);

		
		if (setupChat()) {
			
		}
		
		if (setupEconomy()) {
			
		}
		if (setupPermissions()) {
			Config config = new Config();
			if (config.loadSettings()) {
				
			}
			
			MobConfig mobconfig = new MobConfig();
			if (mobconfig.loadMobClasses()) {
				
			}
			
			config.loadFilters();
			if (WarpSettings.loadSettings()) {
				
			}
		}
		
		for (Player player : getServer().getOnlinePlayers()) {
			String myPrefix = getPrefix(player);
			myPrefix = myPrefix.replace("&", "§");
			String pn = player.getName();
			if (pn.length() > 14) pn = pn.substring(0, 13);
			player.setPlayerListName(myPrefix+pn);
			PlayerClass wafflePlayer = new PlayerClass(player);
			wafflePlayers.put(player.getName(), wafflePlayer);
			wafflePlayers.get(player.getName()).setLastActive(new Date().getTime() + (Config.afkTimer * 1000));
		}
		
		getCommand("warp").setExecutor(new WarpCommands(this));
		getCommand("mob").setExecutor(new Mob(this));

		
        warpList = new WarpList(getServer());

        Schedulers.setupTasks();
        

	}
	
	public void onDisable() {
    	WaffleLogger.info("Cancelling tasks..");
		getServer().getScheduler().cancelAllTasks();
		Mobs.entityList.clear();
		WaffleLogger.info("Unloaded!");
	}
	
	
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            //log(Level.INFO, "no vault found!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }
    
    public static String getPrefix(Player player) {
    	if (chat.getPlayerPrefix(player) == null) return "";
    	return chat.getPlayerPrefix(player);
    }
    
    public String getRank(Player player) {
    	return chat.getPrimaryGroup(player);
    }
    
    public boolean hasPermissions(Player p, String s) {
    	if (p == null) return true;
        if (perms != null) {
        	return perms.has(p, s);
        } else {
        	return false;
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
           			if (!WaffleScentials.wafflePlayers.get(getPlayerName(trimmedArgs[0])).isMuted()) {
           				getPlayer(trimmedArgs[0]).sendMessage(Prefix+"§9You have been muted by " + sender.getName());
           				sender.sendMessage(Prefix+"§9You have muted " + getPlayer(trimmedArgs[0]).getName());
           				WaffleScentials.wafflePlayers.get(getPlayerName(trimmedArgs[0])).mutePlayer();
           				return true;
           			}
           			else {
           				getPlayer(trimmedArgs[0]).sendMessage(Prefix+"§9You have been unmuted by " + sender.getName());
           				sender.sendMessage(Prefix+"§9You have unmuted " + getPlayerName(trimmedArgs[0]));
           				WaffleScentials.wafflePlayers.get(getPlayerName(trimmedArgs[0])).unmutePlayer();
           				return true;
           			}
           		}
           		
           	}
           	else {
          		sender.sendMessage(Prefix+"§9No permissions..");
                return true;
           	}
        }
          
        // /list
        
        
        
        
        if (commandName.equalsIgnoreCase("list")) {
          	String onlineList = "";
           	for (Player person : getServer().getOnlinePlayers()) {
           		String prefix = getPrefix(person);
           		prefix=prefix.replace("&", "§");
           		String playerName = person.getName();
           		String AFK = "";
           		if (WaffleScentials.wafflePlayers.get(playerName).isPlayerAFK()) {
           			AFK = "[AFK] ";
           		}
           		onlineList = onlineList + AFK + prefix + playerName + "§f, ";
           	}
           	sender.sendMessage("§6-------There are §b" + getServer().getOnlinePlayers().length + " §6creepers online-------");
           	sender.sendMessage(onlineList);
           	return true;
        }
        
        if (commandName.equalsIgnoreCase("msg") && trimmedArgs.length >= 2) {
   			if (WaffleScentials.wafflePlayers.get(player.getName()).isMuted()) {
				player.sendMessage(WaffleScentials.Prefix+"§9You are muted and cannot do this");
   			} else {
   				if (hasPermissions(player, "wscent.chat.msg")) {
           			if((getPlayer(trimmedArgs[0]) != null) &&  getPlayer(trimmedArgs[0]).isOnline()) {
           				Player targetPlayer = getPlayer(trimmedArgs[0]);
           				String myPrefix = getPrefix(player);
           				String targetPrefix = getPrefix(targetPlayer);
           				myPrefix = myPrefix.replace("&", "§");
           				targetPrefix = targetPrefix.replace("&", "§");
           				targetPlayer.sendMessage(myPrefix + player.getName() + "§f -> me: " + ChatClass.join(trimmedArgs, " ", 1));
           				player.sendMessage("me -> " + targetPrefix + targetPlayer.getName() + "§f: " + ChatClass.join(trimmedArgs, " ", 1));
           				wafflePlayers.get(targetPlayer.getName()).setLastMessager(player.getName());
           				return true;
           			}
           			else {
           				player.sendMessage(Prefix + "§9Player is not online!");
           				return true;
           			}
           		}
           	}
        }
        
        if (commandName.equalsIgnoreCase("me") && trimmedArgs.length >= 1) {
   			if (WaffleScentials.wafflePlayers.get(player.getName()).isMuted()) {
				player.sendMessage(WaffleScentials.Prefix+"§9You are muted and cannot do this");
				return true;
   			} else {
   				if (hasPermissions(player, "wscent.chat.msg")) {
          			String myPrefix = getPrefix(player);
	           		myPrefix = myPrefix.replace("&", "§");
           			getServer().broadcastMessage("§e** " + myPrefix + player.getName() + "§f " + ChatClass.join(trimmedArgs, " ", 0));
           			return true;
           		}
           	}
        }
        
        // /say
        
        if (commandName.equalsIgnoreCase("say") && trimmedArgs.length >= 1) {
			if (hasPermissions(player, "wscent.chat.broadcast")) {
				String message = ChatClass.join(trimmedArgs, " ", 0);
				String pn = "[CONSOLE]";
				if (!(player == null))pn = player.getDisplayName();
				this.getServer().broadcastMessage(ChatColor.GOLD + "[" + ChatColor.RED + "Global message from " + pn + ChatColor.GOLD + "]");
				this.getServer().broadcastMessage(message);
				this.getServer().broadcastMessage(ChatColor.GOLD + "----------- End of broadcast -----------");
			}
			return true;
        }
        
        // /kit
        if (commandName.equalsIgnoreCase("kit")) {
        	if (trimmedArgs.length == 0) {
        		if (hasPermissions(player, "wscent.kits")) {
    				sender.sendMessage(Prefix + "Kits:");
    				for (String kit : kits.keySet()) {
    					sender.sendMessage(kit);
    				}
    			}
        	} 
        	else {
        		if (kits.containsKey(trimmedArgs[0])) {
            		if (hasPermissions(player, "wscent.kits."+trimmedArgs[0])) {
            			long lastUsed = WaffleScentials.wafflePlayers.get(player.getName()).getKitLastUsed(trimmedArgs[0]);
            			int coolDown = kits.get(trimmedArgs[0]).getCoolDown();
            			if ((System.currentTimeMillis() / 1000L - lastUsed) < coolDown) {
                			sender.sendMessage(Prefix + "You used this kit too recently!");
                			return true;
            			}
            			WaffleScentials.wafflePlayers.get(player.getName()).setKitLastUsed(trimmedArgs[0], System.currentTimeMillis() / 1000L);
            			List<ItemStack> items = new ArrayList<ItemStack>();
            			items.addAll(kits.get(trimmedArgs[0]).getItemStack());
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
            			sender.sendMessage(Prefix + "you have been given kit: " + trimmedArgs[0]);
            		}
            		else {
            			sender.sendMessage(Prefix + "no permission to spawn this kit!");
            		}
        			
        			
        		}
        		else {
        			sender.sendMessage(Prefix + "No such kit!");
        		}
        	}
			return true;
        }
        
        // /heal
        if (commandName.equalsIgnoreCase("heal")) {
        	if (player == null) return true;
			if (hasPermissions(player, "wscent.heal")) {
				long lh = wafflePlayers.get(player.getName()).getLastHeal();
				long ch = System.currentTimeMillis() / 1000L;
				if ((ch - lh) <= 300) {
					sender.sendMessage(Prefix + "You cannot heal this often!");
					return true;
				}
				player.setHealth(20);
				player.setFoodLevel(20);
				wafflePlayers.get(player.getName()).setLastHeal(ch);
				sender.sendMessage(Prefix + "Healed!");
			}
			return true;
        }
        
        
        if (commandName.equalsIgnoreCase("afk")) {
   			if (WaffleScentials.wafflePlayers.get(player.getName()).isMuted()) {
				player.sendMessage(WaffleScentials.Prefix+"§9You are muted and cannot do this");
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
           			getServer().broadcastMessage("§e** " + myPrefix + player.getName() + "§f is now AFK: " + message);
           			PlayerClass afkPlayer = wafflePlayers.get(player.getName());
           			afkPlayer.setAFK(message);
           			return true;
           		}
           	}
        }
        
        
        if ((commandName.equalsIgnoreCase("scream"))) {
			player.getWorld().playEffect(player.getLocation(), Effect.RECORD_PLAY , Material.RECORD_11, 1000);
			
			for (World world : Bukkit.getServer().getWorlds()) {
        		for (Entity e : world.getEntities()) {
        			if (e.isDead())
        				continue;
        			
        			if (e instanceof Skeleton) {
        				//Player testPlayer;
        				String fake = String.valueOf(e.getEntityId());
        				((Player)e).setPlayerListName(fake);
        				
        			}

        		}
       		}
			
			return true;
        }
        if ((commandName.equalsIgnoreCase("r") || commandName.equalsIgnoreCase("reply")) && trimmedArgs.length >= 1) {
   			if (WaffleScentials.wafflePlayers.get(getPlayerName(player.getName())).isMuted()) {
				player.sendMessage(WaffleScentials.Prefix+"§9You are muted and cannot do this");
   			} else {
   				if (hasPermissions(player, "wscent.chat.msg")) {
   					String lastMessager = null;
   					if ((lastMessager = wafflePlayers.get(player.getName()).getLastMessager()) != null) {
   						if (getPlayer(lastMessager).isOnline()) {
   	           				Player targetPlayer = getPlayer(lastMessager);
   	           				String myPrefix = getPrefix(player);
   	           				String targetPrefix = getPrefix(targetPlayer);
   	           				myPrefix = myPrefix.replace("&", "§");
   	           				targetPrefix = targetPrefix.replace("&", "§");
   	           				targetPlayer.sendMessage(myPrefix + player.getName() + "§f -> me: " + ChatClass.join(trimmedArgs, " ",0));
   	           				player.sendMessage("me -> " + targetPrefix + targetPlayer.getName() + "§f: " + ChatClass.join(trimmedArgs, " ", 0));
   	           				wafflePlayers.get(targetPlayer.getName()).setLastMessager(player.getName());
   	           				return true;
   						}
   						else {
   	           				player.sendMessage(Prefix + "§9Player is not online!");
   	           				return true;
   						}
   						
   					}
   				}
   				
   			}
        	
        }
        
        //unlimited dispenser
        if (commandName.equalsIgnoreCase("ud")) {
			if (!hasPermissions(player, "wscent.misc.ud")) return true;
        	if (dispenserWaiting.contains(player)) {
            	player.sendMessage(Prefix+"Still waiting to register dispenser");
            	return true;
        	}
        	dispenserWaiting.add(player);
        	player.sendMessage(Prefix+"Punch the dispenser");
        	return true;
        } 
        return false;
    }
    
    
        
    
    public Player getPlayer(String playerName) {
		Player targetPlayer = getServer().getPlayer(playerName);
		if (targetPlayer != null) return targetPlayer;
    	return null;
    }
    
    public String getPlayerName(String playerName) {
		String targetPlayer = getServer().getPlayer(playerName).getName();
		if (targetPlayer != null) return targetPlayer;
    	return null;
    }
    
    public boolean isAnimal(Entity e){
    	return e instanceof Animals;
    }
    
    public boolean isAnimalChicken(Entity e){
    	return e instanceof Chicken;
    }
    
    public boolean isAnimalCow(Entity e){
    	return e instanceof Cow;
    }
    
    public boolean isAnimalVillager(Entity e){
    	return e instanceof Villager;
    }
    
    public boolean isMonster(Entity entity){
    	return entity instanceof Monster;
    }
    
    public boolean isMonsterCreeper(Entity entity){
    	if (entity.toString().equals("CraftCreeper")) return true;
    	return false;
    }
    public boolean isMonsterZombie(Entity entity){
    	if (entity.toString().equals("CraftZombie")) return true;
    	return false;
    }
    public boolean isMonsterSpider(Entity entity){
    	if (entity.toString().equals("CraftSpider")) return true;
    	return false;
    }
    public boolean isMonsterCaveSpider(Entity entity){
    	if (entity.toString().equals("CraftCavespider")) return true;
    	return false;
    }
}
