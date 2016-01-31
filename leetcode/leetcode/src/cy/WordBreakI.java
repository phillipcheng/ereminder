package cy;

import java.util.HashSet;
import java.util.Set;


//Given a string s and a dictionary of words dict, 
//determine if s can be segmented into a space-separated sequence of one or more dictionary words.
public class WordBreakI {
	private HashSet<String> breakableString = new HashSet<String>();
	private HashSet<String> unbreakableString = new HashSet<String>();
	
	public boolean wb(String s, Set<String> dict) {
		if (breakableString.contains(s)){
			return true;
		}
		
		if (unbreakableString.contains(s)){
			return false;
		}
		
		char[] chars = s.toCharArray();
		if (chars.length==0)
			return true;
		
		int i=1;
		String sub1 = new String(chars, 0, i);
		String sub2 = new String(chars, i, chars.length-i);
		while (true){
			if (dict.contains(sub1) && wb(sub2, dict)){
				breakableString.add(s);
				return true;
			}
			i++;
			if (i>chars.length){
				unbreakableString.add(s);
				return false;
			}
			sub1 = new String(chars, 0, i);
			sub2 = new String(chars, i, chars.length-i);
		}
	}
	public boolean wordBreak(String s, Set<String> dict) {
		if ("".equals(s)){
			return false;
		}else{
			return wb(s, dict);
		}
    }
}
