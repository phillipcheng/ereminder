package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static junit.framework.TestCase.assertTrue;

public class MaxSlidingWindow {
    private static Logger logger =  LogManager.getLogger(MaxSlidingWindow.class);
    public int[] maxSlidingWindow(int[] nums, int k) {
        int[] ret = new int[Math.max(nums.length-k+1, 1)];
        PriorityQueue<int[]> pq = new PriorityQueue<int[]>(new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o2[0]-o1[0];
            }
        });
        for (int i=0; i<nums.length; i++){
            int left = i-k+1;
            pq.add(new int[]{nums[i], i});
            if (left>=0) {
                while (pq.peek()[1]<left){
                    pq.poll();
                }
                int[] max = pq.peek();
                ret[left]= max[0];
            }else{
                ret[0] = pq.peek()[0];
            }
//            logger.info("ret array:" + Arrays.toString(ret));
//            logger.info("pq:" + pq);
        }
        return ret;
    }

    public static void main(String[] args){
        MaxSlidingWindow maxSlidingWindow = new MaxSlidingWindow();
        int[] nums;
        int k;
        int[] ret;

        nums = new int[]{1,3,-1,-3,5,3,6,7};
        k = 3;
        ret = maxSlidingWindow.maxSlidingWindow(nums, k);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.equals(new int[]{3, 3, 5, 5, 6, 7}, ret));

        nums = new int[]{1};
        k = 1;
        ret = maxSlidingWindow.maxSlidingWindow(nums, k);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.equals(new int[]{1}, ret));

        nums = new int[]{1, -1};
        k = 1;
        ret = maxSlidingWindow.maxSlidingWindow(nums, k);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.equals(new int[]{1, -1}, ret));

        nums = new int[]{9, 11};
        k = 2;
        ret = maxSlidingWindow.maxSlidingWindow(nums, k);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.equals(new int[]{11}, ret));

        nums = new int[]{4, -2};
        k = 2;
        ret = maxSlidingWindow.maxSlidingWindow(nums, k);
        logger.info(Arrays.toString(ret));
        assertTrue(Arrays.equals(new int[]{4}, ret));
    }
}
