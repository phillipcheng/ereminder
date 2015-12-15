package org.cld.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
	
	//there is util from map to json object then to string
	public static String toJsonStringFromMap(Map<String, Object> params){
		//
		List<String> removeKeys = new ArrayList<String>();
		for (String key: params.keySet()){
			Object o = params.get(key);
			if (!(o instanceof Serializable)){
				removeKeys.add(key);
			}
		}
		for (String key: removeKeys){
			params.remove(key);
		}
		JSONObject jobj = new JSONObject(params);
		return jobj.toString();
	}
	
	//there is no util from json object to java map
	public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
	    Map<String, Object> retMap = new HashMap<String, Object>();

	    if(json != JSONObject.NULL) {
	        retMap = toMap(json);
	    }
	    return retMap;
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
	    Map<String, Object> map = new HashMap<String, Object>();

	    Iterator<String> keysItr = object.keys();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        map.put(key, value);
	    }
	    return map;
	}

	public static List<Object> toList(JSONArray array) throws JSONException {
	    List<Object> list = new ArrayList<Object>();
	    for(int i = 0; i < array.length(); i++) {
	        Object value = array.get(i);
	        if(value instanceof JSONArray) {
	            value = toList((JSONArray) value);
	        }

	        else if(value instanceof JSONObject) {
	            value = toMap((JSONObject) value);
	        }
	        list.add(value);
	    }
	    return list;
	}
	
	public static Map<String, Object> fromJsonStringToMap(String jsonString){
		
		if (jsonString!=null){
			try{
				JSONObject jobj = new JSONObject(jsonString);
				return jsonToMap(jobj);
			}catch(Exception e){
				logger.error("the paramData is:" + jsonString, e);
			}
		}
		return null;
	}
	
	public static String ObjToJson(Object t){
		ObjectMapper om = new ObjectMapper();
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		ObjectWriter ow = om.writer().with(new MinimalPrettyPrinter());
		try {
			String json = ow.writeValueAsString(t);
			return json;
		} catch (JsonProcessingException e) {
			logger.error("",e );
			return null;
		}
	}
	
	public static Object objFromJson(String json, Class clazz){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		try {
			Object t = mapper.readValue(json, clazz);
			return t;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public static Object deepClone(Object obj){
		return objFromJson(ObjToJson(obj), obj.getClass());
	}
	
}
