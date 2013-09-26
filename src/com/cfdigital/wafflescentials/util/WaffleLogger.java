package com.cfdigital.wafflescentials.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class WaffleLogger {	
	
    public static final Logger log = Logger.getLogger("Minecraft");
    public static final String logPrefix = colorConvert("BROWN") + "[" + colorConvert("LIGHT_GREEN") + "WaffleScentials" + colorConvert("BROWN") +"] " + colorConvert("RESET");

    public static void severe(String string, Exception ex) {
        log.log(Level.SEVERE, logPrefix + string, ex);

    }
    
    public static void severe(Object object) {
        log.log(Level.SEVERE, logPrefix + colorConvert("LIGHT_RED") + object + colorConvert("RESET"));
    }

    public static void info(Object object) {
        log.log(Level.INFO, logPrefix + object);
    }

    public static void warning(Object object) {
        log.log(Level.WARNING, logPrefix + colorConvert("LIGHT_YELLOW") + object + colorConvert("RESET"));
    }
	
	 private static String colorConvert(String color) {
		 if (color.equals("LIGHT_RED")) { return "\u001B[1;31m"; }
		 if (color.equals("LIGHT_GREEN")) { return "\u001B[1;32m"; }
		 if (color.equals("LIGHT_YELLOW")) { return "\u001B[1;33m"; }
		 if (color.equals("LIGHT_CYAN")) { return "\u001B[1;36m"; }
		 if (color.equals("RESET")) { return "\u001B[0m"; }
		 if (color.equals("BROWN")) { return "\u001B[0;33m"; }

		 return "";
	 }	    
    
}