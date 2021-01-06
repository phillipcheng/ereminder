package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

public class LargeGroupPositions {
    private static Logger logger =  LogManager.getLogger(LargeGroupPositions.class);

    public List<List<Integer>> largeGroupPositions(String s) {
        List<List<Integer>> ret = new ArrayList<>();
        char prev = 0;
        int start = -1;
        int count = 0;
        for (int i=0; i<s.length(); i++){
            char ch = s.charAt(i);
            if (prev == ch){
                count++;
            }else{
                //start a new group, check if we have a large group
                if (count>=3){
                    ArrayList list = new ArrayList();
                    list.add(start);
                    list.add(start+count-1);
                    ret.add(list);
                }
                start=i;
                count=1;
            }
            prev = ch;
        }
        if (count>=3){
            ArrayList list = new ArrayList();
            list.add(start);
            list.add(start+count-1);
            ret.add(list);
        }
        return ret;
    }

    public static void main(String[] args){
        LargeGroupPositions largeGroupPositions = new LargeGroupPositions();
        List<List<Integer>> ret;
        String s;

        s = "abbxxxxzzy";
        ret = largeGroupPositions.largeGroupPositions(s);
        logger.info(ret);
        assertTrue("[[3, 6]]".equals(ret.toString()));


        s = "abc";
        ret = largeGroupPositions.largeGroupPositions(s);
        logger.info(ret);
        assertTrue("[]".equals(ret.toString()));

        s = "abcdddeeeeaabbbcd";
        ret = largeGroupPositions.largeGroupPositions(s);
        logger.info(ret);
        assertTrue("[[3, 5], [6, 9], [12, 14]]".equals(ret.toString()));

        s = "aba";
        ret = largeGroupPositions.largeGroupPositions(s);
        logger.info(ret);
        assertTrue("[]".equals(ret.toString()));

        s = "aaa";
        ret = largeGroupPositions.largeGroupPositions(s);
        logger.info(ret);
        assertTrue("[[0, 2]]".equals(ret.toString()));
    }
}
