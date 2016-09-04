package leet.algo;

public class HouseRobber {
	public int rob(int[] nums) {
		int n = nums.length;
		if (n==0) return 0;
		int[] max = new int[n];
		for (int i=0; i<n; i++){
			if (i==0){
				max[i]=nums[i];
			}else if (i==1){
				max[i]= Math.max(nums[0], nums[1]);
			}else{
				int a = max[i-2]+nums[i];
				int b = max[i-1]-nums[i-1]+nums[i];
				int c = max[i-1];
				max[i] = Math.max(Math.max(a, b),c);
			}
		}
		return max[n-1];
    }
}
