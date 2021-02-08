package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static junit.framework.TestCase.assertTrue;

public class EqualSubstring {
    private static Logger logger =  LogManager.getLogger(EqualSubstring.class);

    public int equalSubstring(String s, String t, int maxCost) {
        int left=0;
        int right=0;
        int tc=0;
        while(right<t.length()){
            int cost = Math.abs(s.charAt(right)-t.charAt(right));
            if ((cost+tc)>maxCost){
                tc=tc+cost-Math.abs(s.charAt(left)-t.charAt(left));
                left++;
            }else{
                tc+=cost;
            }
            right++;
        }
        return right-left;
    }

    public static void main(String[] args){
        EqualSubstring equalSubstring = new EqualSubstring();
        int ret;

        ret = equalSubstring.equalSubstring("abcd", "bcdf", 3);
        logger.info(ret);
        assertTrue(ret==3);

        ret = equalSubstring.equalSubstring("abcd", "cdef", 3);
        logger.info(ret);
        assertTrue(ret==1);

        ret = equalSubstring.equalSubstring("abcd", "acde", 0);
        logger.info(ret);
        assertTrue(ret==1);

        ret = equalSubstring.equalSubstring("pxezla", "loewbi", 25);
        logger.info(ret);
        assertTrue(ret==4);
    }
}
