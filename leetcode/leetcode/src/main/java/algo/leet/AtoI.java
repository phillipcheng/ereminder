package algo.leet;

public class AtoI {
	
	public static boolean isDebug = false;
	
	public boolean isNumeric(char ch){
		if (ch>='0' && ch<='9'){
			return true;
		}else
			return false;
	}
	/**
	 * get
	 * @param str
	 * @param startIdx
	 * @return the idx of the 1st none-space character
	 */
	public int getFirstNoneSpaceValidCh(char[] str){
		for (int i=0; i<str.length;i++){
			char c = str[i];
			if (c!=' '){
				if (c=='+'||c=='-'||isNumeric(c))
					return i;
				else
					return -1;
			}
		}
		return -1;
	}
	
	/**
	 * return the number from str[idx] (including)
	 * @param str
	 * @param idx
	 * @return 
	 * 1. Integer.MAX_VALUE: max value reached
	 * 2. -1, nothing found
	 * 3. integer < max, means integer found
	 */
	public long[] getNumber(char[] str, int idx){
		long[] retArray = new long[]{-1,-1};
		int i = idx;
		char ch;
		long ret=-1;
		long digits=1;
		int j=0;
		while (i<str.length && isNumeric(ch=str[i++])){
			if (ret==-1){
				ret=0;
			}
			j++;
			long v = ch-'0';
			ret = ret * 10 + v;
			if (j>1)
				digits = 10*digits;
			
			if (ret>Integer.MAX_VALUE){
				ret = Integer.MAX_VALUE+1l;
				break;
			}
		}
		retArray[0] = ret;
		retArray[1] = digits;
		return retArray;
	}
	
	
	public int atoi(String str) {
		if (isDebug){
			System.out.println("input str:" + str);
		}
        char[] chars = str.toCharArray();
        int idx = getFirstNoneSpaceValidCh(chars);
        if (idx==-1)
        	return 0;
        else{
        	char c = chars[idx];
        	long result=-1;
        	int sign=1;
        	if (c=='+'){
        		sign=1;
        	}else if (c=='-'){
        		sign = -1;
        	}else{
        		result = (c-'0');
        	}
        	
        	if (isDebug){
        		System.out.println("sign:" + sign);
        		System.out.println("1st result:" + result);
        		System.out.println("1st valid ch idx:" + idx);
        	}
        	
    		long retArray[] = getNumber(chars, idx+1);
    		long a = retArray[0];
    		long digits = retArray[1];
    		if (isDebug){
    			System.out.println("get number after 1st valid char:" + a);
    			System.out.println("digits:" + digits);
    		}
    		
    		if (a>Integer.MAX_VALUE){
    			if (sign==1){
    				return Integer.MAX_VALUE;
    			}else{
    				return Integer.MIN_VALUE;
    			}
    		}else if (a==-1){
    			if (result >0){
    				return (int) result;
    			}else{
    				return 0;
    			}
    		}else{
    			if (result>0){//1st char is number
    				result = 10 * digits * result + a;
    				if (result>Integer.MAX_VALUE){
    					return Integer.MAX_VALUE;
    				}else{
    					return (int) result;
    				}
    			}else{
    				if (sign==-1)
    					return (int)(-1*a);
    				else{
    					return (int)a;
    				}
    			}
    		}        	
        }
    }
}
