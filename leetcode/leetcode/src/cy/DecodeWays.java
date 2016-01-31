package cy;

import java.util.HashMap;

//A message containing letters from A-Z is being encoded to numbers using the following mapping:
//'A' -> 1
//'B' -> 2
//...
//'Z' -> 26
//Given an encoded message containing digits, determine the total number of ways to decode it.
public class DecodeWays {
	
	private char[] chars;
	
	//startIdx maps to result
	private HashMap<Integer,Integer> results = new HashMap<Integer,Integer>();
	
	public int numDecoding(int startIdx){
		if (!results.containsKey(startIdx)){
			int ret=0;
			if (startIdx >= chars.length-1){
				if (startIdx==chars.length-1 && chars[startIdx]=='0'){
					ret =0;
				}else{
					ret= 1;
				}
			}else if (chars[startIdx]=='0'){
				ret =0;
			}
			else if (chars[startIdx]>'2' || chars[startIdx]=='2' && chars[startIdx+1]>'6'){
				ret= numDecoding(startIdx+1);
			}else{
				ret= numDecoding(startIdx+1) + numDecoding(startIdx+2);
			}
			results.put(startIdx, ret);
			return ret;
		}else{
			return results.get(startIdx);
		}
	}
	
	public int numDecodings(String s) {
		if ("".equals(s)){
			return 0;
		}
		chars = s.toCharArray();
		
        return numDecoding(0);
    }

}
