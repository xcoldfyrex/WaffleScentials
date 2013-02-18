package com.cfdigital.wafflescentials;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class PlayerClass {
	
	public PlayerClass(Player player) {
		//this.isAFK = isAFK;
		//this.isMuted = isMuted;
	}
	
	public String getAFKReason() {
		return this.reasonAFK;
	}
	public boolean isMuted() {
		return this.muted;
	}
	
	public void mutePlayer() {
		this.muted = true;
	}
	
	public void unmutePlayer() {
		this.muted = false;		
	}
	
	public boolean isPlayerAFK() {
		return this.isAFK;
	}
	public String getLastMessager() {
		return this.lastMessager;
	}
	
	public void setLastMessager(String player) {
		this.lastMessager = player ;
	}
	
	public void setAFK(String reason) {
		this.isAFK = true;
		this.reasonAFK = reason;
	}
	
	public void unsetAFK() {
		this.isAFK = false;
	}
	

	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}

	public long getLastActive() {
		return lastActive;
	}

	public void setLastHeal(long lastHeal) {
		this.lastHeal = lastHeal;
	}

	public long getLastHeal() {
		return lastHeal;
	}

	public void setMobClassAttacker(String mobClassAttacker) {
		this.mobClassAttacker = mobClassAttacker;
	}

	public String getMobClassAttacker() {
		return mobClassAttacker;
	}

	public void setKitLastUsed(String kit, long lastUsed) {
		if (this.kitCoolDown.containsKey(kit)) {
			this.kitCoolDown.remove(kit);
		}
		this.kitCoolDown.put(kit, lastUsed);
	}

	public long getKitLastUsed(String kit) {
		if (kitCoolDown.containsKey(kit)) {
			return kitCoolDown.get(kit);
		}
		return 0;
	}

	public String getLastMessageString() {
		return lastMessageString;
	}

	public void setLastMessageString(String lastMessageString) {
		this.lastMessageString = lastMessageString;
	}

	public long getLastMessageSent() {
		return lastMessageSent;
	}

	public void setLastMessageSent(long lastMessageSent) {
		this.lastMessageSent = lastMessageSent;
	}

	private boolean muted = false;
	private boolean isAFK = false;
	private String reasonAFK; 
	private String lastMessager = null;
	private long lastActive = System.currentTimeMillis() / 1000L;
	private long lastHeal = 0;
	private String mobClassAttacker;
    private HashMap<String, Long> kitCoolDown = new HashMap<String, Long>();
    private long lastMessageSent = 0;
    private String lastMessageString = "";
    

}