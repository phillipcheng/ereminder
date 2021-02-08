package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class PivotIndex {
    private static Logger logger =  LogManager.getLogger(PivotIndex.class);

    public int pivotIndex(int[] nums) {
        long sum = 0;
        for (int num: nums){
            sum+=num;
        }
        long leftSum=0;
        for (int i=0; i<nums.length; i++){
            if (2*leftSum + nums[i]== sum){
                return i;
            }else {
                leftSum += nums[i];
            }
        }
        return -1;
    }

    public static void main(String[] args){
        PivotIndex pivotIndex = new PivotIndex();
        int ret;

        ret = pivotIndex.pivotIndex(new int[]{1,7,3,6,5,6});
        logger.info(ret);
        assertTrue(ret==3);

        ret = pivotIndex.pivotIndex(new int[]{1,2,3});
        logger.info(ret);
        assertTrue(ret==-1);

        ret = pivotIndex.pivotIndex(new int[]{1,7,3,0,0,0,5,6});
        logger.info(ret);
        assertTrue(ret==3);

        ret = pivotIndex.pivotIndex(new int[]{});
        logger.info(ret);
        assertTrue(ret==-1);

        ret = pivotIndex.pivotIndex(new int[]{1});
        logger.info(ret);
        assertTrue(ret==0);

        ret = pivotIndex.pivotIndex(new int[]{-1,-1,-1,-1,-1,0});
        logger.info(ret);
        assertTrue(ret==2);
    }
}
