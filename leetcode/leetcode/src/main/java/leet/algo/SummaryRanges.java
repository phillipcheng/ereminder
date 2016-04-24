package leet.algo;

import java.util.ArrayList;
import java.util.List;

import leet.algo.test.TestSummaryRanges;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SummaryRanges {
	private static Logger logger =  LogManager.getLogger(SummaryRanges.class);
	//Given a sorted integer array without duplicates, return the summary of its ranges.
	//For example, given [0,1,2,4,5,7], return ["0->2","4->5","7"].
	private String genString(int start, int num){
		if (num==0){
			String s = start + "";
			return s;
		}else{
			String s = start + "->" + (start + num);
			return s;
		}
	}
	public List<String> summaryRanges(int[] nums) {
		List<String> ol = new ArrayList<String>();
		int num=0;//continuous number
		int start = 0;
		int pre = 0;
        for (int i=0; i<nums.length; i++){
        	int cur = nums[i];
        	if (i==0){
        		start = cur;
        		num=0;
        	}else{
        		//in Range
        		if (cur==pre+1){
        			num++;
        		}else{
        			//generate string
        			ol.add(genString(start, num));
        			//
        			start = cur;
        			num = 0;
        		}
        	}
        	if (i==nums.length-1){
    			ol.add(genString(start, num));
    		}
        	//logger.info(String.format("cur:%d, pre:%d, start:%d, num:%d", cur, pre, start, num));
        	pre = cur;
        }
        return ol;
    }
}
