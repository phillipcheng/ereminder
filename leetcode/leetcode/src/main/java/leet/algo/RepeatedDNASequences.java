package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepeatedDNASequences {
	
	public List<String> findRepeatedDnaSequences(String s) {
		Set<String> ret = new HashSet<String>();
		Set<String> set = new HashSet<String>();
        for (int i=0; i<=s.length()-10; i++){
        	String str = s.substring(i, i+10);
        	if (set.contains(str)){
        		ret.add(str);
        	}else{
        		set.add(str);
        	}
        }
        List<String> listret = new ArrayList<String>();
        listret.addAll(ret);
        return listret;
    }
}
