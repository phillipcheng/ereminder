package org.cld.taskmgr;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.xml.taskdef.VarType;

public class ScriptEngineUtil {
	private static ScriptEngineManager manager = new ScriptEngineManager();
	private static Logger logger =  LogManager.getLogger(ScriptEngineUtil.class);
	
	public static Object eval(String exp, VarType toType, Map<String,Object> variables){
		ScriptEngine jsEngine = manager.getEngineByName("JavaScript");
		for (String key: variables.keySet()){
			jsEngine.put(key, variables.get(key));
		}
		try {
			Object ret = jsEngine.eval(exp);
			logger.debug(String.format("eval %s get result %s", exp, ret));
			if (ret!=null){
				if (ret instanceof String){
					if (toType == VarType.STRING){
						;
					}else if (toType == VarType.INT){
						ret = Integer.parseInt((String)ret);
					}else{
						logger.error(String.format("unsupported to type for string result: %s", toType));
					}
				}else if (ret instanceof Double){
					if (toType ==VarType.INT){
						ret = ((Double)ret).intValue();
					}else{
						logger.error(String.format("unsupported to type for double result: %s", toType));
					}
				}else if (ret instanceof Boolean){
					if (toType==VarType.BOOLEAN){
						return ret;
					}else{
						logger.error(String.format("expect a boolean result from exp:%s", exp));
						return null;
					}
				}else{
					logger.error(String.format("unsupported type of eval ret: %s", ret.getClass()));
				}
			}
			return ret;
		} catch (ScriptException e) {
			logger.error("", e);
			return null;
		}
	}
}
