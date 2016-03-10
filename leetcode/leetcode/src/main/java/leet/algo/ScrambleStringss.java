package leet.algo;

import java.util.Arrays;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScrambleStringss {
	private static Logger logger =  LogManager.getLogger(ScrambleStringss.class);
	//
	public boolean isScramble(String s1, String s2) {
		if (s1.length()!=s2.length()) return false;
		int n = s1.length();
		if (n==0) return true;
        boolean[][][] A = new boolean[n][n][n];//i,j,k means whether s1[i,i+k] scramble matches s2[j,j+k]
        for (int k=0; k<n; k++){//the length of the string - 1
			for (int i=0; i<n-k; i++){
	        	for (int j=0; j<n-k; j++){
	        		if (k==0) {
	        			A[i][j][k]=(s1.charAt(i)==s2.charAt(j));
	        		}else{
		        		boolean v = false;
		        		for (int m=0; m<k; m++){
		        			v = v || (A[i][j+k-m][m]&&A[i+1+m][j][k-1-m]||A[i][j][m]&&A[i+1+m][j+1+m][k-1-m]);
		        			if (v){
		        				break;
		        			}
		        		}
		        		A[i][j][k]=v;
	        		}
		        	//logger.info(String.format("%d,%d,%d:%b", k, i, j, A[i][j][k]));
	        	}
	        }
        }
        return A[0][0][n-1];
    }

}
