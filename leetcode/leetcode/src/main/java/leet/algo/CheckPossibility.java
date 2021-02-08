package leet.algo;

import static junit.framework.TestCase.assertTrue;

public class CheckPossibility {

    public boolean checkPossibility(int[] nums) {
        int count=0;
        for (int i=0; i<nums.length-1; i++){
            if (nums[i]>nums[i+1]){//change i
                if (count==0){
                    if (i==0 || i== nums.length-2){
                        //ok
                    }else{
                        if (nums[i]<=nums[i+2] || nums[i-1]<=nums[i+1]){

                        }else{
                            return false;
                        }
                    }
                }else{
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    public static void main(String[] args){
        CheckPossibility checkPossibility = new CheckPossibility();
        boolean ret;

        ret= checkPossibility.checkPossibility(new int[]{4,2,3});
        assertTrue(ret);

        ret= checkPossibility.checkPossibility(new int[]{4,2,1});
        assertTrue(!ret);

        ret= checkPossibility.checkPossibility(new int[]{3,4,2,3});
        assertTrue(!ret);

        ret= checkPossibility.checkPossibility(new int[]{5,7,1,8});
        assertTrue(ret);

        ret= checkPossibility.checkPossibility(new int[]{-1,4,2,3});
        assertTrue(ret);

        ret= checkPossibility.checkPossibility(new int[]{1,4,1,2});
        assertTrue(ret);
    }
}
