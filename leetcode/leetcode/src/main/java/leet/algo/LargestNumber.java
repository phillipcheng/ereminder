package leet.algo;

import java.util.Arrays;
import java.util.Comparator;

public class LargestNumber {
	
	class LexicalComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			String s1 = Integer.toString(o1) + Integer.toString(o2);
			String s2 = Integer.toString(o2) + Integer.toString(o1);
			return s1.compareTo(s2);
		}
	}
	public String largestNumber(int[] nums) {
        Integer[] inums = new Integer[nums.length];
        for (int i=0; i<nums.length; i++){
        	inums[i] = nums[i];
        }
        Arrays.sort(inums, new LexicalComparator());
        boolean allZero=true;
        for (int i=0; i<nums.length; i++){
        	if (inums[i]!=0){
        		allZero=false;
        		break;
        	}
        }
        if (allZero) return "0";
        StringBuffer sb = new StringBuffer();
        for (int i=nums.length-1; i>=0; i--){
        	sb.append(inums[i]);
        }
        return sb.toString();
    }

}
