package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class AddToArrayForm {
    private static Logger logger =  LogManager.getLogger(AddToArrayForm.class);

    public List<Integer> addToArrayForm(int[] A, int K) {
        int r = K;
        List<Integer> digits = new ArrayList<>();
        while (r>=10){
            int d = r%10;
            digits.add(d);
            r = r/10;
        }
        digits.add(r);
        //logger.info(digits);
        int carry=0;
        int max = Math.max(A.length, digits.size());
        List<Integer> ret = new ArrayList<>();
        for (int i=0; i<max; i++){
            int a = 0;
            if (A.length-i-1>=0) {
                a = A[A.length - i - 1];
            }
            int b = 0;
            if (i<digits.size()) {
                b = digits.get(i);
            }
            int s = a+b+carry;
            int d = s>=10?s-10:s;
            carry = s>=10?1:0;
            ret.add(0, d);
        }
        if (carry==1){
            ret.add(0, carry);
        }
        return ret;
    }

    public static void main(String[] args){
        AddToArrayForm addToArrayForm = new AddToArrayForm();
        List<Integer> ret;

        ret = addToArrayForm.addToArrayForm(new int[]{1,2,0,0}, 34);
        logger.info(ret);
        assertTrue(ret.toString().equals("[1, 2, 3, 4]"));

        ret = addToArrayForm.addToArrayForm(new int[]{2,7,4}, 181);
        logger.info(ret);
        assertTrue(ret.toString().equals("[4, 5, 5]"));

        ret = addToArrayForm.addToArrayForm(new int[]{2,1,5}, 806);
        logger.info(ret);
        assertTrue(ret.toString().equals("[1, 0, 2, 1]"));

        ret = addToArrayForm.addToArrayForm(new int[]{9,9,9,9,9,9,9,9,9,9}, 1);
        logger.info(ret);
        assertTrue(ret.toString().equals("[1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]"));

    }
}
