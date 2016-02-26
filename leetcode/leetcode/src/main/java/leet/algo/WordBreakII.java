package leet.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


//Given a string s and a dictionary of words dict, 
//add spaces in s to construct a sentence where each word is a valid dictionary word.
public class WordBreakII {
	public static boolean isDebug = false;
	//string to all its broken strings
	private HashMap<String, ArrayList<String>> breakableString = new HashMap<String, ArrayList<String>>();
	private HashSet<String> unbreakableString = new HashSet<String>();
	
	//return null for not breakable, else return broken string list, 
	//mean while insert the result into final results
	public ArrayList<String> wb(String s, Set<String> dict) {
		if (breakableString.containsKey(s)){
			return breakableString.get(s);
		}
		
		if (unbreakableString.contains(s)){
			return null;
		}
		
		char[] chars = s.toCharArray();
		if (chars.length==0){
			//return empty array list
			return new ArrayList<String>();
		}
		
		int i=1;
		do{
			String sub1 = new String(chars, 0, i);
			String sub2 = new String(chars, i, chars.length-i);
			if (dict.contains(sub1)){
				ArrayList<String> strlist = wb(sub2, dict);
				if (strlist!=null){
					if (!strlist.isEmpty()){
						ArrayList<String> als = new ArrayList<String>();
						for (int j=0; j<strlist.size();j++){
							String str = strlist.get(j);
							als.add(sub1 + " " + str);
						}
						
						ArrayList<String> existALS = breakableString.get(s);
						if (existALS!=null){
							existALS.addAll(als);
							if (isDebug)
								System.out.println("for " + s + ":" + existALS);
						}else{
							breakableString.put(s, als);
							if (isDebug)
								System.out.println("for " + s + ":" + als);
						}
					}else{
						ArrayList<String> als = new ArrayList<String>();
						als.add(sub1);
						
						ArrayList<String> existALS = breakableString.get(s);
						if (existALS!=null){
							existALS.addAll(als);
							if (isDebug)
								System.out.println("for " + s + ":" + existALS);
						}else{
							breakableString.put(s, als);
							if (isDebug)
								System.out.println("for " + s + ":" + als);
						}
					}
				}
			}
			i++;		
		}while (i<=chars.length);
		
		
		if (!breakableString.containsKey(s)){
			unbreakableString.add(s);
			return null;
		}else{
			return breakableString.get(s);
		}
		
	}
	
	public ArrayList<String> wordBreak(String s, Set<String> dict) {
		if ("".equals(s) || dict.size()==0){
			return new ArrayList<String>();
		}else{
			ArrayList<String> als = wb(s, dict);
			if (als==null)
				return new ArrayList<String>();
			else
				return als;
		}
    }
}
