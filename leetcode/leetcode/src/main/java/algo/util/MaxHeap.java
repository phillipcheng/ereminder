package algo.util;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxHeap {
	private static Logger logger =  LogManager.getLogger(MaxHeap.class);
	
	private int[] items;
	private int count; //number of items
	
	public MaxHeap(int[] items){
		this.items = items;
		count = items.length;
	}
	
	public int[] getItems(){
		return items;
	}
	
	public void swap(int a, int b){
		int tmp = items[a];
		items[a]=items[b];
		items[b]=tmp;
	}
	
	public void printBinaryTree(){
		logger.info(Arrays.toString(items));
	}
	
	private int getLeftChildIdx(int idx){
		return (2*idx+1);
	}
	
	//sift down the item at start index, make start is the biggest, items between start up to end-index a heap
	private void siftDown(int start, int end){
		logger.info("before siftdown");
		printBinaryTree();
		int idx = start;
		int cidx = getLeftChildIdx(idx);
		while (cidx<=end) {
			if (cidx+1<=end && items[cidx]<items[cidx+1]){
				cidx = cidx+1;
			}
			if (items[cidx]>items[idx]){
				swap(cidx, idx);
				idx = cidx;
			}else{//already there
				break;
			}
			cidx = getLeftChildIdx(idx);
		}
		logger.info("after siftdown");
		printBinaryTree();
	}
	
	public void heapSort(){
		for (int start=count/2; start>=0; start--){
			siftDown(start, count-1);
		}
		int current=count-1;
		while (current>0){
			swap(0, current);
			siftDown(0, current-1);
			current--;
		}
	}
}
