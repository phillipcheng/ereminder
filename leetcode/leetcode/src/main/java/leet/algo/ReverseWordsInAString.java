package leet.algo;

public class ReverseWordsInAString {
	
	/*
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
	*/
	
	public String reverseWords(String s) {
        String[] w = s.trim().split("\\s+", -1);
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<w.length; i++){
            sb.append(w[w.length-1-i]);
            if (i<(w.length-1)){
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
