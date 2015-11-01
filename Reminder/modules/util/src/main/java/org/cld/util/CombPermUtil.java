package org.cld.util;

import java.util.ArrayList;
import java.util.List;

public class CombPermUtil {
	
	//[a,b],[c,d] ==> [a,c],[a,d],[b,c],[b,d]
	public static List<List<Object>> eachOne(List<Object[]> input){
		if (input.size()>1){
			Object[] o1 = input.get(0);
			List<Object[]> oleft = input.subList(1, input.size());
			List<List<Object>> llo = eachOne(oleft);
			List<List<Object>> fllo = new ArrayList<List<Object>>();
			for (Object o: o1){
				for (List<Object> lo:llo){
					List<Object> flo = new ArrayList<Object>();
					flo.add(o);
					flo.addAll(lo);
					fllo.add(flo);
				}
			}
			return fllo;
		}else if (input.size()==1){
			List<List<Object>> llo = new ArrayList<List<Object>>();
			Object[] os = input.get(0);
			for (Object o:os){
				List<Object> lo = new ArrayList<Object>();
				lo.add(o);
				llo.add(lo);
			}
			return llo;
		}else{//size==0
			List<List<Object>> llo = new ArrayList<List<Object>>();
			return llo;
		}
	}

}
