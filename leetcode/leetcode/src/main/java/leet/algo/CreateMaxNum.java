package leet.algo;

import java.util.HashMap;
import java.util.Map;

//Given two arrays of length m and n with digits 0-9 representing two numbers. 
//Create the maximum number of length k <= m + n from digits of the two. 
//The relative order of the digits from the same array must be preserved. 
//Return an array of the k digits. You should try to optimize your time and space complexity.
public class CreateMaxNum {
	private boolean max(int[] a, int[] b){
		for (int i=0; i<a.length; i++){
			if (a[i]>b[i]){
				return true;
			}else if (a[i]<b[i]){
				return false;
			}
		}
		return true;
	}
	private int max(int[] a, int sIdx){//return the first idx of the max int starting from idx
		int max = Integer.MIN_VALUE;
		int idx = sIdx;
		for (int i= sIdx; i<a.length; i++){
			if (a[i]>max){
				idx = i;
				max = a[i];
			}
		}
		return idx;
	}
	
	private int[] maxNumber(int[] nums, int s, int k){
		if (k==0) return new int[]{};
		int idx = max(nums, s);
		
	}
	private int[] maxNumber(int[] nums1, int as, int[] nums2, int bs, int k){
		if (k==0) return new int[]{};
		int n = 0;
		int[] maxn = new int[]{};
		if (as<nums1.length && bs<nums2.length){
			int aidx = max(nums1, as);
			int bidx = max(nums2, bs);
			if (nums1[aidx]>nums2[bidx]){
				n = nums1[aidx];
				maxn = maxNumber(nums1, aidx+1, nums2, bs, k-1);
			}else if (nums2[aidx]<nums2[bidx]){
				n = nums2[bidx];
				maxn = maxNumber(nums1, as, nums2, bidx+1, k-1);
			}else{
				n = nums1[aidx];
				int[] maxa = maxNumber(nums1, aidx+1, nums2, bs, k-1);
				int[] maxb = maxNumber(nums1, as, nums2, bidx+1, k-1);
				if (max(maxa, maxb)){
					maxn = maxa;
				}else{
					maxn = maxb;
				}
			}
		}else if (as<nums1.length){
			int aidx = max(nums1, as);
			n = nums1[aidx];
			maxn = maxNumber(nums1, aidx+1, k-1);
		}else if (bs<nums2.length){
			int bidx = max(nums2, bs);
			n = nums2[bidx];
			maxn = maxNumber(nums2, bidx+1, k-1);
		}
		int[] ret = new int[k];
		ret[0] = n;
		System.arraycopy(maxn, 0, ret, 1, k-1);
		return ret;
	}
	
	public int[] maxNumber(int[] nums1, int[] nums2, int k) {
		return maxNumber(nums1, 0, nums2, 0, k);
	}
}
