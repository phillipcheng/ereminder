package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class Rotate {
    private static Logger logger =  LogManager.getLogger(Rotate.class);

    int gcd(int a, int b){
        while (b!=0){
            int c = a%b;
            a = b;
            b = c;
        }
        return a;
    }

    int getPreIdx(int cur, int k, int n){
        int i = cur-k;
        if (i<0){
            return i+n;
        }else{
            return i;
        }
    }

    public void rotate(int[] nums, int k) {
        int n = nums.length;
        if (n==0) return;
        if (n<k) k = k%n;
        if (k==0) return;

        int gcd = gcd(n, k);
        int p;
        for (int i=0; i<gcd; i++){
            int cur = n-k+i;
            p = nums[cur];
            while (cur!=i){
                int pre= getPreIdx(cur, k, n);
                nums[cur]=nums[pre];
                cur = pre;
            }
            nums[cur]=p;
        }
    }

    public static void main(String[] args){
        Rotate rotate = new Rotate();
        int[] nums;
        int k;
        int[] ret;

        nums = new int[]{1,2,3,4,5,6,7};
        k = 3;
        rotate.rotate(nums, k);
        ret = new int[]{5,6,7,1,2,3,4};
        logger.info(Arrays.toString(nums));
        assertTrue(Arrays.toString(ret).equals(Arrays.toString(nums)));

        nums = new int[]{-1,-100,3,99};
        k = 2;
        rotate.rotate(nums, k);
        ret = new int[]{3,99,-1,-100};
        logger.info(Arrays.toString(nums));
        assertTrue(Arrays.toString(ret).equals(Arrays.toString(nums)));

        nums = new int[]{1};
        k = 0;
        rotate.rotate(nums, k);
        ret = new int[]{1};
        logger.info(Arrays.toString(nums));
        assertTrue(Arrays.toString(ret).equals(Arrays.toString(nums)));

        nums = new int[]{-1};
        k = 2;
        rotate.rotate(nums, k);
        ret = new int[]{-1};
        logger.info(Arrays.toString(nums));
        assertTrue(Arrays.toString(ret).equals(Arrays.toString(nums)));
    }
}
