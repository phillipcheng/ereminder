package org.cld.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombPermUtil {
	//[a,b],[c,d] ==> [a,c],[a,d],[b,c],[b,d]
	public static List<Map<String,Object>> eachOne(Map<String, Object[]> input){
		if (input.size()>1){
			String key = input.keySet().iterator().next();
			Object[] o1 = input.get(key);
			input.remove(key);
			List<Map<String,Object>> lmo = eachOne(input);
			List<Map<String, Object>> flmo = new ArrayList<Map<String,Object>>();
			for (Object o: o1){
				for (Map<String, Object> mo:lmo){
					Map<String, Object> fmo = new HashMap<String, Object>();
					fmo.put(key, o);
					fmo.putAll(mo);
					flmo.add(fmo);
				}
			}
			return flmo;
		}else if (input.size()==1){
			List<Map<String, Object>> lmo = new ArrayList<Map<String, Object>>();
			String key = input.keySet().iterator().next();
			Object[] os = input.get(key);
			for (Object o:os){
				Map<String, Object> mo = new HashMap<String, Object>();
				mo.put(key, o);
				lmo.add(mo);
			}
			return lmo;
		}else{//size==0
			List<Map<String, Object>> llo = new ArrayList<Map<String, Object>>();
			return llo;
		}
	}
}
