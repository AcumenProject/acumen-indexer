package edu.ua.lib.acumen.repo;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Regex {
	
	public static final String SPLIT_AT_EXTENSION_REGEX = "([^.]*)(\\..*)";
	
	private static Pattern SPLIT_AT_EXTENSION_PATTERN = Pattern.compile("([^.]*)(\\..*)");
	private static Pattern GET_EXTENSION = Pattern.compile("(?=[^.]*)\\..*");
	private static Pattern GET_FILE_NAME = Pattern.compile("[^.]*(?=\\.)");
	
	public static Map<String, String> splitAtExtension(String input){
		Map<String, String> parts = new HashMap<String, String>();
		Matcher m = SPLIT_AT_EXTENSION_PATTERN.matcher(input);
		if (m.find()){
			parts.put("name", m.group(1));
			parts.put("ext", m.group(2));
			return parts;
		}
		return null;
	}
	
	public static String getExtension(String input){
		Matcher m = GET_EXTENSION.matcher(input);
		if (m.find()){
			return m.group(0);
		}
		return null;
	}
	
	public static String getFileName(String input){
		Matcher m = GET_FILE_NAME.matcher(input);
		if (m.find()){
			return m.group(0);
		}
		return null;
	}

}
