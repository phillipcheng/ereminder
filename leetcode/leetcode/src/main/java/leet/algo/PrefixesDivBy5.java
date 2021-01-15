package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class PrefixesDivBy5 {
    private static Logger logger =  LogManager.getLogger(PrefixesDivBy5.class);

    public List<Boolean> prefixesDivBy5(int[] A) {
        Boolean[] ret = new Boolean[A.length];
        long num = 0;
        for (int i=0; i<A.length; i++){
            int v = A[i];
            num = num * 2 + v;
            if (num%5== 0){
                ret[i]=true;
            }else{
                ret[i]=false;
            }
            num = num%5;
        }
        return Arrays.asList(ret);
    }

    public static void main(String[] args){
        PrefixesDivBy5 prefixesDivBy5 = new PrefixesDivBy5();
        List<Boolean> ret;

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{0,1,1});
        assertTrue(ret.toString().equals("[true, false, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{1,1,1});
        assertTrue(ret.toString().equals("[false, false, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{0,1,1,1,1,1});
        assertTrue(ret.toString().equals("[true, false, false, false, true, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{1,1,1,0,1});
        assertTrue(ret.toString().equals("[false, false, false, false, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{1,1,0,0,0,1,0,0,1});
        assertTrue(ret.toString().equals("[false, false, false, false, false, false, false, false, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{1,0,0,1,0,1,0,0,1,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,0,1,0,0,0,0,1,1,0,1,0,0,0,1});
        assertTrue(ret.toString().equals("[false, false, false, false, false, false, false, false, false, " +
                "false, false, false, false, false, false, false, false, false, false, false, false, false, " +
                "false, false, false, false, false, false, false, false, false, true, false, false, " +
                "true, true, true, true, false]"));

        ret = prefixesDivBy5.prefixesDivBy5(new int[]{1,0,1,1,1,1,0,0,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1,0,0,0,0,1,1,1,0,0,0,0,0,1,0,0,0,1,0,0,1,1,1,1,1,1,0,1,1,0,1,0,0,0,0,0,0,1,0,1,1,1,0,0,1,0});
        assertTrue(ret.toString().equals("[false, false, true, false, false, false, false, false, false, " +
                "false, true, true, true, true, true, true, false, false, false, false, false, false, false, " +
                "false, false, false, false, false, false, false, false, false, false, false, false, false, " +
                "false, false, false, false, false, false, false, true, false, false, false, true, false, " +
                "false, true, false, false, true, true, true, true, true, true, true, false, false, true, " +
                "false, false, false, false, true, true]"));
    }
}
