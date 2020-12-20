package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * recursion on x1, x2, x3-start-index
 */
public class AdditiveNumber {
	private static Logger logger =  LogManager.getLogger(AdditiveNumber.class);
	private boolean testAdditive(String num, int i, int j){
		int startA = 0;
		int endA = i;
		int startB = i+1;
		int endB = j;
		long a = 0;
		long b = 0;
		if (num.charAt(startA)!='0' || startA==endA){
			a = Long.parseLong(num.substring(startA, endA+1));
		}else{
			return false;
		}
		if (num.charAt(startB)!='0' || startB==endB){
			b = Long.parseLong(num.substring(startB, endB+1));
		}else{
			return false;
		}
		while (true){
			long c = a+b;
			int nc = (int)Math.log10(c)+1;
			int startC = endB+1;
			int endC = startC + nc -1;
			if (num.length()>startC && (num.charAt(startC)!='0'||(startC==endC)) && num.length()>endC){
				if (Long.parseLong(num.substring(startC, endC+1))!=c){
					return false;
				}else{
					//logger.info(String.format("a:%d,b:%d,c:%d, endC:%d", a, b, c, endC));
					if (endC==num.length()-1) return true;
					a=b;
					b=c;
					endB = endC;
				}
			}else{
				return false;
			}
		}
	}
	
    public boolean isAdditiveNumber(String num) {
    	int n = num.length();
        for (int i=0; i<=n/3; i++){//first num [0,i]
        	for (int j=i+1; j<=(n+i)/2; j++){//second num[i+1,j]
        		if (testAdditive(num, i, j)){
        			return true;
        		}
        	}
        }
        return false;
    }

}
