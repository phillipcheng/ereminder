package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


//Given a string S and a string T, find the minimum window in S which 
//will contain all the characters in T in complexity O(n).
//S = "ADOBECODEBANC" T = "ABC" Minimum window is "BANC".

//not rotate the list, sacrifice memory for time
class MatchEntry{
	int count;//should have
	int startIdx;
	int endIdx;
	ArrayList<Integer> positions;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("count:" + count + ",");
		sb.append("startIdx:" + startIdx + ",");
		sb.append("endIdx:" + endIdx + ",");
		sb.append("positions:" + positions);
		return sb.toString();
		
	}
}

class MatchResult{
	public static boolean isDebug=false;
	String s;
	String t;
	//map from character in T to positions in S
	HashMap<Character, MatchEntry> map = new HashMap<Character, MatchEntry>();
	
	int matchNum=0; //
	int minSpan=0;//
	int minSpanStart=0;
	int minSpanEnd = 0;
	
	MatchResult(String S, String T){
		if (isDebug){
			System.out.println("S:" + S);
			System.out.println("T:" + T);
		}
		s = S;
		t = T;
		for (int i=0; i<T.length(); i++){
			char c = T.charAt(i);
			if (map.containsKey(c)){
				map.get(c).count++;
			}else{
				MatchEntry me = new MatchEntry();
				me.count=1;
				me.positions = new ArrayList<Integer>();	
				map.put(c, me);
			}
		}
	}
	
	void match(char c, int idx){
		if (matchNum<t.length()){
			if (map.containsKey(c)){
				MatchEntry me = map.get(c);
				if (me.endIdx-me.startIdx<me.count){
					//add c
					me.positions.add(idx);
					me.endIdx++;
					matchNum++;
					if (matchNum==t.length()){
						//1st match, record
						minSpanStart = getStartIdx();
						minSpanEnd = getEndIdx();
						minSpan = minSpanEnd-minSpanStart+1;
						if (isDebug){
							System.out.println(s.substring(minSpanStart,minSpanEnd+1));
						}
					}
				}else{
					//rotate the list
					me.positions.add(idx);
					me.startIdx++;
					me.endIdx++;
				}
			}
		}else{
			if (map.containsKey(c)){
				//replacement
				//rotate the list
				MatchEntry me = map.get(c);
				me.positions.add(idx);
				me.startIdx++;
				me.endIdx++;
				
				int start = getStartIdx();
				int end = getEndIdx();
				if (isDebug){
					System.out.println(map);
					System.out.println("start:" + start);
					System.out.println("end:" + end);
				}
				int span = end-start+1;
				if (span < minSpan){
					minSpanStart = start;
					minSpanEnd = end;
					minSpan = span;
					if (isDebug){
						System.out.println(s.substring(minSpanStart,minSpanEnd+1));
					}
				}else{
					if (isDebug){
						System.out.println("candidate:" + s.substring(start,end-start+1));
					}
				}
			}			
		}
	}
	
	int getStartIdx(){
		int start = s.length();
		Iterator<MatchEntry> it = map.values().iterator();
		while (it.hasNext()){
			MatchEntry me = it.next();
			if (start>me.positions.get(me.startIdx)){
				start=me.positions.get(me.startIdx);
			}
		}
		return start;
	}
	
	int getEndIdx(){
		int end = 0;
		Iterator<MatchEntry> it = map.values().iterator();
		while (it.hasNext()){
			MatchEntry me = it.next();
			if (end<me.positions.get(me.endIdx-1)){
				end=me.positions.get(me.endIdx-1);
			}
		}
		return end;
	}
}
public class MinWindowSubstring {
	public String minWindow(String S, String T) {
		
		MatchResult mr = new MatchResult(S,T);
        for (int i=0; i<S.length(); i++){
        	mr.match(S.charAt(i),i);
        }
        if (mr.matchNum<T.length()){
        	return "";
        }else{
        	return new String(S.toCharArray(), mr.minSpanStart, mr.minSpan);
        }
    }
}
