package cy;

import java.util.Arrays;

//Given an array S of n integers, find three integers in S such that the sum is closest to a given number, target. 
//Return the sum of the three integers. You may assume that each input would have exactly one solution.

public class ThreeSumClosest {
	
	//sorted input, return two 
	public int twoSumClosest(int[] num, int startIdx, int target, int[] output){
		int j = startIdx;
		int k = num.length-1;
		int curGap = target - num[j] - num[k];
		int preGap = curGap;
		int prej=j;
		int prek=k;
		while (j<k){
			curGap = target - num[j] - num[k];
			if (curGap>0){
				prej = j;
				j++;
			}else if (curGap<0){
				prek = k;
				k--;
			}else{
				output[0] = j;
				output[1] = k;
				return (num[j]+num[k]);
			}
			if (Math.abs(preGap)<Math.abs(curGap)){//go worse
				output[0] = prej;
				output[1] = prek;
				return (num[prej]+num[prek]);
			}
			preGap = curGap;
		}
		output[0] = prej;
		output[1] = prek;
		return target - curGap;
	}
	
	//sorted input
	public int threeSumClosestONSquare(int[] num, int target){
		int minGap = Integer.MAX_VALUE;
		int cThreeSum = 0;
		for (int i=0; i<num.length-2; i++){
			int output[] = new int[2];
			int cTwoSum = twoSumClosest(num, i+1, target-num[i], output);
			int cGap = Math.abs(target - num[i]-cTwoSum);
			//System.out.println(String.format("gap:%d from %d value %d, %d value %d, %d value %d", cGap, i, num[i], output[0], num[output[0]], output[1], num[output[1]]));
			if (cGap < minGap){
				minGap = cGap;
				cThreeSum = cTwoSum + num[i];
			}
		}
		return cThreeSum;
	}
	
    public int threeSumClosest(int[] num, int target) {
    	Arrays.sort(num);
    	return threeSumClosestONSquare(num, target);
    }
}
