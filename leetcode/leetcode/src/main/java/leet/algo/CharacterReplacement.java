package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CharacterReplacement {
    private static Logger logger =  LogManager.getLogger(CharacterReplacement.class);


    public int characterReplacement(String s, int k) {
        int left=0;
        int right=0;
        int[] count = new int[26];//count of each ch within the window
        int maxCover=0;//lss: longest substring
        for (;right<s.length();++right){//move right
            int idx = s.charAt(right)-'A';
            count[idx]++;
            maxCover = Math.max(maxCover, count[idx]);
            if (maxCover+k<right-left+1) {//move left
                count[s.charAt(left)-'A']--;
                left++;
            }
        }
        return right-left;
    }

    public static void main(String[] args){
        CharacterReplacement characterReplacement = new CharacterReplacement();
        int ret;

        ret = characterReplacement.characterReplacement("ABAB", 2);
        logger.info(ret);

        ret = characterReplacement.characterReplacement("AABABBA", 1);
        logger.info(ret);

        ret = characterReplacement.characterReplacement("AAAA", 2);
        logger.info(ret);


    }
}
