package org.cld.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtil {
	private static Logger logger =  LogManager.getLogger(JsonUtil.class);
	
	//
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	
	public static JSONArray toJsonArray(List list){
		JSONArray js = new JSONArray();
		for (Object obj:list){
			if (obj instanceof List){
				js.put(toJsonArray((List)obj));
			}else{
				js.put(obj);
			}
		}
		return js;
	}
	
	public static String toJsonString(Map<String, Object> params){
		JSONObject jobj = new JSONObject();
		if (params!=null){
			for (String key: params.keySet()){
				Object val = params.get(key);
				if (val == null){
					jobj.put(key, JSONObject.NULL);
				}else {
					if (val instanceof String){
						jobj.put(key, (String)val);
					}else if (val instanceof Float){
						jobj.put(key, ((Float) val).floatValue());
					}else if (val instanceof List){
						jobj.put(key, toJsonArray((List)val));
					}else if (val instanceof JSONArray){
						jobj.put(key, val);
					}else if (val instanceof Integer){
						jobj.put(key, val);
					}else if (val instanceof Boolean){
						jobj.put(key, val);
					}else if (val instanceof Date){
						String sd = sdf.format(val);
						jobj.put(key, sd);
					}else{
						logger.warn(String.format("type not supported for json serialization: %s:%s", key, val));
					}
				}
			}
		}
		return jobj.toString();
	}
	
	public static void fromJsonString(String jsonString, Map<String, Object> params){
		if (jsonString!=null){
			try{
				JSONObject jobj = new JSONObject(jsonString);
				String[] names = JSONObject.getNames(jobj);
				if (names!=null){
					for (String name:names){
						Object o = jobj.opt(name);
						if (o instanceof JSONArray){
							List<String> listdata = new ArrayList<String>();     
							JSONArray jArray = (JSONArray)o; 
							if (jArray != null) { 
							   for (int i=0;i<jArray.length();i++){ 
							    listdata.add(jArray.get(i).toString());
							   } 
							}
							params.put(name, listdata);
						}else if (o == JSONObject.NULL){
							params.put(name, null);
						}else{
							params.put(name, o);
						}
					}
				}
			}catch(Exception e){
				logger.error("the paramData is:" + jsonString, e);
			}
		}
	}
}
