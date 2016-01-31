package cy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

//Given an array of integers, find two numbers such that they add up to a specific target number.

public class TwoSum {
    public static int[] twoSum(int[] numbers, int target) {
        int[] answer = new int[2];
        
        TreeMap<Integer, ArrayList<Integer>> tm = new TreeMap<Integer, ArrayList<Integer>>();
        
        for (int i=0; i<numbers.length; i++){
               if (tm.containsKey(numbers[i])){
                     ArrayList<Integer> l = tm.get(numbers[i]);
                     l.add(i);
               }else{
                     ArrayList<Integer> l = new ArrayList<Integer>();
                     l.add(i);
                     tm.put(numbers[i],l);
               }
        }
        
        for (int i=0; i<numbers.length;i++){
               ArrayList<Integer> l1 =tm.get(numbers[i]);
               ArrayList<Integer> l2 = tm.get(target - numbers[i]);
               if (numbers[i] == target - numbers[i]){
                     if (l1.size()>1){
                            answer[0] = l1.get(0)+1;
                            answer[1] = l1.get(1)+1;
                     }else{
                    	 //report error
                     }
               }else{
                     if (l2 != null){                                
                            int a = i;
                            int b = l2.get(0);
                            if (a<b){
                                   answer[0] = a+1;
                                   answer[1] = b+1;
                            }else{
                                   answer[0] = b+1;
                                   answer[1] = a+1;
                            }
                            
                            break;
                     }else{
                    	 //report
                     }
               }
        }
        return answer;
}

}
