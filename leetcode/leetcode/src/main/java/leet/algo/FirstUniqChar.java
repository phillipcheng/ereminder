package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

public class FirstUniqChar {

    private static Logger logger =  LogManager.getLogger(FirstUniqChar.class);

    public int firstUniqChar(String s) {
        Map<Character, Integer> map = new HashMap<>();
        boolean[] flags = new boolean[s.length()];//initial to false
        for (int i=0; i<s.length(); i++){
            Character ch = s.charAt(i);
            if (map.containsKey(ch)){
                Integer idx = map.get(ch);
                flags[idx]=true;
                flags[i]=true;
            }else{
                map.put(ch, i);
            }
            //logger.info(Arrays.toString(flags));
        }

        for (int j=0; j<flags.length; j++){
            if (!flags[j]) return j;
        }
        return -1;
    }

    public static void main(String[] args){
        FirstUniqChar test = new FirstUniqChar();
        int ret = 0;
        String input;


        input = "leetcode";
        ret = test.firstUniqChar(input);
        logger.info("ret:"+ ret);
        assertTrue(ret==0);

        input = "loveleetcode";
        ret = test.firstUniqChar(input);
        logger.info("ret:"+ ret);
        assertTrue(ret==2);


        input = "lloovvee";
        ret = test.firstUniqChar(input);
        logger.info("ret:"+ ret);
        assertTrue(ret==-1);
    }
}
