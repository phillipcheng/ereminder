package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class MaxScore {
    private static Logger logger =  LogManager.getLogger(MaxScore.class);

    Map<String, Integer> maxCache = new HashMap<>();//from+_+to+_+k

    int maxScore(int[] cardPoints, int from, int to, int k){//poor performance
        if (k==1){
            return Math.max(cardPoints[from], cardPoints[to]);
        }else{
            String key = String.format("%d_%d_%d", from, to, k);
            if (maxCache.containsKey(key)){
                return maxCache.get(key);
            }else {
                int ret = Math.max(cardPoints[from] + maxScore(cardPoints, from + 1, to, k - 1),
                        cardPoints[to] + maxScore(cardPoints, from, to - 1, k - 1));
                maxCache.put(key, ret);
                return ret;
            }
        }
    }

    public int maxScore(int[] cardPoints, int k) {
        int l = cardPoints.length-k;
        int total=Arrays.stream(cardPoints).sum();
        int sum = 0;
        for (int j=0;j<l;j++){
            sum+=cardPoints[j];
        }
        int min = sum;
        for (int i=l;i<cardPoints.length;i++){
            sum -= cardPoints[i-l];
            sum += cardPoints[i];
            min = Math.min(min, sum);
        }
        return total-min;
    }

    public static void main(String[] args){
        int ret;
        MaxScore maxScore = new MaxScore();

        ret = maxScore.maxScore(new int[]{1,2,3,4,5,6,1}, 3);
        logger.info(ret);
        assertTrue(ret==12);

        ret = maxScore.maxScore(new int[]{2,2,2}, 2);
        logger.info(ret);
        assertTrue(ret==4);

        ret = maxScore.maxScore(new int[]{9,7,7,9,7,7,9}, 7);
        logger.info(ret);
        assertTrue(ret==55);


        ret = maxScore.maxScore(new int[]{1,1000,1}, 1);
        logger.info(ret);
        assertTrue(ret==1);

        ret = maxScore.maxScore(new int[]{1,79,80,1,1,1,200,1}, 3);
        logger.info(ret);
        assertTrue(ret==202);

    }
}
