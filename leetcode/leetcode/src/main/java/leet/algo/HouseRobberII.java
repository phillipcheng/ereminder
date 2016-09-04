package leet.algo;

import java.util.ArrayList;
import java.util.List;

public class HouseRobberII {
	
	private void setA(int[] max, int[] start, int[] end, int[] nums, int i){
		max[i]=max[i-2]+nums[i];
		start[i]=start[i-2];
		end[i]=i;
	}
	
	private void setB(int[] max, int[] start, int[] end, int[] nums, int i){
		max[i]=max[i-1]-nums[i-1]+nums[i];;
		start[i]=start[i-1];
		end[i]=i;
	}
	
	private void setC(int[] max, int[] start, int[] end, int[] nums, int i){
		max[i]=max[i-1];
		start[i]=start[i-1];
		end[i]=end[i-1];
	}
	
	public int rob(int[] nums) {
		int n= nums.length;
		if (n==0) return 0;
		int[] max = new int[n];
		int[] start = new int[n];
		int[] end = new int[n];
		
		for (int i=0; i<n; i++){
			if (i==0){
				max[i]=nums[i];
				start[i]=i;
				end[i]=i;
			}else if (i==1){
				if (nums[1]>=nums[0]){
					max[1]=nums[1];
					start[i]=1;
					end[i]=1;
				}else{
					max[i]=nums[0];
					start[i]=0;
					end[i]=0;
				}
			}else if (i<n-1){
				int a = max[i-2]+nums[i];
				int b = max[i-1]-nums[i-1]+nums[i];
				int c = max[i-1];
				if (a>b){
					if (a>c){
						setA(max, start, end, nums, i);
					}else if (a<c){
						setC(max, start, end, nums, i);
					}else{//a==c, between a and c, set the bigger start
						if (start[i-2]<start[i-1]){
							setC(max, start, end, nums, i);
						}else{
							setA(max, start, end, nums, i);
						}
					}
				}else if (a<b){
					if (b>=c){//b=c, between b,c 
						setB(max, start, end, nums, i);
					}else if (b<c){
						setC(max, start, end, nums, i);
					}
				}else{//a=b
					if (a>c){//a and b
						if (start[i-2]<start[i-1]){
							setB(max, start, end, nums, i);
						}else{
							setA(max, start, end, nums, i);
						}
					}else if (a<c){//c
						setC(max, start, end, nums, i);
					}else{//a=b=c
						if (start[i-2]<start[i-1]){
							setB(max, start, end, nums, i);
						}else{
							setA(max, start, end, nums, i);
						}
					}
				}
			}else{
				List<Integer> candidates = new ArrayList<Integer>();
				if (start[i-1]==0){
					candidates.add(max[i-1]);
				}else{//
					if (end[i-1]==i-1){
						int a = max[i-1] - nums[i-1]+nums[i];
						candidates.add(a);
						candidates.add(max[i-1]);
					}else{//end < i-1
						int a = max[i-1] + nums[i];
						candidates.add(a);
					}
				}
				if (start[i-2]>0){
					candidates.add(max[i-2]+nums[i]);
				}
				int m = 0;
				for (int c: candidates){
					if (c>m){
						m = c;
					}
				}
				max[i]=m;
			}
		}
		return max[n-1];
    }

}
