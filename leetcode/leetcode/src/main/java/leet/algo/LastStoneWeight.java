package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.PriorityQueue;

import static junit.framework.TestCase.assertTrue;

public class LastStoneWeight {

    private static Logger logger =  LogManager.getLogger(LastStoneWeight.class);

    public int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return - Integer.compare(o1, o2);
            }
        });
        for (int stone: stones){
            maxHeap.add(stone);
        }
        while (maxHeap.size()>1){
            int a = maxHeap.poll();
            int b = maxHeap.poll();
            if (a>b){
                maxHeap.add(a-b);
            }
        }
        //maxHeap <=1
        if (maxHeap.size()==0)
            return 0;
        else{
            return maxHeap.peek();
        }
    }

    public static void main(String[] args){
        LastStoneWeight lastStoneWeight = new LastStoneWeight();
        int[] stones;
        int ret;

        stones = new int[]{2,7,4,1,8,1};
        ret = lastStoneWeight.lastStoneWeight(stones);
        logger.info(ret);
        assertTrue(ret==1);
    }
}
