package cy;

//Write a function to find the longest common prefix string amongst an array of strings.
public class LongestCommonPrefix {
	public String longestCommonPrefix(String[] strs) {
		if (strs.length==0){
			return "";
		}
		if (strs.length==1){
			return strs[0];
		}
        int idx=0;
		while(true){
			char c='.';
        	for (int i=0; i<strs.length; i++){        		
        		if (strs[i].length()>idx){       			
        			if (i==0){
        				c = strs[i].charAt(idx);
        			}else{
        				if (c!=strs[i].charAt(idx)){
        					if (idx>=1)
        						return new String(strs[i].toCharArray(), 0, idx);
        					else
        						return "";
        				}
        			}
        		}else{
        			return strs[i];
        		}
        	}
        	idx++;
        }
    }
}
