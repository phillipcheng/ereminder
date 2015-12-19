package org.cld.datacrawl.mgr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cld.util.StringUtil;
import org.cld.util.entity.Category;


public class CategoryUtil {
	
	public static final String NUM_PRE="(";
	public static final String NUM_POST=")";
	public static final String NAME_POST="‹ ";
	
	public static int getNumInCat(String catWithNum){
		String num = StringUtil.getStringBetweenFirstPreFirstPost(catWithNum, NUM_PRE, NUM_POST);
		if (catWithNum.length() != num.length()){ //num exist
			num = num.replace(" ", "");
			return Integer.parseInt(num);
		}
		else
			return -1;
	}
	
	public static String getNameInCat(String catWithNum){
		int beginIndex = catWithNum.indexOf(NUM_PRE);
		if (beginIndex != -1)
			return catWithNum.substring(0, beginIndex).trim();
		else
			return catWithNum;
	}
	
	public static List<String> getKeyList(List<Category> tl){
		List<String> kl = new ArrayList<String>();
		for (int i=0 ; i<tl.size(); i++){
			Category t = tl.get(i);
			kl.add(t.getId().getId());
		}
		return kl;
	}

}
