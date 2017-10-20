package leet.algo;

import java.util.Collection;
import java.util.List;

//Given a string s and a dictionary of words dict, 
//determine if s can be segmented into a space-separated sequence of one or more dictionary words.
public class WordBreakI {
	
	public boolean wordBreak(String s, Collection<String> wordDict) {
		boolean[] f = new boolean[s.length()+1];//f[i] : whether s[0,i) is breakable
		f[0]=true;
		for(int i=1; i<=s.length(); i++){
			for(int j=0; j<i; j++){
				//substring[j,i)
				if(f[j] && wordDict.contains(s.substring(j,i))){
					f[i]=true;
				}
			}
		}
		return f[s.length()];
    }
}
