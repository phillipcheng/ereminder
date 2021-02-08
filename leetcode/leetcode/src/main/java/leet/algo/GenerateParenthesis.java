package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class GenerateParenthesis {
    private static Logger logger =  LogManager.getLogger(GenerateParenthesis.class);

    public List<String> generateParenthesis(int n) {
        List<String> ret = new ArrayList<>();
        if (n==1){
            ret.add("()");
            return ret;
        }else{
            List<String> ret1 = generateParenthesis(n-1);
            Set<String> set = new HashSet();
            for (String r1: ret1){
                for (int i=0; i<r1.length(); i++){
                    String s = "(" + r1.substring(0, i) + ")" + r1.substring(i);
                    set.add(s);
                }
            }
            ret.addAll(set);
        }
        return ret;
    }

    public static void main(String[] args){
        GenerateParenthesis generateParenthesis = new GenerateParenthesis();
        List<String> ret;

        ret = generateParenthesis.generateParenthesis(1);
        logger.info(ret);
        logger.info(ret.size());

        ret = generateParenthesis.generateParenthesis(2);
        logger.info(ret);
        logger.info(ret.size());

        ret = generateParenthesis.generateParenthesis(3);
        logger.info(ret);
        logger.info(ret.size());

        ret = generateParenthesis.generateParenthesis(4);
        logger.info(ret);
        logger.info(ret.size());

        ret = generateParenthesis.generateParenthesis(10);
        logger.info(ret);
        logger.info(ret.size());
    }
}
