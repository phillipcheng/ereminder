package leet.algo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given n non-negative integers representing the histogram's bar height where the width of each bar is 1, find the area of largest rectangle in the histogram.
public class LargestRectangleInHistogram {
	private static Logger logger =  LogManager.getLogger(LargestRectangleInHistogram.class);
	class Range{//[start, end]
		int start;
		int end;
		int value;
		public Range(int start, int end, int value){
			this.start = start;
			this.end = end;
			this.value = value;
		}
		public String toString(){
			return String.format("%d-%d:%d", start, end, value);
		}
	}
	public int naivelargestRectangleArea(int[] heights) {//o(n^2)
		if (heights.length==0) return 0;
		if (heights.length==1) return heights[0];
    	List<Range> rl = new ArrayList<Range>();
    	rl.add(new Range(1, heights[0], 1));
		int max = heights[0];
        //logger.info(rr);
		for (int i=1; i<heights.length; i++){
        	boolean outGrow=false; //means previous legs outgrow newest leg
        	int maxj=0;
        	for (int j=0; j<rl.size() && !outGrow; j++){
        		Range pR = rl.get(j);
        		if (pR.end>heights[i]){
        			pR.end=heights[i];
        			outGrow=true;
        			maxj=j;
        		}
        		pR.value++;
        		max = Math.max(max, pR.end*pR.value);
        	}
        	if (!outGrow){//add last seg
        		Range last = new Range(rl.get(rl.size()-1).end+1, heights[i], 1);
        		max = Math.max(max, heights[i]);
        		rl.add(last);
        	}else{//remove the rest in rl
        		rl = rl.subList(0, maxj+1);
        	}
        	//logger.info(rr);
        }
		return max;
    }
	class Bar{
		int start;
		int end;
		int value;
		public Bar(int start, int end, int value){
			this.start = start;
			this.end = end;
			this.value = value;
		}
		public String toString(){
			return String.format("%d-%d:%d", start, end, value);
		}
	}
	public int largestRectangleArea(int[] heights){//o(n)
		if (heights.length==0) return 0;
		if (heights.length==1) return heights[0];
		int max=heights[0];
		Deque<Bar> stack = new ArrayDeque<Bar>();
		stack.push(new Bar(0,0,heights[0]));
		int curIdx=1;//the curIdx working with the stack (not in the stack)
		int curStart = -1;
		while(!(stack.isEmpty() && curIdx==heights.length)){
			int curValue=0;
			if (curIdx<heights.length){
				curValue = heights[curIdx];
			}
			if (!stack.isEmpty() && (stack.peek().value>curValue || (stack.peek().value==0 && curValue==0))){
				max = Math.max(max, stack.peek().value * (curIdx -stack.peek().start));
				curStart = stack.peek().start;
				stack.pop();
			}else{
				if (curStart!=-1){
					stack.push(new Bar(curStart, curIdx, curValue));
				}else{
					stack.push(new Bar(curIdx, curIdx, curValue));
				}
				curStart = -1;
				curIdx++;
			}
			//logger.info(String.format("stack:%s, max:%d", stack, max));
		}
		return max;
	}
	
	
}
