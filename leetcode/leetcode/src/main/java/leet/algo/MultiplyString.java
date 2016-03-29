package leet.algo;

import java.util.Arrays;

import leet.algo.test.TestAddTwoNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiplyString {

	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	
	private void addNum(int[] ret, int idx, int v){
		int s = ret[idx]+v;
		if (s>9){
			int a = s%10;
			int b = s/10;
			ret[idx]=a;
			ret[idx+1]+=b;
		}else{
			ret[idx]=s;
		}
	}
	public String multiply(String num1, String num2) {
        int n = num1.length();
        int m = num2.length();
        int[] ret = new int[n+m];
        for (int i=n-1; i>=0; i--){
        	for (int j=m-1; j>=0; j--){
        		int d1 = num1.charAt(i)-'0';
        		int d2 = num2.charAt(j)-'0';
        		int b = d1*d2/10;
        		int a = d1*d2%10;
        		addNum(ret, n-1-i+m-1-j, a);
        		addNum(ret, n-1-i+m-1-j+1, b);
        	}
        }
        //logger.info(Arrays.toString(ret));
        int to = n+m;
        while (ret[to-1]==0 && to>1){
        	to--;
        }
        StringBuffer sb = new StringBuffer();
        for (int i=to-1; i>=0; i--){
        	sb.append(ret[i]);
        }
        return sb.toString();
    }

}
