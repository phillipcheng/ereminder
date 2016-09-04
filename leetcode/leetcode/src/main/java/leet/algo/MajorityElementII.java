package leet.algo;

import java.util.ArrayList;
import java.util.List;

//Given an array of size n, find the majority element. 
//The majority element is the element that appears more than ⌊ n/3 ⌋ times.
//O(n) time, O(1) space
public class MajorityElementII {
	public List<Integer> majorityElement(int[] nums) {
        int count1=0;
        int count2=0;
        int major1=0;
        int major2=0;
		for (int num:nums){
        	if (major1==num){
        		count1++;
        	}else if (major2==num){
        		count2++;
        	}else if (count1==0){
        		major1 = num;
        		count1++;
        	}else if (count2==0){
        		major2 = num;
        		count2++;
        	}else{
        		count1--;
        		count2--;
        	}
        }
		int rcnt1=0;
		int rcnt2=0;
		for (int num:nums){
			if (num==major1){
				rcnt1++;
			}else if (num==major2){
				rcnt2++;
			}
		}
		List<Integer> li = new ArrayList<Integer>();
		int thresh = nums.length/3;
		if (rcnt1>thresh){
			li.add(major1);
		}
		if (rcnt2>thresh){
			li.add(major2);
		}
		return li;
    }
}
