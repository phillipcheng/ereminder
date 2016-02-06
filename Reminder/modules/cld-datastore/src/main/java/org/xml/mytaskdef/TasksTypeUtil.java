package org.xml.mytaskdef;

import java.util.Map;

import org.cld.util.ScriptEngineUtil;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

public class TasksTypeUtil {
	
	//evaluate the starturl getting rid of the parameters if any
	public static XPathType getXPath(ValueType vt, Map<String,Object> params){
		if (vt.getFromType() == VarType.XPATH){
			return new XPathType(vt.getValue(), vt.getFrameId());
		}else if ((vt.getFromType()==VarType.STRING || vt.getFromType()==null) && vt.getValue().contains("/")){
			return new XPathType(vt.getValue(), vt.getFrameId());
		}else if (vt.getFromType() == VarType.EXPRESSION){
			//try evaluate
			String xpath = (String)ScriptEngineUtil.eval(vt.getValue(), VarType.STRING, params);
			if (xpath!=null && xpath.contains("/")){
				return new XPathType(xpath, vt.getFrameId());
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
}
