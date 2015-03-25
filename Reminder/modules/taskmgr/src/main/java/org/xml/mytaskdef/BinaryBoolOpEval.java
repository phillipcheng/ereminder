package org.xml.mytaskdef;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.StringUtil;
import org.xml.taskdef.BinaryBoolOp;
import org.xml.taskdef.OpType;


public class BinaryBoolOpEval {
	private static Logger logger =  LogManager.getLogger(BinaryBoolOpEval.class);
	
	private static String getValue(String varName, Map<String, Object> values){
		Object value = null;
		if (varName.startsWith("$")){
			varName = varName.substring(1);
			if (values.containsKey(varName)){
				value = values.get(varName);
			}else{
				logger.error(String.format("variable %s not found in values:%s", varName, values));
			}
		}else{
			logger.error(String.format("variable should start with $, %s", varName));
		}
		if (value == null){
			return null;
		}else{
			return value.toString();
		}
	}
	
	public static boolean eval(BinaryBoolOp bbo, Map<String, Object> values){
		String lhsVarName = bbo.getLhs(); //variable
		
		OpType op = bbo.getOperator();
		String lhsValue = getValue(lhsVarName, values);
		String rhsValue = null;
		String rhsVarName = bbo.getRhs();
		if (rhsVarName.startsWith("$")){
			rhsValue = getValue(rhsVarName, values);
		}else{
			//const value
			if (rhsVarName.equals("")){
				rhsValue = null;
			}else{
				rhsValue = rhsVarName;
			}
		}
		if (OpType.NOTEQUALS.equals(op.value())){//!=
			return !StringUtil.urlEqual(lhsValue, rhsValue);
		}else{//=
			return StringUtil.urlEqual(lhsValue, rhsValue);
		}
	}

}
