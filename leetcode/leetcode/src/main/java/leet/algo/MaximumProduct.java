package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class MaximumProduct {
    private static Logger logger =  LogManager.getLogger(MaximumProduct.class);

    public int maximumProduct(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        return Math.max(nums[0]*nums[1]*nums[n-1], nums[n-3]*nums[n-2]*nums[n-1]);
    }

    public static void main(String[] args){
        MaximumProduct maximumProduct = new MaximumProduct();
        int ret;

        ret = maximumProduct.maximumProduct(new int[]{1,2,3,4});
        logger.info(ret);
        assertTrue(ret == 24);

        ret = maximumProduct.maximumProduct(new int[]{-3,1,2,4});
        logger.info(ret);
        assertTrue(ret == 8);

        ret = maximumProduct.maximumProduct(new int[]{-4, -3, 1, 2});
        logger.info(ret);
        assertTrue(ret == 24);

        ret = maximumProduct.maximumProduct(new int[]{-4, -3, -2, 1});
        logger.info(ret);
        assertTrue(ret == 12);

        ret = maximumProduct.maximumProduct(new int[]{-4, -3, -2, -1});
        logger.info(ret);
        assertTrue(ret == -6);

        ret = maximumProduct.maximumProduct(new int[]{-4, -3, 0, 0, 1});
        logger.info(ret);
        assertTrue(ret == 12);

        ret = maximumProduct.maximumProduct(new int[]{-4, 0, 0, 0, 1});
        logger.info(ret);
        assertTrue(ret == 0);
    }
}
