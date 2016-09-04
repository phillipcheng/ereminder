package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAnagrams {

	private String sort(String in){
		char[] chars = in.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
	}
	
	public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> tree = new HashMap<String, List<String>>();
        for (String str: strs){
        	String sstr = sort(str);
        	List<String> sset = tree.get(sstr);
        	if (sset==null){
        		sset = new ArrayList<String>();
        		tree.put(sstr, sset);
        	}
        	sset.add(str);
        }
        List<List<String>> ret = new ArrayList<List<String>>();
        for (List<String> values: tree.values()){
        	Collections.sort(values);
        	ret.add(values);
        }
        return ret;
    }

}
