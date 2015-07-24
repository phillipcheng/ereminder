package org.xml.mytaskdef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.taskmgr.ScriptEngineUtil;
import org.cld.util.StringUtil;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.TasksType;
import org.xml.taskdef.ValueType;
import org.xml.taskdef.VarType;

public class TasksTypeUtil {
	
	//find the 1st matching browse task which contains the starturl
	public static BrowseTaskType getBTTByStartUrl(TasksType tt, String starturl){
		List<BrowseTaskType> bttlist = new ArrayList<BrowseTaskType>();
		for (BrowseCatType bct: tt.getCatTask()){
			bttlist.add(bct.getBaseBrowseTask());
		}
		for (BrowseDetailType bdt: tt.getPrdTask()){
			bttlist.add(bdt.getBaseBrowseTask());
		}
		
		for (BrowseTaskType btt: bttlist){
			if (starturl.equals(btt.getStartUrl())){
				return btt;
			}
			for (String sampleurl:btt.getSampleUrl()){
				if (starturl.equals(sampleurl)){
					return btt;
				}
			}
		}
		return null;
	}
	
	//evaluate the starturl getting rid of the parameters if any
	public static String getXPath(ValueType vt, Map<String,Object> params){
		if (vt.getFromType() == VarType.XPATH){
			return vt.getValue();
		}else if ((vt.getFromType()==VarType.STRING || vt.getFromType()==null) && vt.getValue().contains("/")){
			return vt.getValue();
		}else if (vt.getFromType() == VarType.EXPRESSION){
			//try evaluate
			String xpath = (String)ScriptEngineUtil.eval(vt.getValue(), VarType.STRING, params);
			if (xpath!=null && xpath.contains("/")){
				return xpath;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
}
