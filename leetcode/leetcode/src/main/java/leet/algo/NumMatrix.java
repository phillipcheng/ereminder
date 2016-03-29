package leet.algo;

import leet.algo.test.TestAddTwoNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.BoardUtil;

public class NumMatrix {
	private static Logger logger =  LogManager.getLogger(NumMatrix.class);
	int[][] sum;
	int n;
	int m;
	
	public NumMatrix(int[][] matrix) {
        n = matrix.length;
        m = matrix[0].length;
        sum = new int[n+1][m+1];
        for (int r=n; r>=0; r--){
        	for (int c=0; c<=m; c++){
        		if (c==0 || r==n) 
        			sum[r][c]=0;
        		else{
        			sum[r][c]=matrix[r][c-1]+sum[r][c-1]+sum[r+1][c]-sum[r+1][c-1];
        			logger.info(BoardUtil.getBoardString(sum));
        		}
        	}
        }
    }

    public int sumRegion(int row1, int col1, int row2, int col2) {//row1 ≤ row2 and col1 ≤ col2.
    	int x1 = m-col1;
    	int y1 = row1;
    	int x2 = m-col2;
    	int y2 = row2;
    	logger.info(String.format("%d,%d->%d  %d,%d->%d", x1,y1,sum[x1][y1], x2,y2, sum[x2][y2]));
        return sum[x2][y2]+sum[x1+1][y1-1]-sum[x1+1][y2]-sum[x2][y1-1];
    }

}
