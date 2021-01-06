package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Comparator;

import static junit.framework.TestCase.assertTrue;

public class EraseOverlapIntervals {

    private static Logger logger =  LogManager.getLogger(EraseOverlapIntervals.class);

    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals.length==0) return 0;
        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[1]-o2[1];
            }
        });

        int count=1;
        int end = intervals[0][1];
        for (int i=0; i<intervals.length; i++){
            if (intervals[i][0]<end){
                continue;
            }
            end = intervals[i][1];
            count++;
        }
        return intervals.length-count;
    }

    public static void main(String[] args){
        EraseOverlapIntervals e = new EraseOverlapIntervals();
        int[][] intervals;
        int ret;

        intervals = new int[][]{{1,2},{2,3},{3,4},{1,3}};
        ret = e.eraseOverlapIntervals(intervals);
        logger.info(ret);
        assertTrue(ret==1);

        intervals = new int[][]{{1,2},{1,2},{1,2}};
        ret = e.eraseOverlapIntervals(intervals);
        logger.info(ret);
        assertTrue(ret==2);


        intervals = new int[][]{{1,2},{2,3}};
        ret = e.eraseOverlapIntervals(intervals);
        logger.info(ret);
        assertTrue(ret==0);


        intervals = new int[][]{{0,2},{1,3},{2,4},{3,5},{4,6}};
        ret = e.eraseOverlapIntervals(intervals);
        logger.info(ret);
        assertTrue(ret==2);

    }
}
