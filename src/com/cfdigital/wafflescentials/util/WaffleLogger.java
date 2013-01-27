package com.cfdigital.wafflescentials.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WaffleLogger {	
	
    public static final Logger log = Logger.getLogger("Minecraft");

    public static void severe(String string, Exception ex) {
        log.log(Level.SEVERE, "[WaffleScentials] " + string, ex);

    }
    public static void severe(String string) {
        log.log(Level.SEVERE, "[WaffleScentials] " + string);
    }

    public static void info(String string) {
        log.log(Level.INFO,"[WaffleScentials] " + string);
    }

    public static void warning(String string) {
        log.log(Level.WARNING, "[WaffleScentials] " + string);
    }
	public static void warning(Pattern filterPattern) {
        log.log(Level.WARNING, "[WaffleScentials] " + filterPattern);
		// TODO Auto-generated method stub
		
	}
    
}