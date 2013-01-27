package com.cfdigital.wafflescentials.mobs;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MobEntity {
	
	public MobEntity(LivingEntity monster){
		this.monster = monster;
		this.meta = "";
		//only do this for mobs that can be dressed
		//if (monster instanceof Zombie) this.entityLiving = getEntityLiving();
		//if (monster instanceof Skeleton) this.entityLiving = getEntityLiving();
		//if (monster instanceof PigZombie) this.entityLiving = getEntityLiving();
	}
	
	public void setPowered() {
		if (monster instanceof Creeper) {
			((Creeper)monster).setPowered(true);
		} 
	}
	
	public void setMeta(String data){
		//fake metadata, not using the bukkit type
		this.meta = data;
	}
	
	public String getMeta(){
		//fake metadata, not using the bukkit type
		return this.meta;
	}
	
	public void targetEntity(LivingEntity le){
		//only works on creatures
   		if (!(monster instanceof Creature)) return;
		if (this.target != null)  return;
   		((Creature)monster).setTarget(le);
   		target = le;
		//WaffleLogger.warning("Target " + le + " " + monster);

	}
	
	public void moveEntity(LivingEntity le) {
		//it seems bats don't have a way to move.
		if (le == null) return;
		Location targetLocation = le.getLocation();
		Location sourceLocation = monster.getLocation();
		double sx,sy,sz,tx,ty,tz,nx,ny,nz,dist;
		sx = sourceLocation.getX();
		sy = sourceLocation.getY();
		sz = sourceLocation.getZ();
		
		tx = targetLocation.getX();
		ty = targetLocation.getY();
		tz = targetLocation.getZ();
		
		nx = tx - sx;
		ny = ty - sy;
		nz = tz - sz;
		
		dist = Math.sqrt((nx*nx)+(ny*ny)+(nz*nz));
		//WaffleLogger.warning("distance " + dist);
		
		//entityLiving.move(arg0, arg1, arg2)
	}
	
	public Entity getTarget(){
		return this.target;
	}
	
   	
   	public void setVampiric() {
   		//if (!(monster instanceof Bat)) return;
   		this.isVampiric = true;
   	}
   	
   	public boolean getVampiric() {
   		return this.isVampiric;
   	}
   	
   	public List<?> getNearbyEntities() {
   		List<?> entities = null;
   		if (monster instanceof Creature) { entities = ((Creature)monster).getNearbyEntities(50, 50, 50); }
   		if (monster instanceof Ambient) { entities = ((Ambient)monster).getNearbyEntities(50, 50, 50); }
   		return entities;
   	}
   	
   	public Player getClosestPlayer() {
   		//won't work with other types
   		if (!((monster instanceof Ambient) || (monster instanceof Creature))) return null;
   		List<?> entities = getNearbyEntities();
   		int x = 0;
   		while (x < entities.size()) {
   			if (entities.get(x) instanceof Player) return (Player) entities.get(x);
   			x++;
   		}
   		return null;
   	}
   	
   	//add stuff to it
    public void dressMonster(String mobClassString){
    	/* 
    	 * TODO
    	 * 
    	 * WHY THE FUCK IS THIS RETURNING FALSE??? LOOK HERE FOR NPE!!!
    	 */
    	//if (!monster.isValid()) return;
    	MobClass mobclass = Mobs.getMobClass(mobClassString);
    	if (mobclass == null) return;
    	EntityEquipment equipment = this.monster.getEquipment();
    	equipment.setArmorContents(mobclass.getArmorSet());
    	equipment.setItemInHand(mobclass.getWeapon());
    }
   	
    public void setBonus(boolean isBonus) {
		this.isBonus = isBonus;
	}

	public boolean isBonus() {
		return isBonus;
	}

	public void setEffect(PotionEffectType effect) {
		if (effect == null) return;
		this.monster.addPotionEffect(new PotionEffect(effect, 9999999, 1));
	}
	
	public void setMeta(String metaKey, MetadataValue metaValue) {
		this.monster.setMetadata(metaKey, metaValue);
	}
	
	public List<MetadataValue> getMeta(String metaKey){
		return this.monster.getMetadata(metaKey);
	}
	
	private LivingEntity monster;
	private String meta = "";
	private Entity target = null;
	private boolean isVampiric;
	private boolean isBonus;
	
}