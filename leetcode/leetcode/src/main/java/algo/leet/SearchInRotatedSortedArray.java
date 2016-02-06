package algo.leet;

import java.util.Arrays;

//Suppose a sorted array is rotated at some pivot unknown to you beforehand.
//You are given a target value to search. If found in the array return its index, otherwise return -1.
//You may assume no duplicate exists in the array.
//O(lgN)
public class SearchInRotatedSortedArray {
	
	//search in array a from the from-index to the to-index (not including)
	public int search(int[] a, int from, int to, int target){
		if (from+1==to){
			if (a[from]==target){
				return from;
			}else{
				return -1;
			}
		}
		
		if (a[from]<a[to-1]){
			//just binary search it
			int pos = Arrays.binarySearch(a, from, to, target);
			if (pos>=0)
				return pos;
			else
				return -1;
			 
		}
		
		//split into 2 segments
		int mid = (from+to)/2;
		int pos = search(a, from, mid, target);
		if (pos<0){
			return search(a, mid, to, target);
		}else{
			return pos;
		}		
	}
	
	public int search(int[] a, int target) {
		return search(a, 0, a.length, target);
    }
}
