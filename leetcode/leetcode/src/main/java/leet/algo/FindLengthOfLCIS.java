package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class FindLengthOfLCIS {
    private static Logger logger =  LogManager.getLogger(FindLengthOfLCIS.class);

    public int findLengthOfLCIS(int[] nums) {
        int max=0;
        int count = 0;
        for (int i=0; i<nums.length; i++){
            if (i==0){
                count=1;
            }else{
                if (nums[i]>nums[i-1]){
                    count++;
                }else{
                    max = Math.max(max, count);
                    count=1;
                }
            }
        }
        max = Math.max(max, count);//for last
        return max;
    }

    public static void main(String[] args){
        FindLengthOfLCIS findLengthOfLCIS = new FindLengthOfLCIS();
        int ret;

        ret = findLengthOfLCIS.findLengthOfLCIS(new int[]{1,3,5,4,7});
        logger.info(ret);
        assertTrue(ret==3);

        ret = findLengthOfLCIS.findLengthOfLCIS(new int[]{2,2,2,2,2});
        logger.info(ret);
        assertTrue(ret==1);

        ret = findLengthOfLCIS.findLengthOfLCIS(new int[]{2});
        logger.info(ret);
        assertTrue(ret==1);

        ret = findLengthOfLCIS.findLengthOfLCIS(new int[]{2,1,1,1,1,2,3});
        logger.info(ret);
        assertTrue(ret==3);

    }
}
