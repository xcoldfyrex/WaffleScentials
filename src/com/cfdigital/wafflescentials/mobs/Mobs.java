package com.cfdigital.wafflescentials.mobs;

import java.util.ConcurrentModificationException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Mobs {

	public static HashMap<LivingEntity,MobEntity> entityList = new HashMap<LivingEntity,MobEntity>(); 
	public static HashMap<String, MobClass> mobClass = new HashMap<String,MobClass>();

	public static void spawnMob(Location loc, World world, EntityType entity, String mobClass) {
		if (world == null) return;
		if (entity == null) {
			Log.warn("NULL ENT");
			return;
		}
		Entity monster = world.spawnEntity(loc, entity);    	    		
      	MobEntity tospawn = Mobs.mob((LivingEntity)monster);
      	if (tospawn == null) return;
      	Mobs.addMob((LivingEntity)monster);
      	//Log.warn(bonusSpawn + " " + a + " " + mobClass + " " + bonusMob.isBonus());
      	Log.warn(entity + " " + mobClass);
      	//set this since we spawned it and to avoid more
      	tospawn.dressMonster(mobClass);	
      	tospawn.setMeta(mobClass);
	}
	
	public static void addMob(LivingEntity le) {
		if (!(entityList.containsKey(le))) {
			//if(!le.isValid()) return;
			if(le.isDead()) return;
			MobEntity me = new MobEntity(le);
			entityList.put(le, me);
			entityList.get(le).setPowered();
		}
	}
	
	public static void delMob(LivingEntity le) {
		if (entityList.containsKey(le)) {
			entityList.remove(le);
		}
	}

	public static int getMetaTagCount(String meta) {
		int count = 0;
		for (LivingEntity le : entityList.keySet()) {
			if (entityList.get(le).getMeta().equalsIgnoreCase(meta)) {
				count++;
			}
		}
		return count;
	}
	
	public static MobEntity mob(LivingEntity le) {
		return entityList.get(le);
	}
	
	public static void doIterate() {
		for (World world : Bukkit.getServer().getWorlds()) {
			
			try {
				for (Entity e : world.getEntities()) {
					if (e.isDead()) continue;
					if ((e instanceof Ambient) || (e instanceof Creature)) {
						LivingEntity mob = ((LivingEntity)e);
						addMob(mob);
						//attackPlayer(mob(mob).getClosestPlayer(), mob(mob));
					}
    			}
			}
			catch (ConcurrentModificationException e) { 
					
			}
    	}
	}
	
	public static void attackPlayer(Player player, MobEntity mob) {
		if (player == null) return;
		//if (mob instanceof Ambient) {
			mob.moveEntity(player);
		//}
		
	}

	public static void addMobClass(String className, MobClass mobClass) {
		Mobs.mobClass.put(className, mobClass);
	}

	public static MobClass getMobClass(String className) {
		if (Mobs.mobClass.containsKey(className)) { 
			return Mobs.mobClass.get(className);
		}
		return null;
	}
}