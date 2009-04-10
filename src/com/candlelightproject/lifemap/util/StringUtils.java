package com.candlelightproject.lifemap.util;

public class StringUtils {
	
	/* returns true if given string is null, empty or contains whitespace */
	public static boolean isEmptyOrWhitespace(String aString) {
		
		if (aString == null) return true;
		
		if(aString.trim().equals("")) {
			return true;
		
		} else {
			return false;
		
		}
		
	}

}
