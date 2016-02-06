package algo.leet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Given an array S of n integers, are there elements a, b, c, and d in S such that a + b + c + d = target? 
//Find all unique quadruplets in the array which gives the sum of target.
public class FourSum {
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
	
    public ArrayList<ArrayList<Integer>> fourSum(int[] num, int target) {
    	Arrays.sort(num);
        ArrayList<ArrayList<Integer>> aal = new ArrayList<ArrayList<Integer>>();
        for (int i=0; i<num.length-3; i++){
        	for (int j=i+3; j<num.length; j++){
	        	int k=i+1;
	        	int m=j-1;
	        	while(k<m){
	        		if (num[k]+num[m]<target-num[i]-num[j]){
	        			k++;
	        		}else if (num[k]+num[m]>target-num[i]-num[j]){
	        			m--;
	        		}else{
	        			ArrayList<Integer> al = sort(new int[]{num[i], num[k], num[m], num[j]});
	        			if (al!=null){
	        				aal.add(al);
	        			}
	        			k++;
	        			m--;
	        		}
	        	}
        	}
        
        }
        return aal;
    }
}
