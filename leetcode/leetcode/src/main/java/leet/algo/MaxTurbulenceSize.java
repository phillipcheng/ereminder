package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.junit.Assert.assertTrue;

public class MaxTurbulenceSize {
    private static Logger logger =  LogManager.getLogger(MaxTurbulenceSize.class);

    int getDir(int[] arr, int i){
        int dir=0;
        if (arr[i]>arr[i+1]){
            dir=1;//down
        }else if (arr[i]<arr[i+1]){
            dir=2;//up
        }
        return dir;
    }
    public int maxTurbulenceSize(int[] arr) {
        if (arr.length==1) return 1;
        int i=0;
        int preDir = getDir(arr, i);
        int maxCount;
        if (preDir==0){
            maxCount=1;
        }else{
            maxCount=2;
        }
        int curCount=maxCount;
        i++;
        while (i+1<arr.length){
            int curDir = getDir(arr, i);
            if (curDir == 0){
                maxCount = Math.max(maxCount, curCount);
                curCount=1;
            }else if (curDir == preDir){
                maxCount = Math.max(maxCount, curCount);
                curCount=2;
            }else{
                curCount++;
                maxCount = Math.max(maxCount, curCount);
            }
            preDir = curDir;
            i++;
        }
        return maxCount;
    }

    public static void main(String args[]){
        MaxTurbulenceSize maxTurbulenceSize = new MaxTurbulenceSize();
        int ret;

        ret = maxTurbulenceSize.maxTurbulenceSize(new int[]{9,4,2,10,7,8,8,1,9});
        logger.info(ret);
        assertTrue(ret==5);

        ret = maxTurbulenceSize.maxTurbulenceSize(new int[]{4,8,12,16});
        logger.info(ret);
        assertTrue(ret==2);

        ret = maxTurbulenceSize.maxTurbulenceSize(new int[]{100});
        logger.info(ret);
        assertTrue(ret==1);

        ret = maxTurbulenceSize.maxTurbulenceSize(new int[]{0,8,45,88,48,68,28,55,17,24});
        logger.info(ret);
        assertTrue(ret==8);


    }
}
