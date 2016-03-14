package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NumArray {
	private static Logger logger =  LogManager.getLogger(NumArray.class);
	
	class SegmentNode{
		int left;
		int right;
		int val;
		public SegmentNode(int left, int right, int val){
			this.left = left;
			this.right = right;
			this.val = val;
		}
		public String toString(){
			return String.format("%d-%d:%d", left,right,val);
		}
	}
	
	SegmentNode[] st;//segment tree
	int[] nums;
	
	//set the output[idx] to the range sum of input[start,end]
	private int setup(int idx, int start, int end){
		if (start==end){
			st[idx]=new SegmentNode(start, start, nums[start]);
		}else{
			int mid = (start+end)/2;
			int sum1 = setup(2*idx+1, start, mid);
			int sum2 = setup(2*idx+2, mid+1, end);
			st[idx]= new SegmentNode(start, end, sum1+sum2);
		}
		//logger.info(String.format("%d->%d:%d:%s", start, end, idx, st[idx]));
		return st[idx].val;
	}
	
	public NumArray(int[] nums) {
		this.nums = nums;
        int n = nums.length;
        if (n>0){
        	int x = (int)Math.ceil(Math.log(n)/Math.log(2));
        	st = new SegmentNode[2 * (int) Math.pow(2, x) - 1];
        	setup(0, 0, n-1);
        }
    }

	// i hit the segment node idx 
	private void update(int idx, int i, int diff){
		SegmentNode sn = st[idx];
		if (sn.left<=i && sn.right>=i){
			if (sn.right==sn.left){
				nums[sn.left]+=diff;
			}else{
				update(2*idx+1, i, diff);
				update(2*idx+2, i, diff);
			}
			sn.val += diff;
		}
	}
	
    public void update(int i, int val) {
    	int diff = val - nums[i];
        update(0, i, diff);
    }

    //return the sum range from segment node at idx belongs to [i,j]
    private int sumRange(int idx, int i, int j){
    	SegmentNode sn = st[idx];
    	if (i<=sn.left && j>=sn.right){
    		return sn.val;
    	}else if (i>sn.right || j<sn.left){
    		return 0;
    	}else{
    		return sumRange(2*idx+1, i,j) + sumRange(2*idx+2, i, j);
    	}
    }
    
    public int sumRange(int i, int j) {
        return sumRange(0, i, j);
    }
}
