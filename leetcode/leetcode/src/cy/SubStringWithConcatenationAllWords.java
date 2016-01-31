package cy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

//You are given a string, S, and a list of words, L, that are all of the same length.
//Find all starting indices of substring(s) in S that is a concatenation of each word in L exactly once 
//and without any intervening characters.


public class SubStringWithConcatenationAllWords {
	
	class MatchResult{
		int offset=0; //indicates the starting matching char idx in key/input string
		int matchCount=0;//total matched count
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>(); //not fully matched key to number of keys already matched
		
		public MatchResult(){
			
		}
		
		public void copyFrom(MatchResult mr){
			offset+=mr.offset;
			matchCount = mr.matchCount;
			Iterator<String> keys = mr.resultMap.keySet().iterator();
			while (keys.hasNext()){
				String key = keys.next();
				int v = mr.resultMap.get(key);
				resultMap.put(key, v);
			}
		}
		
		public MatchResult clone(){
			MatchResult ret = new MatchResult();
			ret.matchCount = this.matchCount;
			ret.offset = this.offset;
			Iterator<String> keys = resultMap.keySet().iterator();
			while (keys.hasNext()){
				String key = keys.next();
				int v = resultMap.get(key);
				ret.resultMap.put(key, v);
			}
			return ret;
		}
		
		MatchResult(String[] L){
			matchCount=0;
			reset(L);
		}
		
		public void reset(String[] L){			
			for (int i=0; i<L.length; i++){
	    		String s = L[i];
	    		resultMap.put(s, 0);	    		
			}
			matchCount=0;
			offset=0;
		}
		
		//input length == step
		//return true, means matches 1 or the remaining keys, false means start all over needed
		public boolean match(String input){
			if (resultMap.containsKey(input)){
				matchCount++;
				int c = resultMap.get(input);
				c++;
				resultMap.put(input, c);
				if (c==totalCount.get(input)){
					resultMap.remove(input);
				}
				return true;
			}else{
				return false;
			}
		}		
		
		//input length=step*n (n<L.length)
		//return false if no potential match (number of un-matched char less then pl) found, else true
		//set the match result to the earliest match
		public boolean matchMultiple(String input, String[] L){
			int pl = L[0].length();
			int i=1;
			offset=i;
			boolean thisMatch=false;
			boolean preMatch = false;
			
			for (;i<=input.length()-pl;){
				String in = input.substring(i, i+pl);
				if (match(in)){
					i=i+pl;
					thisMatch=true;
				}else{
					thisMatch=false;
					if (preMatch==true){
						String key = input.substring(offset, offset+pl*matchCount);
						if (matchCache.containsKey(key)){
							copyFrom(matchCache.get(key));//offset is set
							i=offset+pl*matchCount;
							thisMatch=true;
						}else{
							offset++;
							i=offset;
							thisMatch=false;
						}
					}else{
						offset++;
						i=offset;
					}
				}
				preMatch=thisMatch;
			}
			if (matchCount>0){
				return true;
			}else{
				return false;
			}
		}
		
		public String toString(){
			StringBuffer sb = new StringBuffer();
			sb.append("\n");
			sb.append("offset:" + this.offset + "\n");
			sb.append("match count:" + this.matchCount + "\n");
			sb.append(resultMap.toString() + "\n");
			return sb.toString();
		}
	}
	
	//O(n*l*w)
	//n = S.length, l = L.length, w = L[i].length
    
	public static boolean isDebug = false;
	HashMap<String, MatchResult> matchCache = new HashMap<String, MatchResult>(); //cached match results
	HashMap<String, Integer> totalCount = new HashMap<String, Integer>();//total count
	int firstMatchPos=-1;
    
	public ArrayList<Integer> findSubstring(String S, String[] L) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		if (L.length==0){
			return ret;
		}
		int pl = L[0].length(); //pattern length
		//set total count
		for (int i=0; i<L.length; i++){
    		String s = L[i];
    		if (totalCount.containsKey(s)){
    			totalCount.put(s, totalCount.get(s)+1);
    		}else{
    			totalCount.put(s, 1);
    		}
		}
		
		boolean preMatch=false;
		boolean thisMatch=false;
		MatchResult mr = new MatchResult(L);
		String input;
		for (int i=0; i<=S.length()-pl; ){
			input = S.substring(i, i+pl);
			
			boolean matched = false;
			matched = mr.match(input);
			
			if (isDebug){
				System.out.println(input);
				System.out.println("matched:" + matched);
				System.out.println(mr);
			}
			
			if (matched){
				if (mr.matchCount==1){
					firstMatchPos=i;
				}
				thisMatch = true;
				if (mr.matchCount==L.length){
					ret.add(firstMatchPos);
					input = S.substring(firstMatchPos, firstMatchPos+pl*L.length);
					if (matchCache.containsKey(input)){
						mr = matchCache.get(input).clone();
						firstMatchPos += mr.offset;
						i = firstMatchPos + mr.matchCount*pl;
					}else{
						//add to cache
						mr.reset(L);
						if (mr.matchMultiple(input, L)){
							MatchResult cmr = mr.clone();
							matchCache.put(input, cmr);
							firstMatchPos += mr.offset;
							i = firstMatchPos + mr.matchCount*pl;
							thisMatch=true;
						}else{
							thisMatch=false;
							mr.reset(L);
							i=firstMatchPos+1;
						}
					}
				}else{
					i+=pl;
				}
			}else{
				thisMatch=false;
				//use the nextMap to optimize
				//optimize i step and matchTargets
				if (preMatch==true){	
					input = S.substring(firstMatchPos, firstMatchPos+pl*mr.matchCount);
					if (matchCache.containsKey(input)){
						mr = matchCache.get(input).clone();
						firstMatchPos +=mr.offset;
						i = firstMatchPos + mr.matchCount*pl;
					}else{
						//add to cache
						mr.reset(L);
						if (mr.matchMultiple(input, L)){
							MatchResult cmr = mr.clone();
							matchCache.put(input, cmr);
							firstMatchPos +=mr.offset;
							i = firstMatchPos + mr.matchCount*pl;
							thisMatch=true;
						}else{
							thisMatch=false;
							mr.reset(L);
							i=firstMatchPos+1;
						}
					}
				}else{
					//no optimizing here
					i++;
				}
			}		
			preMatch = thisMatch;
		}
		
		if (isDebug){
			System.out.println("match cache:" + matchCache);
		}
        return ret;
    }
}
