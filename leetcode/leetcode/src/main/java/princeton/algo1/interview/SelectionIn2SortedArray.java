package princeton.algo1.interview;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SelectionIn2SortedArray {
	private static Logger logger =  LogManager.getLogger(SelectionIn2SortedArray.class);
	//a, b no duplication
	private int find(int[] a, int startA, int endA, int[] b, int startB, int endB, int k){
		int m = endA-startA+1;
		int n = endB-startB+1;
		int i = (int)((double)m / (m+n) * (k-1));
		int j = (k-1) - i;
		int ai_1 = (i<=startA? Integer.MIN_VALUE:a[startA+i-1]);
		int ai = (i>=endA? Integer.MAX_VALUE:a[startA+i]);
		int bj_1 = (j<=startB? Integer.MIN_VALUE:b[startB+j-1]);
		int bj = (j>=endB? Integer.MAX_VALUE:b[startB+j]);
		if (ai>bj && bj>ai_1){
			return bj;
		}if (bj>ai && ai>bj_1){
			return ai;
		}
		int ret=0;
		if (ai>bj){
			ret= find(a, startA, i, b, j+1, endB, k-j-1);
		}else{
			ret= find(a, i+1, endA, b, startB, j, k-i-1);
		}
		logger.info(String.format("%d->%d, %d->%d, k:%d, ret:%d", startA, endA, startB, endB, k, ret));
		return ret;
	}
	
	//find the kth element in the 2 sorted array a, b T=log(a.length+b.length)
	public int find(int[] a, int[] b, int  k){
		return find(a, 0, a.length-1, b, 0, b.length-1, k);
	}
	
}
