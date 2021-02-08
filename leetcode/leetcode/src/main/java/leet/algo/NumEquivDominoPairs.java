package leet.algo;

import algo.util.IOUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

public class NumEquivDominoPairs {
    private static Logger logger =  LogManager.getLogger(NumEquivDominoPairs.class);

    class Item extends Object{
        int[] value;
        public Item(int[] value){
            this.value= value;
        }
        @Override
        public int hashCode(){
            return value[0]+value[1];
        }
        @Override
        public boolean equals(Object o){
            if (!(o instanceof Item)){
                return false;
            }
            Item item=(Item)o;
            if (item.value[0]==value[0] && item.value[1]==value[1] ||
                    item.value[0]==value[1] && item.value[1]==value[0]
            ){
                return true;
            }else{
                return false;
            }
        }
    }

    public int numEquivDominoPairs(int[][] dominoes) {
        Map<Item, Integer> map = new HashMap<>();
        for (int i=0; i<dominoes.length; i++){
            Item item = new Item(dominoes[i]);
            if (map.containsKey(item)){
                int v = map.get(item);
                map.put(item, v+1);
            }else{
                map.put(item, 1);
            }
        }
        int count=0;
        for (int v: map.values()){
            if (v>1){
                count+= v*(v-1)/2;
            }
        }
        return count;
    }

    public static void main(String[] args){
        NumEquivDominoPairs numEquivDominoPairs = new NumEquivDominoPairs();
        int ret;

        ret = numEquivDominoPairs.numEquivDominoPairs(IOUtil.getIntArrayArray("[[1,2],[2,1],[3,4],[5,6]]"));
        logger.info(ret);

    }
}
