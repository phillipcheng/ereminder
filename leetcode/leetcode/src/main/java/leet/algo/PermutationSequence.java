package leet.algo;

import leet.algo.test.TestAddTwoNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PermutationSequence {
	private static Logger logger =  LogManager.getLogger(TestAddTwoNumber.class);
	//Given n will be between 1 and 9 inclusive.
	public static int factorial(int n) {
        int fact = 1; // this  will be the result
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
	
	private int getI(int[] cand, int idx){//get the idx one not zero
		int target = idx+1;
		int cnt=0;
		for (int i=0; i<cand.length; i++){
			if (cand[i]!=0){
				cnt++;
			}
			if (cnt==target){
				return cand[i];
			}
		}
		return -1;
	}
	
	public String getPermutation(int n, int k) {
        int[] cand = new int[n];
        for (int i=0; i<n; i++){
        	cand[i]=i+1;
        }
        int quotient=k;
        int left=0;
        int i = n-1;
        String ret = "";
        while (i>=1){
        	int divisor = factorial(i);
        	left = (quotient-1) / divisor;//between 1..i
        	quotient = (quotient-1) % divisor + 1;
        	int v = getI(cand, left);
        	//logger.info(String.format("divisor:%d, left:%d, quotient:%d, v:%d", divisor, left, quotient, v));
        	ret += v;
        	cand[v-1]=0;
        	i--;
        }
        //add the last digit
        ret += getI(cand, 0);
        return ret;
    }

}
