package leet.algo;

import java.util.Arrays;

import leet.algo.test.TestInsertIntervals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;

//Given s1, s2, s3, find whether s3 is formed by the interleaving of s1 and s2.
public class InterleavingString {
	private static Logger logger =  LogManager.getLogger(InterleavingString.class);
	//
	public boolean isInterleave(String s1, String s2, String s3) {
        int m = s1.length();
        int n = s2.length();
        if (s3.length()!=m+n){
        	return false;
        }
        boolean[][] A = new boolean[m+1][n+1];
        //A[i][j] means s3 start from i+j can be inter-left by s1 start from i and s2 start from j
        A[m][n]=true;
        for (int i=n-1; i>=0; i--){
        	A[m][i] = s3.charAt(m+i)==s2.charAt(i) && A[m][i+1];
        }
        //logger.info(BoardUtil.getBoardString(A));
        for (int i=m-1; i>=0; i--){
        	A[i][n] = s3.charAt(n+i)==s1.charAt(i) && A[i+1][n];
        }
        //logger.info(BoardUtil.getBoardString(A));
        for (int i=m-1; i>=0; i--){
        	for (int j=n-1; j>=0; j--){
    			A[i][j] = s3.charAt(i+j)==s1.charAt(i) && A[i+1][j] || 
    					s3.charAt(i+j)==s2.charAt(j) && A[i][j+1];
        	}
        	//logger.info(BoardUtil.getBoardString(A));
        }
		return A[0][0];
    }

}
