package com.cfdigital.wafflescentials.mobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import org.bukkit.potion.PotionEffectType;

import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.util.WaffleLogger;

public class MobClass {
	
	public MobClass (String boots, int bootsColor, String pants, int pantsColor, String chest, int chestColor, String helmet, int helmetColor, String mob, String weapon, String world) {
		if (mob.equalsIgnoreCase("WitherSkeleton")) {
			mob = "Skeleton";
			
		}

		EntityType mobEntity = EntityType.fromName(mob);
		this.mob = mobEntity;
		if (world != null) {
			//will never spawn if not set as this defaults to "world"
			this.setWorld(WaffleScentials.plugin.getServer().getWorld(world));
		}
		Map<Enchantment,Integer> enchantmentMap = new HashMap<Enchantment,Integer>();
		
		//do the boots
		if (!(boots == null)) {
			if (boots.contains(" ")) {
				String itemList[] = boots.split(" ");
				boots = itemList[0];
				String enchantmentList[] = itemList[1].split(";");
				for (String ench : enchantmentList) {
					String enchantment[] = ench.split(",");
					enchantmentMap.put(Enchantment.getByName(enchantment[0]), Integer.valueOf(enchantment[1]));
				}
			}
			this.boots = (new ItemStack(Material.getMaterial(boots)));
			setColor(this.boots, bootsColor);
		
			if (!enchantmentMap.isEmpty()) {
				this.boots.addUnsafeEnchantments(enchantmentMap);
				enchantmentMap.clear();
			}
		}
		
		//do the pants
		if (!(pants == null)) {
			if (pants.contains(" ")) {
				String itemList[] = pants.split(" ");
				pants = itemList[0];
				String enchantmentList[] = itemList[1].split(";");
				for (String ench : enchantmentList) {
					String enchantment[] = ench.split(",");
					enchantmentMap.put(Enchantment.getByName(enchantment[0]), Integer.valueOf(enchantment[1]));
				}
			}
		
			this.pants = (new ItemStack(Material.getMaterial(pants)));
			setColor(this.pants, pantsColor);

			if (!enchantmentMap.isEmpty()) {
				this.pants.addUnsafeEnchantments(enchantmentMap);
				enchantmentMap.clear();
			}
		}
		//do the chest
		if (!(chest == null)) {
			if (chest.contains(" ")) {
				String itemList[] = chest.split(" ");
				chest = itemList[0];
				String enchantmentList[] = itemList[1].split(";");
				for (String ench : enchantmentList) {
					String enchantment[] = ench.split(",");
					enchantmentMap.put(Enchantment.getByName(enchantment[0]), Integer.valueOf(enchantment[1]));
				}
			}
			this.chest = (new ItemStack(Material.getMaterial(chest)));
			setColor(this.chest, chestColor);
			
			if (!enchantmentMap.isEmpty()) {
				this.chest.addUnsafeEnchantments(enchantmentMap);
				enchantmentMap.clear();
			}
		}
		//do the helmet
		if (!(helmet== null)) {
			if (helmet.contains(" ")) {
				String itemList[] = helmet.split(" ");
				helmet = itemList[0];
				String enchantmentList[] = itemList[1].split(";");
				for (String ench : enchantmentList) {
					String enchantment[] = ench.split(",");
					enchantmentMap.put(Enchantment.getByName(enchantment[0]), Integer.valueOf(enchantment[1]));
				}
			}
			this.helmet = (new ItemStack(Material.getMaterial(helmet)));
			setColor(this.helmet, helmetColor);

			if (!enchantmentMap.isEmpty()) {
				this.helmet.addUnsafeEnchantments(enchantmentMap);
				enchantmentMap.clear();
			}
		}
		if (weapon.contains(" ")) {
			String itemList[] = weapon.split(" ");
			weapon = itemList[0];
			String enchantmentList[] = itemList[1].split(";");
			for (String ench : enchantmentList) {
				String enchantment[] = ench.split(",");
				enchantmentMap.put(Enchantment.getByName(enchantment[0]), Integer.valueOf(enchantment[1]));
			}

		}
		
		this.weapon = (new ItemStack(Material.getMaterial(weapon)));
		if (!enchantmentMap.isEmpty()) {
			this.weapon.addUnsafeEnchantments(enchantmentMap);
			enchantmentMap.clear();
		}

	/*
		if (
			(!(mobEntity == EntityType.ZOMBIE)) || 
			(!(mobEntity == EntityType.PIG_ZOMBIE)) || 
			(!(mobEntity == EntityType.SKELETON))
		) return;
		*/
		
	}
	
	private void setColor(ItemStack item, int color) {
		switch (item.getType()) {
			case LEATHER_BOOTS:
				break;
			case LEATHER_CHESTPLATE:
				break;
			case LEATHER_LEGGINGS:
				break;
			case LEATHER_HELMET:
				break;
			default:
				return;
		}

		LeatherArmorMeta im = (LeatherArmorMeta) item.getItemMeta();
		Color realColor = Color.fromRGB(color);
		im.setColor(realColor);
		item.setItemMeta(im);
	}
		
	public ItemStack getBoots() {
		return this.boots;
	}
	
	public ItemStack getPants() {
		return this.pants;
	}
	
	public ItemStack getChestPlate() {
		return this.chest;
	}
	
	public ItemStack getHelmet() {
		return this.helmet;
	}
	
	//@SuppressWarnings("null")
	public ItemStack[] getArmorSet() {
		ItemStack[] armorSet;
		armorSet = new ItemStack[10];
		armorSet[0] = this.boots;
		armorSet[1] = this.pants;
		armorSet[2] = this.chest;
		armorSet[3] = this.helmet;		
		return armorSet;
	}
	
	public ItemStack getWeapon() {
		return this.weapon;
	}
	public float getReward() {
		return this.reward;
	}
	
	public void setReward(String reward2) {
		if (reward2 == null) {
			this.reward = 0;
			return;
		}
		this.reward = Float.valueOf(reward2);
	}
	public void setEnchantment (Enchantment enchantment, int level, ItemStack item) {
		if ((level > 126) || (level < 1)) return;
		if (item == null) return;
		item.addUnsafeEnchantment(enchantment, level);
	}
	
	public EntityType getMobEntity() {
		return this.mob;
	}

	private void setWorld(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public void setBiomeList(List<String> biomeList) {
		this.biomeList = biomeList;
	}

	public List<String> getBiomeList() {
		return this.biomeList;
	}

	public void setDamageImmune(List<String> damageImmune) {
		this.damageImmune = damageImmune;
	}

	public List<String> getDamageImmune() {
		return damageImmune;
	}

	public void setBonusSpawn(int bonusSpawn) {
		this.bonusSpawn = bonusSpawn;
	}

	public int getBonusSpawn() {
		return bonusSpawn;
	}

	public void setDrops(List<String> drops) {
		for (int i = 0; i < drops.size(); i++) {
			ItemStack is = new ItemStack(Material.getMaterial(drops.get(i)));
			this.drops.add(is);
		}
		
	}

	public List<ItemStack> getDrops() {
		return drops;
	}

	public void setEffect(String potionEffect) {
		if (potionEffect == null) return;
		this.effectType = PotionEffectType.getByName(potionEffect);
		if (this.effectType == null) {
			WaffleLogger.warning("Potion " + potionEffect + " is invalid!");
		}
		Log.warn(this.effectType);
	}

	public PotionEffectType getEffect() {
		return this.effectType;
	}

	public void setDamageEffect(String effect) {
		if (effect == null) return;

		this.damageEffect = PotionEffectType.getByName(effect);
	}

	public PotionEffectType getDamageEffect() {
		return damageEffect;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	private EntityType mob;
	private ItemStack weapon;
	private ItemStack boots;
	private ItemStack pants;
	private ItemStack chest;
	private ItemStack helmet;
	private float reward = 0;
	private World world = WaffleScentials.plugin.getServer().getWorld("spawn");
	private List<String> biomeList = new ArrayList<String>();
	private List<String> damageImmune = new ArrayList<String>();
	private List<ItemStack> drops = new ArrayList<ItemStack>();
	private int bonusSpawn = 0;
	private PotionEffectType effectType;
	private PotionEffectType damageEffect;
	private int type = 0;
	
	
}