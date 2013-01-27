package com.cfdigital.wafflescentials.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_4_6.WorldChunkManager;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_4_6.CraftWorld;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftSkeleton;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.cfdigital.wafflescentials.Config;
import com.cfdigital.wafflescentials.WaffleScentials;
import com.cfdigital.wafflescentials.mobs.MobEntity;
import com.cfdigital.wafflescentials.mobs.Mobs;

public class MobListener implements Listener {

	final WaffleScentials plugin;

	public MobListener(WaffleScentials instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damagee = event.getEntity();
		if (damagee instanceof Player) {
			if (damager instanceof LivingEntity) {
				List<MetadataValue> values = damager.getMetadata("class");
				String mobClass = null;
				for(MetadataValue value : values){
					mobClass = value.value().toString();
				}				
				Player dp = (Player) damagee;
				WaffleScentials.wafflePlayers.get(dp.getName()).setMobClassAttacker(mobClass);
				if (!Mobs.mobClass.containsKey(mobClass)) return;
				PotionEffectType effect = Mobs.mobClass.get(mobClass).getDamageEffect();
				if (!(effect == null)) {
					dp.addPotionEffect(new PotionEffect(effect, 500, 1));
				}
				
			}
		}
	}

	//entities damaged by the environment
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageEntity(EntityDamageEvent event) {
		final Entity target = event.getEntity();
		final DamageCause cause = event.getCause();
		if (!(target instanceof LivingEntity)) return;
		final LivingEntity le = (LivingEntity) target;

		if (Mobs.mob(le) != null ){
			final String mobClass = Mobs.mob(le).getMeta();
			if (!mobClass.isEmpty()){
				//check for immunities and cancel
				if (!Mobs.mobClass.get(mobClass).getDamageImmune().isEmpty()) {
					final List<String> checkDmg = Mobs.mobClass.get(mobClass).getDamageImmune();
					int x = 0;
					while (x < checkDmg.size()) {
						if (cause.equals(DamageCause.valueOf(checkDmg.get(x)))) {
							event.setCancelled(true);
							return;
						}
						x++;
					}
				}
			}
		}
	}
	/*
	 * TODO
	 * remove this hard coded shit

		if (target instanceof Monster) {
			if (cause.equals(DamageCause.ENTITY_EXPLOSION)) event.setCancelled(true);
		}

		if (target instanceof Creeper) {
			if ((cause.equals(DamageCause.PROJECTILE)) || cause.equals(DamageCause.CONTACT)) {
				if (!(attacker instanceof Skeleton)) {
					event.setCancelled(true);
				}
			}
		Log.warn(attacker.toString() + " " +cause);	
		}
	 */



	@EventHandler(priority = EventPriority.HIGH)
	public void creatureSpawn(CreatureSpawnEvent event) {
		//block all non plugin spawning in custom worlds
		SpawnReason reason = event.getSpawnReason();
		String spawnworld = event.getLocation().getWorld().getName();
		if (spawnworld == null) return;
		if (spawnworld.equalsIgnoreCase(Config.spawnControlWorlds)) { 		
			if (reason.equals(SpawnReason.NATURAL)){
				event.setCancelled(true);
				return;
			}
			if (reason.equals(SpawnReason.BREEDING)){
				event.setCancelled(true);
				return;
			}
			if (reason.equals(SpawnReason.EGG)){
				event.setCancelled(true);
				return;
			}
		}
		
		int x = event.getLocation().getBlockX();
		int z = event.getLocation().getBlockZ();
		WorldChunkManager biomeManager = ((CraftWorld) event.getLocation().getWorld()).getHandle().getWorldChunkManager();
		String spawnbiome = biomeManager.getBiome(x, z).y;
		EntityType entity = event.getEntityType();
		LivingEntity le = event.getEntity();
		Mobs.addMob(le);
		List<MetadataValue> values = le.getMetadata("class");
		for(MetadataValue value : values){
			  if (value.value().equals("")) return;
		  }

		MobEntity mob = Mobs.mob(le);
		String mobClass = null;
		if (Mobs.mobClass.size() == 0) return;

		//add specific rules to the mobs
		//should never happen, but check in case..

		if (mob == null) return;

		//list of things to chose from for rand, if spawn class matched
		List<String> rand = new ArrayList<String>();
		//default to all biomes if none are speced

		switch (entity) {
		case ZOMBIE:
			for (String ent : Mobs.mobClass.keySet()) {
				final EntityType et = Mobs.mobClass.get(ent).getMobEntity();
				if (et != null) {
					try {
						String mobWorld = Mobs.mobClass.get(ent).getWorld().getName();
						if (mobWorld.isEmpty()) mobWorld = "world";
						if (mobWorld.equals(spawnworld)) {
							if ((Mobs.mobClass.get(ent).getBiomeList().contains(spawnbiome)) || Mobs.mobClass.get(ent).getBiomeList().size() == 0) {
								if (et.equals(EntityType.ZOMBIE)) rand.add(ent);	
							}

						}
					} catch (NullPointerException ex) {

					}

				}
			}
			break;

		case SKELETON:
			for (String ent : Mobs.mobClass.keySet()) {
				final EntityType et = Mobs.mobClass.get(ent).getMobEntity();
				if (et != null) {	
					try {
						String mobWorld = Mobs.mobClass.get(ent).getWorld().getName();
						if (mobWorld.isEmpty()) mobWorld = "world";
						if (mobWorld.equals(spawnworld)) {
							if ((Mobs.mobClass.get(ent).getBiomeList().contains(spawnbiome)) || Mobs.mobClass.get(ent).getBiomeList().size() == 0) {
								if (et.equals(EntityType.SKELETON)) rand.add(ent);
							}
						}
					} catch (NullPointerException ex) {
					}
				}
			}
			break;

		case PIG_ZOMBIE:
			for (String ent : Mobs.mobClass.keySet()) {
				final EntityType et = Mobs.mobClass.get(ent).getMobEntity();
				if (et != null) {
					try {
						String mobWorld = Mobs.mobClass.get(ent).getWorld().getName();
						if (mobWorld.isEmpty()) mobWorld = "world";
						if (mobWorld.equals(spawnworld)) {
							if ((Mobs.mobClass.get(ent).getBiomeList().contains(spawnbiome)) || Mobs.mobClass.get(ent).getBiomeList().size() == 0) {
								if (et.equals(EntityType.PIG_ZOMBIE)) rand.add(ent);
							}
						}
					} catch (NullPointerException ex) {
					}
				}
			}
			break;

		default:
			//no matched classes
			return;
		}
		if (rand.size() == 0) {
			//bail out
			return;
		}
		//pick a random class to spawn
		Random randGen = new Random();
		int randNum = randGen.nextInt(rand.size());
		mobClass = rand.get(randNum);
		//hope to god this is not somehow null
		if (mobClass == null) return;
		mob.dressMonster(mobClass);	
		le.setMetadata("class", new FixedMetadataValue(plugin,mobClass));

		int type = Mobs.mobClass.get(mobClass).getType();
		if (entity.equals(EntityType.SKELETON)) {
			((CraftSkeleton)le).getHandle().setSkeletonType(type);
		}
		//add potion effect if present
		mob.setEffect(Mobs.mobClass.get(mobClass).getEffect());
		//this is a bonus mob, don't check it
		if (mob.isBonus()) return;

		//now, do the bonus spawns

		final int bonusSpawn = Mobs.mobClass.get(mobClass).getBonusSpawn();

		if (bonusSpawn != 0) {
			int a = 1;
			while (a <= bonusSpawn) {
				Mobs.spawnMob(event.getLocation(), WaffleScentials.plugin.getServer().getWorld(spawnworld), entity, mobClass);   		
				//very important to avoid endless spawning
				a++;
			}
		}
		//Log.warn(spawnworld + " " + spawnbiome + " " + entity + " " + mobClass + " " + biomelist );
	}

	public void spawnBonus(int bonus) {

	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void creatureDeath(EntityDeathEvent event) {
		LivingEntity le = event.getEntity();
		if (Mobs.mob(le) != null ){
			List<MetadataValue> values = le.getMetadata("class");
			String mobClass = null;
			for(MetadataValue value : values){
				mobClass = value.value().toString();
			}
			if (mobClass != null){
				if (Mobs.getMobClass(mobClass).getDrops().size() > 0) {
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
					if (!(le.getKiller() == null)) {
						event.getDrops().add(Mobs.getMobClass(mobClass).getDrops().get(0));
						float reward = Mobs.getMobClass(mobClass).getReward();
						le.getKiller().sendMessage(ChatColor.DARK_GRAY + "You gain " + reward + " for killing this " + mobClass);
						String name = le.getKiller().getName();
						if (WaffleScentials.economy.isEnabled()) { 
							if (WaffleScentials.economy.hasAccount(name)) {
								WaffleScentials.economy.bankDeposit(name, reward);
							}
						}
					}
				} else {
					//somehow there is metadata but no hashmap entry
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
				}
				
			} else {
				//stop drops from all mobs not in a class in spawn control worlds
				String spawnworld = event.getEntity().getWorld().getName();
				if (spawnworld == null) return;
				if (spawnworld.equalsIgnoreCase(Config.spawnControlWorlds)) {
					event.getEntity().getEquipment().clear();
					event.getDrops().clear();
				}
				
			}
		}
		Mobs.delMob(le);
		return;
	}

}


