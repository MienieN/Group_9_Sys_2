package main.java.zenit.util;

public class StringUtilities {

	public static int countLeadingSpaces(String text) {
		if (text == null || text.length() == 0 || text.charAt(0) != ' ') {
			return 0;
		}
		return 1 + countLeadingSpaces(text.substring(1));
	}
	
	public static int count(String haystack, char needle) {
		if (haystack == null || haystack.length() == 0) {
			return 0;
		}
		
		if (haystack.charAt(0) == needle) {
			return 1 + count(haystack.substring(1), needle);
		}
		return count(haystack.substring(1), needle);
	}
}
