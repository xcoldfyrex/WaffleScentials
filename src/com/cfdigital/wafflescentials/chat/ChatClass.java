package com.cfdigital.wafflescentials.chat;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.cfdigital.wafflescentials.util.WaffleLogger;

public class ChatClass {
	
	public static HashMap<String, ChatFilter> chatFilters = new HashMap<String, ChatFilter>();
	
	public static String join(String[] strArray, String delimiter, int start) {
		String joined = "";
		int noOfItems = 0;
		for (String item : strArray) {
			if (noOfItems < start) { noOfItems++; continue; }
			joined += item;
			if (++noOfItems < strArray.length)
			joined += delimiter;
		}
		return joined;
	}
	
	public static void setTabName(Player player, String text) {
		String pn = text+player.getName();
		if (pn.length() > 16) pn = pn.substring(0, 15);
		player.setPlayerListName(pn);
		
	}
	
    public static Boolean matchPattern(String message, String regex) {
    	ChatFilter cfClass = chatFilters.get(regex);
    	if (cfClass == null) return false;
    	Matcher matcher = cfClass.filterPattern.matcher(message);
    	return matcher.find();
    } 
    
    public static void addPattern(String regex, String filterAction, String filterReplace) {
    	// Do not re-compile if we already have this pattern 
    	if (chatFilters.get(regex) == null) {
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			ChatFilter filter = new ChatFilter(filterAction,pattern, filterReplace);
			chatFilters.put(regex, filter);	
			WaffleLogger.info("Added filter: " + regex + " " + filterAction + " " + filterReplace);
    	}
    }
    
    public static String replacePattern(String message, String regex, String to) {
    	ChatFilter cfClass = chatFilters.get(regex);
    	if (cfClass == null) {
    		return message;
    	}
    	Matcher matcher = cfClass.filterPattern.matcher(message);
    	return matcher.replaceAll(to);
    }
}