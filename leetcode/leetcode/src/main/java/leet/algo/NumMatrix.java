package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;

public class NumMatrix {
	private static Logger logger =  LogManager.getLogger(NumMatrix.class);
	
	int[][] sum; //sum[i][j] is the sum of [0,0] to [i,j]
	int n;
	int m;
	
	public NumMatrix(int[][] matrix) {
        n = matrix.length;
        if (n>0){
	        m = matrix[0].length;
	        sum = new int[n][m];
	        for (int i=0; i<n; i++){
	        	for (int j=0; j<m; j++){
	        		if (i==0 && j==0){
	        			sum [i][j]=matrix[0][0];
	        		}else if (i==0){
	        			sum[i][j] = sum[i][j-1] + matrix[i][j];
	        		}else if (j==0){
	        			sum[i][j] = sum[i-1][j] + matrix[i][j];
	        		}else{
	        			sum[i][j] = sum[i-1][j] + sum[i][j-1] + matrix[i][j] - sum[i-1][j-1];
	        		}
	        	}
	        }
        }
        //logger.info(BoardUtil.getBoardString(sum));
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {//row1 ≤ row2 and col1 ≤ col2.
    	if (n==0) return 0;
    	if (row1==0 && col1==0){
    		return sum[row2][col2];
    	}else if (row1==0){
    		return sum[row2][col2] - sum[row2][col1-1];
    	}else if (col1==0){
    		return sum[row2][col2] - sum[row1-1][col2];
    	}else{
    		return sum[row2][col2] - sum[row2][col1-1] - sum[row1-1][col2] + sum[row1-1][col1-1];
    	}
    	
    }

}
