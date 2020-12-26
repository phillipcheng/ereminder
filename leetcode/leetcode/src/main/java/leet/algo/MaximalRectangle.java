package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class MaximalRectangle {
    private static Logger logger =  LogManager.getLogger(MaximalRectangle.class);

    public int largestRectangleArea(int[] heights) {
        int prev[] = null;
        int max = 0;
        for (int i=0; i<heights.length; i++){
            int cur[] = new int[i+1];
            cur[0] = heights[i];
            for (int j=1; j<i+1; j++){
                cur[j] = Math.min(prev[j-1], cur[0]);
            }
            prev = cur;
            int val = 0;
            for (int k=0; k< i+1; k++){
                val = Math.max(val, cur[k]*(k+1));
            }
            max = Math.max(max, val);
//            logger.info(Arrays.toString(cur));
//            logger.info(val);
        }
        return max;
    }

    int[] getHeight(int[] height, char[] line){
        int[] ret = new int[line.length];
        if (height==null){
            for (int i=0; i<line.length; i++){
                if (line[i]=='1'){
                    ret[i]=1;
                }
            }
        }else{
            for (int i=0; i<line.length; i++){
                if (line[i]=='1'){
                    ret[i]=height[i]+1;
                }else{
                    ret[i]=0;
                }
            }
        }
        return ret;
    }

    public int maximalRectangle(char[][] matrix) {
        int height[] = null;
        int value = 0;
        for (int i=0; i<matrix.length; i++){
            height = getHeight(height, matrix[i]);
            value = Math.max(value, largestRectangleArea(height));
        }
        return value;
    }

    public static void main(String[] args){
        MaximalRectangle m = new MaximalRectangle();
        int[] in;
        char[][] inChar;
        int ret;
//
//        in = new int[]{2,1,5,6,2,3};
//        ret = m.largestRectangleArea(in);
//        logger.info(ret);
//        assertTrue(ret==10);



        inChar = new char[][]{
                {'1','0','1','0','0'},
                {'1','0','1','1','1'},
                {'1','1','1','1','1'},
                {'1','0','0','1','0'}};

        ret = m.maximalRectangle(inChar);
        logger.info(ret);
        assertTrue(ret==6);
    }
}
