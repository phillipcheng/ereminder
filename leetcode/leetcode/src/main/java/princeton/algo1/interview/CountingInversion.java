package princeton.algo1.interview;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import princeton.algo1.interview.test.TestCountInversion;

public class CountingInversion {
	
	private static Logger logger =  LogManager.getLogger(TestCountInversion.class);
	
	private int[] input;
	private int[] helper;
	/**
	 * An inversion in an array a[] is a pair of entries a[i] and a[j] such that i<j but a[i]>a[j]. Given an array, 
	 * design a linearithmic algorithm to count the number of inversions
	 */

	private int countInversion(int start, int end){
		int ic = 0;
		if (start==end){
			return 0;
		}
		int mid = (start+end)/2;
		ic +=countInversion(start, mid);
		ic +=countInversion(mid+1, end);
		int i= start;
		int j= mid+1;
		int k = start;//index for the helper array
		while (i<=mid && j<=end){
			if (input[i]<=input[j]){//
				helper[k]=input[i];
				i++;
			}else{//i<j && input[i]>input[j]
				ic++;
				helper[k]=input[j];
				j++;
			}
			k++;
		}
		if (i>mid){//
			System.arraycopy(input, j, helper, k, end + 1 -j);
		}
		if (j>end){//
			System.arraycopy(input, i, helper, k, mid + 1 -i);
			ic += (mid-i)*(end-mid);
		}
		//copy back
		for (int l=start; l<=end; l++){
			input[l] = helper[l];
		}
		logger.info(String.format("%d->%d, %s, %d", start, end, Arrays.toString(input), ic));
		return ic;
		
	}
	public int countInversion(int[] input){
		this.helper = new int[input.length];
		this.input = input;
		
		return countInversion(0, input.length-1);
	}
	
}
