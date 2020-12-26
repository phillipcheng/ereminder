package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;

public class FindContentChildren {
    private static Logger logger =  LogManager.getLogger(FindContentChildren.class);

    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int ret=0;
        int i=0, j=0;
        while (i<g.length && j<s.length){
            if (g[i]<=s[j]){
                i++;
                j++;
                ret++;
            }else{
                j++;
            }
        }

        return ret;
    }

    public static void main(String[] args){
        FindContentChildren fcc = new FindContentChildren();
        int[] greedy;
        int[] satisfy;
        int ret;

        greedy = new int[]{1,2,3};
        satisfy = new int[]{1,1};
        ret = fcc.findContentChildren(greedy, satisfy);
        logger.info(ret);
        assertTrue(ret==1);


        greedy = new int[]{1,2};
        satisfy = new int[]{1,2,3};
        ret = fcc.findContentChildren(greedy, satisfy);
        logger.info(ret);
        assertTrue(ret==2);

        greedy = new int[]{10,9,8,7};
        satisfy = new int[]{5,6,7,8};
        ret = fcc.findContentChildren(greedy, satisfy);
        logger.info(ret);
        assertTrue(ret==2);
    }
}
