package leet.algo;

import java.util.Arrays;

public class MedianTwoSortedArray {
	
	//get the kth num from nums1 and nums2, k starts from 1
	public double find(int[] nums1, int start1, int[] nums2, int start2, int k){
		if (start1>=nums1.length) return nums2[start2+k-1];
		if (start2>=nums2.length) return nums1[start1+k-1];
		if (k==1) return Math.min(nums1[start1], nums2[start2]);
		int midA = Integer.MAX_VALUE;
		int midB = Integer.MAX_VALUE;
		if (start1+k/2-1<nums1.length){
			midA = nums1[start1+k/2-1];
		}
		if (start2+k/2-1<nums2.length){
			midB = nums2[start2+k/2-1];
		}
		if (midA>midB){
			return find(nums1, start1, nums2, start2+k/2, k-k/2);
		}else{
			return find(nums1, start1+k/2, nums2, start2, k-k/2);
		}
	}
	
	public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int n1 = nums1.length;
        int n2 = nums2.length;
        return 0.5*(find(nums1, 0, nums2, 0, (n1+n2+1)/2) + 
        			find(nums1, 0, nums2, 0, (n1+n2+2)/2));
    }
}
