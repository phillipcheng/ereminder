package leet.algo;

import java.util.Comparator;
import java.util.PriorityQueue;

import leet.algo.test.TestInsertIntervals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MedianFinder {
	private static Logger logger =  LogManager.getLogger(MedianFinder.class);
	public class DecreaseComparator implements Comparator<Integer>{
		@Override
		public int compare(Integer o1, Integer o2) {
			if (o1>o2){
				return -1;
			}else if (o1<o2){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>();//every time get the min
	DecreaseComparator dc = new DecreaseComparator();
	PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>(10, dc);
	
	// Adds a number into the data structure.
    public void addNum(int num) {
    	assert (Math.abs(minHeap.size()-maxHeap.size())<=1);
        int maxSize = maxHeap.size();
        int minSize = minHeap.size();
        if (maxSize==0 && minSize==0){
        	maxHeap.add(num);
        }else if (maxSize==0){
        	int min = minHeap.peek();
        	if (num<=min){
        		maxHeap.add(num);
        	}else{//num>min
        		minHeap.remove();
        		maxHeap.add(min);
        		minHeap.add(num);
        	}
        }else if (minSize==0){
        	int max = maxHeap.peek();
        	if (num>=max){
        		minHeap.add(num);
        	}else{
        		maxHeap.remove();
        		minHeap.add(max);
        		maxHeap.add(num);
        	}
        }else{
        	int min=minHeap.peek();
        	int mins = minHeap.size();
        	int max=maxHeap.peek();
        	int maxs = maxHeap.size();
        	if (num<=min){
        		if (maxs>mins){
        			int bigger = Math.max(max, num);
        			int smaller = Math.min(max, num);
        			minHeap.add(bigger);
        			maxHeap.remove();
        			maxHeap.add(smaller);
        		}else{
        			maxHeap.add(num);
        		}
        	}else{
        		if (mins>maxs){
        			int bigger = Math.max(min, num);
        			int smaller = Math.min(min, num);
        			maxHeap.add(smaller);
        			minHeap.remove();
        			minHeap.add(bigger);
        		}else{
        			minHeap.add(num);
        		}
        	}
        }
        /*
        logger.info(Math.abs(minHeap.size()-maxHeap.size())<=1);
        if (minHeap.size()>0 && maxHeap.size()>0){
        	logger.info(String.format("minHeap>=maxHeap:%b",minHeap.peek()>=maxHeap.peek()));
        }
        logger.info(String.format("maxHeap:%s", maxHeap));
        logger.info(String.format("minHeap:%s", minHeap));*/
    }

    // Returns the median of current data stream
    public double findMedian() {
    	int maxSize = maxHeap.size();
        int minSize = minHeap.size();
        if (maxSize==0 && minSize==0){
        	return 0f;//error
        }else if (maxSize==0){
        	return minHeap.peek();
        }else if (minSize==0){
        	return maxHeap.peek();
        }else{
        	int min=minHeap.peek();
        	int mins = minHeap.size();
        	int max=maxHeap.peek();
        	int maxs = maxHeap.size();
        	if (mins==maxs){
        		return 0.5*(min+max);
        	}else if (maxs>mins){
        		return max;
        	}else{
        		return min;
        	}
        }
    }
}
