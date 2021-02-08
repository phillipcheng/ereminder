package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;

public class FairCandySwap {
    private static Logger logger =  LogManager.getLogger(FairCandySwap.class);

    public int[] fairCandySwap(int[] A, int[] B) {
        int sumA = Arrays.stream(A).sum();
        int sumB = 0;
        Map<Integer, Integer> bMap = new HashMap<>();
        for (int i=0; i<B.length; i++){
            bMap.put(B[i], i);
            sumB+=B[i];
        }
        if (sumA>=sumB){
            int gap = sumA-sumB;
            if (gap%2==0){
                int v = gap/2;
                for (int i=0; i<A.length; i++){
                    if (bMap.containsKey(A[i]-v)){
                        return new int[]{A[i], A[i]-v};
                    }
                }
                return null;
            }else{
                return null;
            }
        }else {
            int gap = sumB-sumA;
            if (gap%2==0){
                int v = gap/2;
                for (int i=0; i<A.length; i++){
                    if (bMap.containsKey(A[i]+v)){
                        return new int[]{A[i], A[i]+v};
                    }
                }
                return null;
            }else{
                return null;
            }
        }
    }

    public static void main(String[] args){
        FairCandySwap fairCandySwap = new FairCandySwap();
        int[] ret;

        ret = fairCandySwap.fairCandySwap(new int[]{1,1}, new int[]{2,2});
        logger.info(Arrays.toString(ret));
        assertTrue("[1, 2]".equals(Arrays.toString(ret)));

        ret = fairCandySwap.fairCandySwap(new int[]{1,2}, new int[]{2,3});
        logger.info(Arrays.toString(ret));
        assertTrue("[1, 2]".equals(Arrays.toString(ret)));

        ret = fairCandySwap.fairCandySwap(new int[]{2}, new int[]{1,3});
        logger.info(Arrays.toString(ret));
        assertTrue("[2, 3]".equals(Arrays.toString(ret)));

        ret = fairCandySwap.fairCandySwap(new int[]{1,2,5}, new int[]{2,4});
        logger.info(Arrays.toString(ret));
        assertTrue("[5, 4]".equals(Arrays.toString(ret)));

    }
}
