package com.cfdigital.wafflescentials.warp;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;

public class WarpCommands  implements CommandExecutor {
	
	 WaffleScentials plugin;

	public WarpCommands(WaffleScentials plugin) {
		this.plugin = plugin;
	}
    
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    final WarpList warpList = WaffleScentials.plugin.warpList;
        String[] split = args;
        String commandName = command.getName().toLowerCase();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("warp")) {
            	if ((split.length == 1 || (split.length == 2 && isInteger(split[1]))) && split[0].equalsIgnoreCase("list")) {
                    Lister lister = new Lister(warpList);
                    lister.addPlayer(player);

                    if (split.length == 2) {
                        int page = Integer.parseInt(split[1]);
                        if (page < 1) {
                            player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                            return true;
                        } else if (page > lister.getMaxPages(player)) {
                            player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(player) + " pages of warps");
                            return true;
                        }
                        lister.setPage(page);
                    } else {
                        lister.setPage(1);
                    }
                    lister.list();

                    /**
                     * /warp slist
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("slist")) {
                    warpList.list(player);
                    /**
                     * /warp search <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("search") && WarpPermissions.search(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    Searcher searcher = new Searcher(warpList);
                    searcher.addPlayer(player);
                    searcher.setQuery(name);
                    searcher.search();
                    /**
                     * /warp create <name>
                     */
                } else if (split.length > 1 && (split[0].equalsIgnoreCase("create") || split[0].equalsIgnoreCase("set"))
                        && (WarpPermissions.publicCreate(player) || WarpPermissions.privateCreate(player))) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    if (WarpPermissions.publicCreate(player)) {
                        warpList.addWarp(name, player);
                    } else {
                        warpList.addWarpPrivate(name, player);
                    }
                    /**
                     * /warp point <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("point") && WarpPermissions.compass(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.point(name, player);
                    /**
                     * /warp pcreate <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("pcreate") && WarpPermissions.privateCreate(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.addWarpPrivate(name, player);
                    /**
                     * /warp delete <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("delete") && WarpPermissions.delete(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.deleteWarp(name, player);
                    /**
                     * /warp welcome <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("welcome") && WarpPermissions.welcome(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.welcomeMessage(name, player);
                    /**
                     * /warp private <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("move") && WarpPermissions.move(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.moveWarp(name, player);
                    /**
                     * /warp move <name>
                     */
                    
                } else if (split.length > 1 && split[0].equalsIgnoreCase("private") && WarpPermissions.canPrivate(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.privatize(name, player);
                    /**
                     * /warp public <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("public") && WarpPermissions.canPublic(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.publicize(name, player);

                    /**
                     * /warp give <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("give") && WarpPermissions.give(player)) {
                    Player givee = WaffleScentials.plugin.getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String giveeName = (givee == null) ? split[1] : givee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    if (WaffleScentials.plugin.getPlayer(name) != null) {
                    	warpList.give(name, player, giveeName);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Cannot give warp to offline player: " + name);
                    }

                    /**
                     * /warp invite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("invite") && WarpPermissions.invite(player)) {
                    Player invitee = WaffleScentials.plugin.getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.invite(name, player, inviteeName);
                    /**
                     * /warp uninvite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("uninvite") && WarpPermissions.uninvite(player)) {
                    Player invitee = WaffleScentials.plugin.getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.uninvite(name, player, inviteeName);

                    /**
                     * /warp player <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("player") && WarpPermissions.isAdmin(player)) {
                    Player invitee = WaffleScentials.plugin.getServer().getPlayer(split[1]);
                    //String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.adminWarpTo(name, invitee, player);

                    /**
                     * /warp help
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("help")) {
                    ArrayList<String> messages = new ArrayList<String>();
                    messages.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED + " --------------------");
                    if (WarpPermissions.warp(player)) {
                        messages.add(ChatColor.RED + "/warp [name]" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY + "[name]");
                    }
                    if (WarpPermissions.publicCreate(player) || WarpPermissions.privateCreate(player)) {
                        messages.add(ChatColor.RED + "/warp create [name]" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "[name]");
                    }
                    if (WarpPermissions.privateCreate(player)) {
                        messages.add(ChatColor.RED + "/warp pcreate [name]" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "[name]");
                    }

                    if (WarpPermissions.delete(player)) {
                        messages.add(ChatColor.RED + "/warp delete [name]" + ChatColor.WHITE + "  -  Delete warp " + ChatColor.GRAY + "[name]");
                    }

                    if (WarpPermissions.welcome(player)) {
                        messages.add(ChatColor.RED + "/warp welcome [name]" + ChatColor.WHITE + "  -  Change the welcome message of " + ChatColor.GRAY
                                + "[name]");
                    }

                    if (WarpPermissions.list(player)) {
                        messages.add(ChatColor.RED + "/warp list (#)" + ChatColor.WHITE + "  -  Views warp page " + ChatColor.GRAY + "(#)");
                    }

                    if (WarpPermissions.search(player)) {
                        messages.add(ChatColor.RED + "/warp search [query]" + ChatColor.WHITE + "  -  Search for " + ChatColor.GRAY + "[query]");
                    }
                    if (WarpPermissions.give(player)) {
                        messages.add(ChatColor.RED + "/warp give [player] [name[" + ChatColor.WHITE + "  -  Give " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " your " + ChatColor.GRAY + "[name]");
                    }
                    if (WarpPermissions.invite(player)) {
                        messages.add(ChatColor.RED + "/warp invite [player] [name]" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "[name]");
                    }
                    if (WarpPermissions.uninvite(player)) {
                        messages.add(ChatColor.RED + "/warp uninvite [player] [name[" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "[player]"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "[name]");
                    }
                    if (WarpPermissions.canPublic(player)) {
                        messages.add(ChatColor.RED + "/warp public [name]" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "[name]" + ChatColor.WHITE
                                + " public");
                    }
                    if (WarpPermissions.canPrivate(player)) {
                        messages.add(ChatColor.RED + "/warp private [name]" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "[name]"
                                + ChatColor.WHITE + " private");
                    }
                    for (String message : messages) {
                        player.sendMessage(message);
                    }

                    /**
                     * /warp <name>
                     */
                } else if (split.length > 0 && WarpPermissions.warp(player)) {
                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 0; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.warpTo(name, player);
                } else {
                	player.sendMessage(WaffleScentials.Prefix + "No permission for this command.");
                    return true;
                }
                return true;
            }
        }
        return false;
    }
	
    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}