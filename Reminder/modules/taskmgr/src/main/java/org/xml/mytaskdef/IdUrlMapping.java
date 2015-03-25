package org.xml.mytaskdef;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.ScriptEngineUtil;
import org.xml.taskdef.DirectionType;
import org.xml.taskdef.RegExpType;
import org.xml.taskdef.TokenType;
import org.xml.taskdef.VarType;

public class IdUrlMapping{

	private static Logger logger =  LogManager.getLogger(IdUrlMapping.class);
	
	public static final String ID_KEY="id";
	public static final String PAGENUM_KEY="pageNum";
	public static final String ID2_KEY="id2";
	
	private static ScriptEngineManager manager = new ScriptEngineManager();
    
    
	String patternString="";
	Pattern regExpPattern;
	RegExpType ret;
	
	//this map is for OUT/BOTH parameters, extracting parameters from url
	Map<String, Integer> nameToIdxMap = new HashMap<String, Integer>();
	//this map is for IN/BOTH expression parameters to make the url
	Map<String, TokenType> inNameExpDefMap = new HashMap<String, TokenType>();
	
	public IdUrlMapping(RegExpType ret){
		this.ret = ret;
		int i=1;
		for (TokenType tt: ret.getToken()){
			if (tt.getDir()!=DirectionType.IN){//for OUT and BOTH typed tokens
				nameToIdxMap.put(tt.getName(), i);
				i++;
				String valueFromXml = StringEscapeUtils.unescapeXml(tt.getValue());
				if (tt.getType()==VarType.REGEXP){
					patternString+="(" + valueFromXml + ")";
				}else{
					patternString+= "(" + Pattern.quote(valueFromXml) + ")";
				}
			}
			if (tt.getDir()!=DirectionType.OUT && tt.getType()==VarType.EXPRESSION){
				inNameExpDefMap.put(tt.getName(), tt);
			}
		}
		patternString = "^" + patternString + "$";
		regExpPattern = Pattern.compile(patternString);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("name to idx map:");
		sb.append(nameToIdxMap.toString() + "\n");
		sb.append("patternString:");
		sb.append(patternString + "\n");
		return sb.toString();
	}
	
	public Matcher match(String url){
		return regExpPattern.matcher(url);
	}
	
	public int getIdIdx(){
		if (nameToIdxMap.containsKey(ID_KEY)){
			return nameToIdxMap.get(ID_KEY);
		}else{
			logger.error(String.format("name to index map should have id defined."));
			return nameToIdxMap.get(ID_KEY);
		}
	}
	
	public int getPageNumIdx(){
		return nameToIdxMap.get(PAGENUM_KEY);
	}
	
	public String getUrl(Map<String, Object> values){
		String url="";
		for (TokenType tt: ret.getToken()){
			if (tt.getDir()!=DirectionType.OUT){//IN or BOTH
				if (values.containsKey(tt.getName())){//new value provided for token
					if (inNameExpDefMap.containsKey(tt.getName())){
						TokenType mytt = inNameExpDefMap.get(tt.getName());
						//eval expression first
						Object ret = ScriptEngineUtil.eval(mytt.getValue(), mytt.getToType(), values);
						url += ret.toString();
					}else{
						url += values.get(tt.getName());
					}
				}else{
					url +=tt.getValue();
				}
			}
		}
		return url;
	}
}