package com.cfdigital.wafflescentials.mobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class MobConfig {

	private FileConfiguration config;
	public static File configFile;
	static YamlConfiguration ymlConfig;

	public boolean loadMobClasses() {
		configFile = new File(WaffleScentials.plugin.getDataFolder(), "mobclasses.yml");
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

		Map<String, Object> keys = config.getValues(true);
		for (String n : keys.keySet()) {
			if (n.startsWith("classes")) {
				String temp[] = n.split("\\.");
				if (temp.length == 2) {
					String classname = temp[1];
					String mob = config.getString("classes." + classname + ".Mob");
					String world = config.getString("classes." + classname + ".World");
					String weapon = config.getString("classes." + classname + ".Weapon");
					String helmet = config.getString("classes." + classname + ".Armor.Helmet");
					int helmetcolor = config.getInt("classes." + classname + ".Armor.ColorHelmet");
					String chest = config.getString("classes." + classname + ".Armor.Chest");
					int chestcolor = config.getInt("classes." + classname + ".Armor.ColorChest");
					String legs = config.getString("classes." + classname + ".Armor.Legs");
					int legscolor = config.getInt("classes." + classname + ".Armor.ColorLegs");
					String boots = config.getString("classes." + classname + ".Armor.Boots");
					int bootscolor = config.getInt("classes." + classname + ".Armor.ColorBoots");
					List<String> spawnbiomes = config.getStringList("classes." + classname + ".Biomes");
					List<String> immune = config.getStringList("classes." + classname + ".DamageImmune");
					List<String> drops = config.getStringList("classes." + classname + ".Drops");
					int groupSize = config.getInt("classes." + classname + ".GroupSize");
					int type = config.getInt("classes." + classname + ".Type");
					String effect = config.getString("classes." + classname + ".Effect");
					String damageeffect = config.getString("classes." + classname + ".DamageEffect");
					String reward = config.getString("classes." + classname + ".Reward");

					//Double reward = config.getDouble("classes." + classname + ".Reward");
					MobClass mobclass = new MobClass(boots, bootscolor, legs, legscolor, chest, chestcolor, helmet, helmetcolor, mob, weapon, world);
					Mobs.addMobClass(classname, mobclass);

					if (!drops.isEmpty()) {
						Mobs.mobClass.get(classname).setDrops(drops);
					}
					Mobs.mobClass.get(classname).setType(type);
					Mobs.mobClass.get(classname).setEffect(effect);
					Mobs.mobClass.get(classname).setDamageEffect(damageeffect);
					Mobs.mobClass.get(classname).setReward(reward);

					if (!immune.isEmpty()) {
						Mobs.mobClass.get(classname).setDamageImmune(immune);
					}

					if (!spawnbiomes.isEmpty()) {
						Mobs.mobClass.get(classname).setBiomeList(spawnbiomes);
					}

					Mobs.mobClass.get(classname).setBonusSpawn(groupSize);

					WaffleLogger.info("Learned class " + classname + " " + mob + " " + Mobs.mobClass.get(classname).getBiomeList() + " " + groupSize + " " + drops);

				}
			}
		}

		return true;
	}
}