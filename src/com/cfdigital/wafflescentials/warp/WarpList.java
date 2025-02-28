package com.cfdigital.wafflescentials.warp;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



import org.bukkit.*;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;

public class WarpList {
	private HashMap<String, Warp> warpList;
	private HashMap<String, Warp> welcomeMessage;
	private Server server;

	public WarpList(Server server) {
		welcomeMessage = new HashMap<String, Warp>();
		this.server = WaffleScentials.plugin.getServer();
		WarpDataSource.initialize();
		warpList = WarpDataSource.getMap();
	}

	public void addWarp(String name, Player player) {
		if (playerCanBuildPublicWarp(player)) {
			if (warpList.containsKey(name)) {
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Warp called '" + name + "' already exists.");
			} else {
				Warp warp = new Warp(name, player);
				warpList.put(name, warp);
				WarpDataSource.addWarp(warp);
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Successfully created '" + name + "'");
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "If you'd like to privatize it,");
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Use: " + ChatColor.RED + "/warp private " + name);
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You have reached your max # of public warps (" + WarpPermissions.maxPublicWarps(player)
					+ ")");
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Delete some of your warps to make more");
		}
	}

	private int numPublicWarpsPlayer(Player player) {
		int size = 0;
		for (Warp warp : warpList.values()) {
			boolean publicAll = warp.publicAll;
			String creator = warp.creator;
			if (creator.equals(player.getName()) && publicAll)
				size++;
		}
		return size;
	}

	private boolean playerCanBuildPublicWarp(Player player) {
		if(WarpPermissions.isAdmin(player) && !WarpSettings.adminsObeyLimits) {
			return true;
		} else{
			return numPublicWarpsPlayer(player) < WarpPermissions.maxPublicWarps(player);
		}
	}

	private boolean playerCanBuildPrivateWarp(Player player) {
		if(WarpPermissions.isAdmin(player) && !WarpSettings.adminsObeyLimits) {
			return true;
		} else{
			return numPrivateWarpsPlayer(player) < WarpPermissions.maxPrivateWarps(player);
		}
	}


	public void addWarpPrivate(String name, Player player) {
		if (playerCanBuildPrivateWarp(player)) {
			if (warpList.containsKey(name)) {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Warp called '" + name + "' already exists.");
			} else {
				Warp warp = new Warp(name, player, false);
				warpList.put(name, warp);
				WarpDataSource.addWarp(warp);
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Successfully created '" + name + "'");
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "f you'd like to invite others to it,");
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Use: " + ChatColor.RED + "/warp invite <player> " + name);
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You have reached your max # of private warps ("
					+ WarpPermissions.maxPrivateWarps(player) + ")");
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Delete some of your warps to make more");
		}
	}

	private int numPrivateWarpsPlayer(Player player) {
		int size = 0;
		for (Warp warp : warpList.values()) {
			boolean privateAll = !warp.publicAll;
			String creator = warp.creator;
			if (creator.equals(player.getName()) && privateAll)
				size++;
		}
		return size;
	}

	public void blindAdd(Warp warp) {
		warpList.put(warp.name, warp);
	}

	public void warpTo(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanWarp(player)) {
				if (!WarpPermissions.freeWarps(player)) {
					double cost = 0;
					if (!warp.isFree) {
						cost = WarpSettings.warpFee;
						Double bal = WaffleScentials.economy.getBalance(player.getName());
						/*
						if (bal < 1 ) {
							player.sendMessage(ChatColor.RED + "You don't enough money to warp here! Try voting by /vote"); 
							return;
						}
						
						cost = 1;
						WaffleLib.economy.bankWithdraw(player.getName(), cost);
						*/
					}
				}
				warp.warp(player, server);
				player.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 0);
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY +  warp.welcomeMessage);

			} else {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You do not have permission to warp to '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "No such warp '" + name + "'");
		}
	}

	public void deleteWarp(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				warpList.remove(name);
				WarpDataSource.deleteWarp(warp);
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have deleted '" + name + "'");
			} else {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You do not have permission to delete '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "No such warp '" + name + "'");
		}
	}

	public void moveWarp(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				warpList.get(name).x = player.getLocation().getX();
				warpList.get(name).y = player.getLocation().getBlockY();
				warpList.get(name).z = player.getLocation().getZ();
				warpList.get(name).pitch = Math.round(player.getLocation().getPitch() % 360);
				warpList.get(name).yaw = Math.round(player.getLocation().getYaw() % 360);     	
				WarpDataSource.moveWarp(warp);
				player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have moved '" + name + "'");
			} else {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You do not have permission to move '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "No such warp '" + name + "'");
		}
	}

	public void privatize(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				if (playerCanBuildPrivateWarp(player)) {

					warp.publicAll = false;
					WarpDataSource.publicizeWarp(warp, false);
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have privatized '" + name + "'");
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "If you'd like to invite others to it,");
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "Use: " + ChatColor.RED + "/warp invite <player> " + name);
				}
				else {
					player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You have reached your max # of private warps ("
							+ WarpPermissions.maxPrivateWarps(player) + ")");
					player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "Delete some of your warps to make more");
				}

			} else {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You do not have permission to privatize '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "No such warp '" + name + "'");
		}
	}

	public void invite(String name, Player player, String inviteeName) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				if (warp.playerIsInvited(inviteeName)) {
					player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY +  inviteeName + " is already invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + inviteeName + " is the creator, of course he's the invited!");
				} else {
					warp.invite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You have invited " + inviteeName + " to '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "But '" + name + "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "You've been invited to warp '" + name + "' by " + player.getName());
						match.sendMessage(ChatColor.AQUA + ">> " + ChatColor.GRAY + "/warp " + name + " to warp to it.");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "You do not have permission to invite players to '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + ">> " + ChatColor.GRAY + "No such warp '" + name + "'");
		}
	}

	public void publicize(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				warp.publicAll = true;
				WarpDataSource.publicizeWarp(warp, true);
				player.sendMessage(ChatColor.AQUA + "You have publicized '" + name + "'");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to publicize '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void uninvite(String name, Player player, String inviteeName) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				if (!warp.playerIsInvited(inviteeName)) {
					player.sendMessage(ChatColor.RED + inviteeName + " is not invited to this warp.");
				} else if (warp.playerIsCreator(inviteeName)) {
					player.sendMessage(ChatColor.RED + "You can't uninvite yourself. You're the creator!");
				} else {
					warp.uninvite(inviteeName);
					WarpDataSource.updatePermissions(warp);
					player.sendMessage(ChatColor.AQUA + "You have uninvited " + inviteeName + " from '" + name + "'");
					if (warp.publicAll) {
						player.sendMessage(ChatColor.RED + "But '" + name + "' is still public.");
					}
					Player match = server.getPlayer(inviteeName);
					if (match != null) {
						match.sendMessage(ChatColor.RED + "You've been uninvited to warp '" + name + "' by " + player.getName() + ". Sorry.");
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public ArrayList<Warp> getSortedWarps(Player player, int start, int size) {
		ArrayList<Warp> ret = new ArrayList<Warp>();
		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		int index = 0;
		int currentCount = 0;
		while (index < names.size() && ret.size() < size) {
			String currName = names.get(index);
			Warp warp = warpList.get(currName);
			if (warp.playerInvited(player) || warp.creator.equalsIgnoreCase(player.getName())) {
				if (currentCount >= start) {
					ret.add(warp);
				} else {
					currentCount++;
				}
			}
			index++;
		}
		return ret;
	}

	public int getSize() {
		return warpList.size();
	}

	public MatchList getMatches(String name, Player player) {
		ArrayList<Warp> exactMatches = new ArrayList<Warp>();
		ArrayList<Warp> matches = new ArrayList<Warp>();

		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		for (int i = 0; i < names.size(); i++) {
			String currName = names.get(i);
			Warp warp = warpList.get(currName);
			if (warp.playerCanWarp(player)) {
				if (warp.name.equalsIgnoreCase(name)) {
					exactMatches.add(warp);
				} else if (warp.name.toLowerCase().contains(name.toLowerCase())) {
					matches.add(warp);
				}
			}
		}
		if (exactMatches.size() > 1) {
			for (int i = 0; i < exactMatches.size(); i++) {
				Warp warp = exactMatches.get(i);
				if (!warp.name.equals(name)) {
					exactMatches.remove(warp);
					matches.add(0, warp);
					i--;
				}
			}
		}
		return new MatchList(exactMatches, matches);
	}

	public void give(String name, Player player, String giveeName) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				if (warp.playerIsCreator(giveeName)) {
					player.sendMessage(ChatColor.RED + giveeName + " is already the owner.");
				} else {
					warp.setCreator(giveeName);
					WarpDataSource.updateCreator(warp);
					player.sendMessage(ChatColor.AQUA + "You have given '" + name + "' to " + giveeName);
					Player match = server.getPlayer(giveeName);
					if (match != null) {
						match.sendMessage(ChatColor.AQUA + "You've been given '" + name + "' by " + player.getName());
					}
				}
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to uninvite players from '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public double getMaxWarps(Player player) {
		int count = 0;
		for (Warp warp : warpList.values()) {
			if (warp.playerInvited(player) || warp.creator.equalsIgnoreCase(player.getName())) {
				count++;
			}
		}
		return count;
	}

	public void welcomeMessage(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanModify(player)) {
				welcomeMessage.put(player.getName(), warp);
				player.sendMessage(ChatColor.AQUA + "Enter the welcome message for '" + name + "'");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to modify '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public boolean waitingForWelcome(Player player) {
		return welcomeMessage.containsKey(player.getName());
	}

	public void setWelcomeMessage(Player player, String message) {
		if (welcomeMessage.containsKey(player.getName())) {
			Warp warp = welcomeMessage.get(player.getName());
			warp.welcomeMessage = message;
			WarpDataSource.updateWelcomeMessage(warp);
			player.sendMessage(ChatColor.AQUA + "Changed welcome message for '" + warp.name + "' to:");
			player.sendMessage(message);
		}

	}

	public void notWaiting(Player player) {
		welcomeMessage.remove(player.getName());
	}

	public void list(Player player) {
		ArrayList<Warp> results = warpsInvitedTo(player);

		if (results.size() == 0) {
			player.sendMessage(ChatColor.RED + "You can access no warps.");
		} else {
			player.sendMessage(ChatColor.AQUA + "You can warp to:");
			player.sendMessage(results.toString().replace("[", "").replace("]", ""));
		}
	}

	private ArrayList<Warp> warpsInvitedTo(Player player) {
		ArrayList<Warp> results = new ArrayList<Warp>();

		List<String> names = new ArrayList<String>(warpList.keySet());
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		Collections.sort(names, collator);

		for (String name : names) {
			Warp warp = warpList.get(name);
			if (warp.playerCanWarp(player)) {
				results.add(warp);
			}
		}
		return results;
	}

	public void point(String name, Player player) {
		MatchList matches = this.getMatches(name, player);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			if (warp.playerCanWarp(player)) {
				player.setCompassTarget(warp.getLocation(server));
				player.sendMessage(ChatColor.AQUA + "Your compass now guides you to '" + name + "'");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have permission to point to '" + name + "'");
			}
		} else {
			player.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}

	public void adminWarpTo(String name, Player invitee, Player admin) {
		MatchList matches = this.getMatches(name, admin);
		name = matches.getMatch(name);
		if (warpList.containsKey(name)) {
			Warp warp = warpList.get(name);
			warp.warp(invitee, server);
			invitee.sendMessage(ChatColor.AQUA + warp.welcomeMessage);
			admin.sendMessage(ChatColor.AQUA + "Successfully warped " + invitee.getName());
		} else {
			admin.sendMessage(ChatColor.RED + "No such warp '" + name + "'");
		}
	}
}
