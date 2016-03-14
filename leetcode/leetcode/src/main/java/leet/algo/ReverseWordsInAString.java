package leet.algo;

public class ReverseWordsInAString {
	
	public String reverseWords(String s) {
		String[] strs = s.split("\\s+");
        StringBuffer sb = new StringBuffer();
        for (int i=strs.length-1; i>=0; i--){
        	if (i<strs.length-1){
        		if (i>0){
        			sb.append(" ");
        		}else{
        			if (!"".equals(strs[0])){
        				sb.append(" ");
        			}
        		}
        	}
        	sb.append(strs[i]);
        }
        return sb.toString();
    }

}
