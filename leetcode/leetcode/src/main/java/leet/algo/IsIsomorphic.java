package leet.algo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class IsIsomorphic {
    public boolean isIsomorphic(String s, String t) {
        Map<Character, Character> map = new HashMap<>();
        Set<Character> tgt = new HashSet<>();
        for (int i=0; i<s.length(); i++){
            char sc = s.charAt(i);
            char tc = t.charAt(i);
            if (map.containsKey(sc)){
                char c = map.get(sc);
                if (tc != c){
                    return false;
                }
            }else{
                if (tgt.contains(tc)){//
                    return false;
                }else {
                    map.put(sc, tc);
                    tgt.add(tc);
                }
            }
        }
        return true;
    }

    public static void main(String[] args){
        IsIsomorphic ii = new IsIsomorphic();
        String s, t;
        boolean ret;

        s = "ab";
        t = "aa";
        ret = ii.isIsomorphic(s, t);
        assertTrue(ret==false);

        s = "egg";
        t = "add";
        ret = ii.isIsomorphic(s, t);
        assertTrue(ret==true);

        s = "foo";
        t = "bar";
        ret = ii.isIsomorphic(s, t);
        assertTrue(ret==false);

        s = "paper";
        t = "title";
        ret = ii.isIsomorphic(s, t);
        assertTrue(ret==true);
    }

}
