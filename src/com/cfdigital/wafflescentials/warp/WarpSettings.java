package com.cfdigital.wafflescentials.warp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.cfdigital.wafflescentials.WaffleScentials;

public class WarpSettings {
    
    public static File dataDir;

    public static int maxPublic;
    public static int maxPrivate;
    public static int extraWarps;
    public static boolean adminsObeyLimits;
    public static boolean adminPrivateWarps;
    public static boolean loadChunks;
    
    public static boolean usemySQL;
    public static String mySQLuname;
    public static String mySQLpass;
    public static String mySQLconn;

    public static boolean opPermissions;
    
    private static FileConfiguration config;
    public static File configFile;
    static YamlConfiguration ymlConfig;
    	    	
    public static boolean loadSettings() {
            configFile = new File(WaffleScentials.plugin.getDataFolder(), "warp.yml");
    		config = WaffleScentials.plugin.getConfig();

    		if (!WaffleScentials.plugin.getDataFolder().exists()) {
    			WaffleScentials.plugin.getDataFolder().mkdirs();
            }
    		
    		try {
                config.load(configFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            maxPublic = config.getInt("maxPublic");
        	maxPrivate = config.getInt("maxPrivate");
        	adminsObeyLimits = config.getBoolean("adminsObeyLimits");
        	adminPrivateWarps = config.getBoolean("adminPrivateWarps");
        	loadChunks = config.getBoolean("loadChunks");
        
        	usemySQL = config.getBoolean("usemySQL");
			mySQLconn = config.getString("mySQLconn");
			mySQLuname = config.getString("mySQLuname");
			mySQLpass = config.getString("mySQLpass");
        	extraWarps = config.getInt("extraWarps");

			opPermissions = config.getBoolean("opPermissions");
			return true;

    }
}
