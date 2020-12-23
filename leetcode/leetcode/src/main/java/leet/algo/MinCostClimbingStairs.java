package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class MinCostClimbingStairs {

    private static Logger logger =  LogManager.getLogger(MinCostClimbingStairs.class);

    public int minCostClimbingStairs(int[] cost) {
        int i = 0;
        int minusTwo = 0;
        int minusOne = cost[i];
        int minCost =  minusOne;//the min cost of index i
        for (i=1; i<cost.length; i++){
            minCost = Math.min(minusOne, minusTwo) + cost[i];
            minusTwo = minusOne;
            minusOne = minCost;
            //logger.info(String.format("i:%d, minusTwo:%d, minusOne:%d, minCost:%d", i, minusTwo, minusOne, minCost));
        }
        return Math.min(minusTwo, minusOne);
    }

    public static void main(String[] args){
        int[] cost = null;
        int ret = 0;
        MinCostClimbingStairs minCostClimbingStairs = new MinCostClimbingStairs();

        cost = new int[] {10, 15, 20};
        ret = minCostClimbingStairs.minCostClimbingStairs(cost);
        logger.info(ret);
        assertTrue(ret == 15);

        cost = new int[] {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
        ret = minCostClimbingStairs.minCostClimbingStairs(cost);
        logger.info(ret);
        assertTrue(ret == 6);

    }
}
