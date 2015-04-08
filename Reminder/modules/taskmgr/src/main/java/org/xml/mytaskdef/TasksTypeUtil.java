package org.xml.mytaskdef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.util.StringUtil;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.BrowseDetailType;
import org.xml.taskdef.ParamType;
import org.xml.taskdef.TasksType;

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
	public static String getEvaledStartUrl(BrowseTaskType btt, String startUrl){
		String orgStartUrl=null;
		Map<String, Object> params = new HashMap<String, Object>();
		orgStartUrl = btt.getStartUrl();
		if (btt.getParam()!=null){
			for (ParamType pt:btt.getParam()){
				if (pt.getValue()!=null){
					params.put(pt.getName(), pt.getValue());
				}
			}
			return StringUtil.fillParams(orgStartUrl, params, ConfKey.PARAM_PRE, ConfKey.PARAM_POST);
		}else{
			return orgStartUrl;
		}
	}
	
	public static String getOrgStartUrl(TasksType tt){
		if (tt.getCatTask().size()>0){
			return tt.getCatTask().get(0).getBaseBrowseTask().getStartUrl();
		}else if (tt.getPrdTask().size()>0){
			return tt.getPrdTask().get(0).getBaseBrowseTask().getStartUrl();
		}else{
			return null;
		}
	}
}
