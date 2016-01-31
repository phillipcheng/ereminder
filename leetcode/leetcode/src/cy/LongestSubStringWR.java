package cy;


//Given a string, find the length of the longest substring without repeating characters. 
//For example, the longest substring without repeating letters 
//for "abcabcbb" is "abc", which the length is 3. For "bbbbb" the longest substring is "b", with the length of 1.

public class LongestSubStringWR {
	
	private static boolean isDebug=false;
	
	char[] chars;
	int maxCount=0;
	int maxStartIdx=0;
	 
	public void recordMax(int len, int idx){
		
		if (len>maxCount){
			maxCount=len;
			maxStartIdx = idx;
			if (isDebug)
				System.out.println("recorded: from:" + maxStartIdx + ", len:" + maxCount + ", " + new String(chars, maxStartIdx, maxCount));
		}
	}
	
	 public int lengthOfLongestSubstring(String s) {
		if (isDebug)
			 System.out.println("input:" + s);
		chars = s.toCharArray();	
		
		if (chars.length==1){
			return 1;
		}
		int count =0;
		for (int i=0; i<chars.length; i++){
			 for (int j=i+1; j<chars.length; j++){
				 if (isDebug)
					 System.out.println("i,j:" + i + "," + j);
				 //compare j against the string from i to j-1
				 int ip=-1;
				 for (int k=i; k<j; k++){
					 if (chars[k]==chars[j]){
						 count++;
						 ip = k;
						 break;
					 }
				 }
				 if (ip<0){
					 //if last char, record it
					 if (j==chars.length-1){
						 int len = j-i+1;
						 recordMax(len, i);
					 }else{
						 //not find, increment j
					 }
				 }else{
					 //find dup, record it
					 int len = j-i;
					 recordMax(len, i);
					 i = ip;
					 break;
				 }
			 }
			 
		 }
		
		if (isDebug)
			System.out.println("maxStartIdx:" + maxStartIdx);
		 
		System.out.println("count:" + count);
		 return maxCount;
	        
	 }

}
