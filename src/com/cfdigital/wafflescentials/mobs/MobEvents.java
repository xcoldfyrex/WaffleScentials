package com.cfdigital.wafflescentials.mobs;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobEvents extends Event {
    private static final HandlerList handlers = new HandlerList();
    private LivingEntity le;
    
    public MobEvents(LivingEntity ent) {
        le = ent;
    }
    public LivingEntity getLivingEntity() {
    	return le;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}