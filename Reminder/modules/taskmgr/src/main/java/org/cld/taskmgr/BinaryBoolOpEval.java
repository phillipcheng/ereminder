package org.cld.taskmgr;

import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.mytaskdef.ScriptEngineUtil;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.VarType;


public class BinaryBoolOpEval {
	
	private static Logger logger =  LogManager.getLogger(BinaryBoolOpEval.class);
	
	public static boolean eval(BinaryBoolOp bbo, Map<String, Object> attributes){
		Boolean ret = (Boolean)ScriptEngineUtil.eval(bbo.getValue(), VarType.BOOLEAN, attributes);
		return ret.booleanValue();
	}
}
