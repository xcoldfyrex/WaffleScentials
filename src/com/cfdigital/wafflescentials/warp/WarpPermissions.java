package com.cfdigital.wafflescentials.warp;

import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.WaffleScentials;


public class WarpPermissions {


    public static boolean isAdmin(Player player) {
        return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.admin");
    }

    public static boolean warp(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.warp");
    }

    public static boolean delete(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.delete");
    }
    
    public static boolean move(Player player) {
        return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.move");
}

    public static boolean list(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.list");
    }

    public static boolean welcome(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.welcome");
    }

    public static boolean search(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.search");
    }

    public static boolean give(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.soc.give");
    }

    public static boolean invite(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.soc.invite");
    }

    public static boolean uninvite(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.soc.uninvite");
    }

    public static boolean canPublic(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.soc.public");
    }

    public static boolean canPrivate(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.soc.private");
    }

    public static boolean signWarp(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.sign.warp");
    }

    public static boolean privateCreate(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.createprivate");
    }
    
    public static boolean publicCreate(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.createpublic");
    }
    
    public static boolean compass(Player player) {
            return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.basic.compass");
    }

    public static int maxPrivateWarps(Player player) {
    	if (extraWarps(player)) {
            return WarpSettings.maxPrivate + WarpSettings.extraWarps;
    	}
        return WarpSettings.maxPrivate;
    }
    
    public static int maxPublicWarps(Player player) {
    	if (extraWarps(player)) {
            return WarpSettings.maxPublic + WarpSettings.extraWarps;
    	}
        return WarpSettings.maxPublic;
    }
    
    public static boolean extraWarps(Player player) {
        return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.extra.extrawarps");
    }
    
    public static boolean freeWarps(Player player) {
        return WaffleScentials.plugin.hasPermissionsSilent(player, "wsent.warp.extra.freewarps");
    }

}