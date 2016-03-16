package leet.algo;

public class MaxProductSubarray {
	
	public int dynamicMaxProduct(int[] nums) {//O(n^2), O(n^2)
        int n = nums.length;
        int[][] A = new int[n][n];
        int max = Integer.MIN_VALUE;
        for (int i=0; i<n; i++){
        	A[i][i]=nums[i];
        	if (A[i][i]>max){
        		max = A[i][i];
        	}
        }
        for (int i=0; i<n; i++){
        	for (int j=i-1; j>=0; j--){
        		A[j][i] = nums[j]*A[j+1][i];
            	if (A[j][i]>max){
            		max = A[j][i];
            	}
        	}
        }
        return max;
    }
	
	public int maxProduct(int[] nums) {//since all input are integer, so the abs(product) always go upper
        int n = nums.length;
        int max = nums[0];
        int min = nums[0];
        int maxret = nums[0];
        for (int i=1; i<n; i++){
        	int a = nums[i];
        	int tmpmax = Math.max(a, Math.max(a*max, a*min));
        	int tmpmin = Math.min(a, Math.min(a*max, a*min));
        	max = tmpmax;
        	min = tmpmin;
        	maxret = Math.max(maxret, tmpmax);
        }
        return maxret;
    }

}
