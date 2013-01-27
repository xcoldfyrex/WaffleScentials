package com.cfdigital.wafflescentials.chat;

import java.util.regex.Pattern;

public class ChatFilter {
	
	public ChatFilter(String filterAction, Pattern filterPattern, String filterReplace) {		
		this.filterPattern = filterPattern;
		this.filterAction = filterAction;
		this.filterReplace = filterReplace;
	}

	
	public Pattern filterPattern;
	public String filterAction = "";
	public String filterReplace = "";
	//enum action {KICK, BAN, DENY};
}