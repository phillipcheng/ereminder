package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Given an array S of n integers, are there elements a, b, c in S 
//such that a + b + c = 0? Find all unique triplets in the array which gives the sum of zero.

public class ThreeSum {
	public Set<List<Integer>> result = new HashSet<List<Integer>>();
	
	public ArrayList<Integer> sort(int[] a){
		Arrays.sort(a);
		ArrayList<Integer> al = new ArrayList<Integer>();
		for (int i=0; i<a.length; i++){
			al.add(a[i]);
		}
		
		if (!result.contains(al)){
			result.add(al);
			return al;
		}else{
			return null;
		}
	}
	
	public ArrayList<ArrayList<Integer>> threeSumONSquare(int[] num){
		Arrays.sort(num);
		ArrayList<ArrayList<Integer>> aal = new ArrayList<ArrayList<Integer>>();
		for (int i=0; i<num.length; i++){
			int j=i+1;
			int k=num.length-1;
			while (j<k){
				if (num[j]+num[k]<0-num[i]){
					j++;
				}else if (num[j]+num[k]>0-num[i]){
					k--;
				}else{
					ArrayList<Integer> al = sort(new int[]{num[i], num[j], num[k]});
					if (al!=null)
						aal.add(al);
					j++;
					k--;
				}
			}
		}
		return aal;
	}
	
	public ArrayList<ArrayList<Integer>> threeSumONSquareLgN(int[] num){
		Arrays.sort(num);
		
		ArrayList<ArrayList<Integer>> aal = new ArrayList<ArrayList<Integer>>();
		for (int i=0; i<num.length; i++){
			for (int j=i+1; j<num.length; j++){
				int k = 0-num[i]-num[j];
				int ip =-1;
				
				ip = Arrays.binarySearch(num, 0, i, k);
				if (ip>=0){
					ArrayList<Integer> al = sort(new int[]{num[i], num[j], num[ip]});
					if (al!=null)
						aal.add(al);
					continue;
				}
				ip = Arrays.binarySearch(num, i+1, j, k);
				if (ip>=0){
					ArrayList<Integer> al = sort(new int[]{num[i], num[j], num[ip]});
					if (al!=null)
						aal.add(al);
					continue;
				}
				ip = Arrays.binarySearch(num, j+1, num.length, k);
				if (ip>=0){
					ArrayList<Integer> al = sort(new int[]{num[i], num[j], num[ip]});
					if (al!=null)
						aal.add(al);
					continue;
				}
			}
		}
		return aal;
	}
	
    public ArrayList<ArrayList<Integer>> threeSum(int[] num) {
    	return threeSumONSquare(num);
    }
}
