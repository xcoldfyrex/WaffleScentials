package com.cfdigital.wafflescentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.cfdigital.wafflescentials.chat.ChatClass;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class Config extends JavaPlugin {

	private FileConfiguration config;
	public static File configFile;
	static YamlConfiguration ymlConfig;

	public static String chatFormat;
	public static int afkTimer = 300;
	public static int maxMessages = 3;
	public static int maxMessagesToKick = 5;
	public static int maxChatTime = 3;
	private static HashMap<World, Float> deathTax = new HashMap<World, Float>();

	public boolean loadSettings() {
		WaffleScentials.kits.clear();
		configFile = new File(WaffleScentials.plugin.getDataFolder(), "config.yml");
		config = WaffleScentials.plugin.getConfig();

		if (!WaffleScentials.plugin.getDataFolder().exists()) {
			WaffleScentials.plugin.getDataFolder().mkdirs();
		}

		try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		chatFormat = config.getString("chatFormat"); 
		Map<String, Object> keys = config.getValues(true);
		for (String n : keys.keySet()) {
			if (n.startsWith("kits")) {
				String temp[] = n.split("\\.");
				if (temp.length == 2) {
					String kitname = temp[1];
					int limit = config.getInt("kits." + kitname + ".cooldown");
					String items = config.getString("kits." + kitname + ".items");
					Kits kit = new Kits(limit, items);
					WaffleScentials.kits.put(kitname, kit);
				}
			}
			if (n.startsWith("deathtax")) {
				String temp[] = n.split("\\.");
				if (temp.length == 2) {
					String world = temp[1];
					String tax = config.getString("deathtax." + world + ".percent");
					deathTax.put(WaffleScentials.plugin.getServer().getWorld(world), Float.valueOf(tax));
					WaffleLogger.info("Added tax of " + tax + " to " + world);
				}
			}
		}

		return true;

	}

	public void loadFilters(){
		WaffleLogger.info("Loading chat filters");
		try {
			BufferedReader input =  new BufferedReader(new FileReader(WaffleScentials.plugin.getDataFolder()+"/filters.txt"));
			String line = null;
			while (( line = input.readLine()) != null) {
				line = line.trim();
				if (!line.matches("^#.*") && !line.matches("")) {
					if (line.startsWith("match ") || line.startsWith("replace ")) {
						String[] parts = line.split(" ");
						String filterReplace = "";
						if (parts.length == 4) filterReplace = parts[3]; 
						ChatClass.addPattern(parts[2],parts[1], filterReplace);
					}
				}
			}
			input.close();
		}
		catch (FileNotFoundException e) {
			WaffleLogger.warning("Cannot find filters.txt");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static float getDeathTax(World world) {
		if (deathTax.containsKey(world)) return deathTax.get(world);
		return 0;
	}
}