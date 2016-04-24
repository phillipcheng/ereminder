package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxSquare {
	private static Logger logger =  LogManager.getLogger(MaxSquare.class);
	
	public int maximalSquare(char[][] matrix) {
		int n = matrix.length;
		if (n==0) return 0;
		int m = matrix[0].length;
		int[][] A = new int[n][m]; //the max square edge length at matrix[i][j]
		int maxArea = 0;
		for (int i=0; i<n; i++){
			for (int j=0; j<m; j++){
				if (matrix[i][j]=='0'){
					A[i][j]= 0;
				}else{
					if (i==0 || j==0){
						A[i][j] = 1;
					}else{
						if (A[i][j-1]==0 || A[i-1][j]==0){
							A[i][j] = 1;
						}else if (A[i][j-1]==A[i-1][j]){
							int c = A[i][j-1];
							if (matrix[i-c][j-c]=='1'){
								A[i][j]=c+1;
							}else{
								A[i][j]=c;
							}
						}else{
							A[i][j] = Math.min(A[i][j-1], A[i-1][j])+1;
						}	
					}
				}
				int area = A[i][j] * A[i][j];
				if (area>maxArea){
					maxArea = area;
				}
				//logger.info(String.format("A[%d][%d]=%s, maxArea:%d", i, j, A[i][j], maxArea));
			}
		}
        return maxArea;
    }

}
