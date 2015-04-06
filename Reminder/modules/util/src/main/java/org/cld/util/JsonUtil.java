package org.cld.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class JsonUtil {
	
	//
	public static JSONObject getJsonDataFromSingleParameterFunctionCall(String input){
		Pattern p = Pattern.compile("\\((.*?)\\)");
		Matcher m = p.matcher(input);
		if (m.find()){
			String jsonData = m.group(1);
			return new JSONObject(jsonData);
		}else{
			return null;
		}
	}
}
