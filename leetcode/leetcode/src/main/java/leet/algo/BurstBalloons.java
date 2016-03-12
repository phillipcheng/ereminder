package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class BurstBalloons {
	private static Logger logger =  LogManager.getLogger(BurstBalloons.class);
	
	public int maxCoins(int[] nums) {
		int n = nums.length;
		int[][][][]A = new int[n][n][101][101];//A:i,j,l,m: the max of l,[i,j],m
		for (int j=0; j<n; j++){
			for (int l=0; l<=100; l++){
				for (int m=0; m<=100; m++){
					A[j][j][l][m] = l*nums[j]*m;
					
				}
			}
		}
		for (int j=0; j<n; j++){
			for (int i=0; i<j; i++){
				for (int l=0; l<=100; l++){
					for (int m=0; m<=100; m++){
						int max = Integer.MIN_VALUE;
						for (int k=i;k<=j;k++){
							if (k==i){
								max = Math.max(max, l*nums[i]*nums[i+1]+A[i+1][j][l][m]);
							}else if (k==j){
								max = Math.max(max, A[i][j-1][l][m] + nums[j-1]*nums[j]*m);
							}else{
								//TODO wrong
								max = Math.max(max, A[i][k-1][l][nums[k+1]]+nums[k-1]*nums[k]*nums[k+1]+A[k+1][j][nums[k-1]][m]);
							}
						}
						A[i][j][l][m]=max;
					}
				}
				int left = 1;
				if (i-1>=0){
					left = nums[i-1];
				}
				int right=1;
				if (j+1<n){
					right = nums[j+1];
				}
				logger.info(String.format("%d,%d-%d,%d: %d", left,i,j,right, A[i][j][left][right]));
			}
		}
		return A[0][n-1][1][1];
    }
}
