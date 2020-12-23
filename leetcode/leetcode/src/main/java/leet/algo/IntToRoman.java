package leet.algo;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class IntToRoman {
    private static Logger logger =  LogManager.getLogger(IntToRoman.class);

    /*
    I can be placed before V (5) and X (10) to make 4 and 9. 
    X can be placed before L (50) and C (100) to make 40 and 90. 
    C can be placed before D (500) and M (1000) to make 400 and 900.
     */
    String[] symbals = new String[]{"I", "X", "C", "M"};
    int[] thresholds = new int[]{1, 10, 100, 1000};

    String[] halfs = new String[]{"V", "L", "D"};
    int[] halfv = new int[]{5, 50, 500};

    String getRepeatSymbal(String symbal, int value){
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<value; i++){
            sb.append(symbal);
        }
        return sb.toString();
    }

    String getHalfSymbal(String symbal){
        if ("I".equals(symbal)){
            return "V";
        }
        if ("X".equals(symbal)){
            return "L";
        }
        if ("C".equals(symbal)){
            return "D";
        }
        return "unknown";
    }

    String getNextSymbal(String symbal){
        if ("I".equals(symbal)){
            return "X";
        }
        if ("X".equals(symbal)){
            return "C";
        }
        if ("C".equals(symbal)){
            return "M";
        }
        return "unknown";
    }

    String getString(String symbal, int value){
        if (value<4){
            return getRepeatSymbal(symbal, value);
        }
        if (value==4){
            return symbal + getHalfSymbal(symbal);
        }
        if (value==5){
            return getHalfSymbal(symbal);
        }
        if (value<9){
            return getHalfSymbal(symbal)+getRepeatSymbal(symbal, value-5);
        }
        if (value==9){
            return symbal + getNextSymbal(symbal);
        }
        return "unknown";
    }

    public String intToRoman(int num) {
        int[] digits = new int[thresholds.length];
        for (int i=thresholds.length-1; i>=0; i--){
            int threshold = thresholds[i];
            int rest = num % threshold;
            int digit = num / threshold;
            digits[i]=digit;
            num = rest;
            if (num == 0) break;
        }
        logger.info(Arrays.toString(digits));
        StringBuffer sb = new StringBuffer();
        for (int i=digits.length-1; i>=0; i--){//large to small
            String symbol = symbals[i];
            int val = digits[i];
            sb.append(getString(symbol, val));
        }
        return sb.toString();
    }

    public static void main(String[] args){
        IntToRoman btz = new IntToRoman();
        String s;
        s = btz.intToRoman(3);
        logger.info(s);
        assertTrue("III".equals(s));

        s = btz.intToRoman(4);
        logger.info(s);
        assertTrue("IV".equals(s));

        s = btz.intToRoman(9);
        logger.info(s);
        assertTrue("IX".equals(s));

        s = btz.intToRoman(58);
        logger.info(s);
        assertTrue("LVIII".equals(s));

        s = btz.intToRoman(1994);
        logger.info(s);
        assertTrue("MCMXCIV".equals(s));

        s = btz.intToRoman(10);
        logger.info(s);
        assertTrue("X".equals(s));

        s = btz.intToRoman(40);
        logger.info(s);
        assertTrue("XL".equals(s));

    }


}
