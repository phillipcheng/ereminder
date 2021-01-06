package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Given a sorted positive integer array nums and an integer n, 
//add/patch elements to the array such that any number in range [1, n] inclusive can be formed by the sum of some elements in the array. 
//Return the minimum number of patches required.
public class PatchingArray {
	private static Logger logger =  LogManager.getLogger(PatchingArray.class);
	public int minPatches(int[] nums, int n) {
		List<Long> addedList = new ArrayList<Long>();
		int idx=0;
		long maxCover=0;
		while (maxCover<n){
			long toTry=maxCover+1;
			if (idx<nums.length){
				if (nums[idx]>toTry){
					addedList.add(toTry);
					maxCover = 2*maxCover+1;
				}else {
					maxCover = maxCover + nums[idx];
					idx++;
				}
			}else{
				addedList.add(toTry);
				maxCover = 2*maxCover+1;
			}
			System.err.println(String.format("maxCover:%d, addedList:%s", maxCover, addedList));
		}
		return addedList.size();
	 }
}
