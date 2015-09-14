package org.cld.taskmgr;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.util.SafeSimpleDateFormat;
import org.cld.util.StringUtil;
import org.cld.util.distribute.SimpleNodeConf;
import org.xml.mytaskdef.ConfKey;
import org.xml.mytaskdef.ScriptEngineUtil;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskUtil {
	private static Map<String, SafeSimpleDateFormat> dfMap = new HashMap<String, SafeSimpleDateFormat>();
	public static final String LIST_VALUE_SEP=",";
	private static Logger logger =  LogManager.getLogger(TaskUtil.class);
	
	public static Set<Task> convertToSet(List<Task> tl){
		LinkedHashSet<Task> lhs = new LinkedHashSet<Task>();
		for (int i=0; i<tl.size(); i++){
			lhs.add(tl.get(i));
		}
		return lhs;
	}
	
	public static List<String> getKeyList(List<? extends Task> tl){
		List<String> kl = new ArrayList<String>();
		for (int i=0 ; i<tl.size(); i++){
			Task t = tl.get(i);
			kl.add(t.getId());
		}
		return kl;
	}
	
	public static List<String> getKeyList(Set<Task> tl){
		List<String> kl = new ArrayList<String>();
		Iterator<Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static Set<String> getKeySet(List<Task> tl){
		Set<String> kl = new HashSet<String>();
		Iterator<Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static Set<String> getKeySet(Set<? extends Task> tl){
		Set<String> kl = new HashSet<String>();
		Iterator<? extends Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static String taskToJson(Task t){
		//
		List<String> removeKeys = new ArrayList<String>();
		for (String key: t.getParamMap().keySet()){
			Object o = t.getParamMap().get(key);
			if (o!=null && !(o instanceof Serializable)){
				removeKeys.add(key);
			}
		}
		for (String key: removeKeys){
			t.getParamMap().remove(key);
		}
		ObjectWriter ow = new ObjectMapper().writer().with(new MinimalPrettyPrinter());
		try {
			String json = ow.writeValueAsString(t);
			return json;
		} catch (JsonProcessingException e) {
			logger.error("",e );
			return null;
		}
	}
	
	public static Task taskFromJson(String json){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Task t = mapper.readValue(json, Task.class);
			Class<? extends Task> clazz = (Class<? extends Task>) Class.forName(t.getTtype());
			t =  mapper.readValue(json, clazz);
			if (t.getParamMap()==null || t.getParamMap().size()==0){
				t.fromParamData();
			}
			return t;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	//
	public static NodeConf getNodeConf(String properties){
		NodeConf nc = null;
		nc = new NodeConf(properties);
		return nc;
	}
	

	public static Object getValue(ValueType vt, String valueExp){
		return getValue(vt, valueExp, vt.getToType());
	}
	
	//if vt provides more info for toType, it can be null
	public static Object getValue(ValueType vt, String valueExp, VarType toType){
		if (toType==null){
			toType = vt.getToType();
		}
		Object value = null;
		if (vt!=null){
			//perform pre-process
			ValueType.StrPreprocess sp = vt.getStrPreprocess();
			if (sp!=null){
				valueExp = StringUtil.getStringBetweenFirstPreFirstPost(valueExp, sp.getTrimPre(), sp.getTrimPost());
				valueExp = valueExp.trim();
			}
		}
		if (toType!=null){
			if (VarType.DATE==toType){
				String format = vt.getFormat();
				SafeSimpleDateFormat sdf=null;
				if (dfMap.containsKey(format)){
					sdf = dfMap.get(format);
				}else{
					sdf = new SafeSimpleDateFormat(format);
					dfMap.put(format, sdf);
				}
				try {
					Date d = sdf.parse(valueExp);
					if (!format.contains("yy")){
						//if no year set, using current year
						d.setYear(new Date().getYear());
					}
					value = d;
				} catch (ParseException e) {
					logger.error(String.format("date: %s can't be parsed using format: %s.", 
							valueExp, format), e);
				}
			}else if (VarType.INT == toType){
				valueExp = valueExp.replaceAll("\\D+","");
				value = Integer.parseInt(valueExp);
			}else if (VarType.FLOAT == toType){
				value = Float.parseFloat(valueExp);
			}else if (VarType.BOOLEAN == toType){
				value = Boolean.parseBoolean(valueExp);
			}else if (VarType.STRING == toType){
				value = valueExp;
			}else if (VarType.URL == toType){
				value = valueExp;
			}else if (VarType.LIST == toType){
				String[] vs = valueExp.split(LIST_VALUE_SEP);
				List<Object> vl = new ArrayList<Object>();
				for (String v:vs){
					if (vt!=null){
						if (VarType.STRING == vt.getToEntryType()){
							vl.add(v);
						}else if (VarType.INT == vt.getToEntryType()){
							vl.add(Integer.parseInt(v));
						}else{
							logger.error(String.format("unsupported toEntryType %s.", vt.getToEntryType()));
						}
					}else{//default as string list
						vl.add(v);
					}
				}
				return vl;
			}else{
				logger.error(String.format("toType not supported: %s", toType));
			}
		}else{
			//treated as string
			value = valueExp;
		}
		
		return value;
	}
	
	//evalue value without using page, xpath
	public static String evalStringValue(VarType fromType, String value, Map<String, Object> params){
		//transform from value
		String valueExp = null;
		if (VarType.EXPRESSION == fromType){
			valueExp = (String) ScriptEngineUtil.eval(value, VarType.STRING, params);
		}else{
			//for simple [parameter] replacement
			valueExp = StringUtil.fillParams(value, params, ConfKey.PARAM_PRE, ConfKey.PARAM_POST);
		}
		return valueExp;
	}
	
	//evalue value without using page, xpath
	public static Object eval(ValueType vt, Map<String, Object> params){
		//transform from value
		String valueExp = evalStringValue(vt.getFromType(), vt.getValue(), params);
		//get value from valueExp
		return getValue(vt, valueExp);
	}
}
