package leet.algo;

//Given an unsorted integer array, find the first missing positive integer.
//Your algorithm should run in O(n) time and uses constant space.
//
public class FirstMissingPositive {
    public int firstMissingPositive(int[] a) {
    	for (int i=0; i<a.length; i++){
    		//a[i], should be put at position i+1
    		int v = a[i];
    		while (v>=1 && v<=a.length && a[v-1]!=v){
    			int temp = a[v-1];
    			a[v-1]=v;
    			v=temp;    			
    		}
    		if (v>=1 && v<=a.length && a[v-1]!=v){
    			a[i]=v;
    		}
    	}
    	for (int i=0; i<a.length; i++){
    		if (a[i]!=i+1){
    			return i+1;
    		}
    	}
    	return a.length+1;
    }
}
