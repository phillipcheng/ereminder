package leet.algo;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinimumSizeSubarraySum {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	//O(n^2)
	public int minSubArrayLenO2(int s, int[] nums) {
		if (nums.length==0) return 0;
		int n = nums.length;
		int[][] sum = new int[n][n];
        for (int i=0; i<n; i++){
        	for (int j=0; j<n-i;j++){
        		if (i==0){
        			sum[j][i+j]=nums[i];
        		}else{
        			sum[j][i+j] = sum[j][i+j-1]+nums[i+j];
        		}
        		if (sum[j][i+j]>=s){
        			return i;
        		}
        	}
        }
		return 0;
    }
	
	public int minSubArrayLen(int s, int[] nums) {
		if (nums.length==0) return 0;
		int n = nums.length;
		int i=0;//
		int j=0;//advancing pointer
		int sum = nums[i];
		int min = Integer.MAX_VALUE;
		while (j<n){
			while (sum<s){
				j++;
				if (j<n)
					sum = sum + nums[j];
				else
					break;
			}
			if (j>=n) break;
			while (sum>=s && i<=j){
				sum = sum - nums[i];
				i++;
			}
			int i0 = i-1;
			int len = j-i0+1;
			min = Math.min(min, len);
			//logger.info(String.format("%d, %d, sum:%d", i0, j, sum+nums[i0]));
		}
		if (min == Integer.MAX_VALUE)  min=0;
		return min;
    }

}
