package com.cfdigital.wafflescentials;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class WafflePlayer {

	WaffleScentials plugin;
	
	private Player player;

	private static Map<String, WafflePlayer> WafflePlayers = new HashMap<String, WafflePlayer>();

	public WafflePlayer(Player player) {
		this.player = player;
		//this.isAFK = isAFK;
		//this.isMuted = isMuted;
		WafflePlayers.put(player.getName(), this);
		this.plugin = WaffleScentials.plugin;
	}

	// Return a running instance (or create a new one)
	public static WafflePlayer getInstanceOfPlayer(Player player) {
		if(!WafflePlayers.containsKey(player.getName())) {
			return new WafflePlayer(player);
		}
		else if(WafflePlayers.containsKey(player.getName())) {
			return WafflePlayers.get(player.getName());
		}
		return null;
	}

	//public PlayerClass(Player player) {
	//this.isAFK = isAFK;
	//this.isMuted = isMuted;
	//}

	public String getAFKReason() {
		return (String) plugin.getMetadata(player, "AFKReason");
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
		return (String) plugin.getMetadata(player, "lastMessager");
	}

	public void setLastMessager(String lastMessager) {
		plugin.setMetadata(player, "lastMessager", lastMessager);
	}

	public void setAFK(String reason) {
		this.isAFK = true;
		plugin.setMetadata(player, "AFKReason", reason);
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

	public int getMessageCount() {
		return this.messagecount;
	}

	public void incrMessageCount() {
		this.messagecount++;
	}

	public void decrMessageCount() {
		if (this.messagecount > 0) {
			this.messagecount--;
		} else {
			this.messagecount = 0;
		}
	}

	public void setLastMessageSent(long lastMessageSent) {
		this.lastMessageSent = lastMessageSent;
	}

	private boolean muted = false;
	private boolean isAFK = false;
	private String lastMessager = null;
	private long lastActive = System.currentTimeMillis() / 1000L;
	private long lastHeal = 0;
	private String mobClassAttacker;
	private HashMap<String, Long> kitCoolDown = new HashMap<String, Long>();
	private long lastMessageSent = 0;
	private String lastMessageString = "";
	private int messagecount = 0;


}