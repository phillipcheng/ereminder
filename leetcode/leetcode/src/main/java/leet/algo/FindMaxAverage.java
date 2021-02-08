package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FindMaxAverage {
    private static Logger logger =  LogManager.getLogger(FindMaxAverage.class);

    public double findMaxAverage(int[] nums, int k) {
        //0 -- k-1
        int sum=0;
        for (int j=0; j<k; j++){
            sum+=nums[j];
        }
        double max = sum;
        for (int i=k; i<nums.length; i++){
            sum = sum - nums[i-k] + nums[i];
            max = Math.max(sum, max);
        }
        return max/k;
    }

    public static void main(String[] args){
        FindMaxAverage findMaxAverage = new FindMaxAverage();
        double ret;

        ret = findMaxAverage.findMaxAverage(new int[]{1,12,-5,-6,50,3}, 4);
        logger.info(ret);

        ret = findMaxAverage.findMaxAverage(new int[]{5}, 1);
        logger.info(ret);

        ret = findMaxAverage.findMaxAverage(new int[]{0, 1, 1, 3, 3}, 4);
        logger.info(ret);
    }
}
