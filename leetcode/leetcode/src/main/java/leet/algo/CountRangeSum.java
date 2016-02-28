package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//Given an integer array nums, return the number of range sums that lie in [lower, upper] inclusive.
public class CountRangeSum {

	private static Logger logger =  LogManager.getLogger(CountRangeSum.class);
	
	class BSTNode{
		BSTNode left;
		BSTNode right;
		long val;
		int nright;//#>
		int nleft;//#<
		int nequal;//#=
		
		public BSTNode(long val){
			this.val = val;
			this.nequal=1;
		}
		
		public String toString(){
			return String.format("v:%d, ne:%d, nb:%d, ns:%d", val, nequal, nright, nleft);
		}
	}
	
	public String treeToString(BSTNode root, int level){
		String ret="";
		if (root!=null){
			String leading="";
			for (int i=0; i<level; i++){
				leading=leading+"  ";
			}
			ret = String.format("%s %s", leading, root.toString());
			ret +="\n";
			ret += treeToString(root.left, level+1);
			ret +="\n";
			ret += treeToString(root.right, level+1);
		}
		return ret;
	}
	
	public void add(BSTNode root, long val){
		if (val==root.val){
			root.nequal++;
		}else if (val>root.val){
			if (root.right!=null){
				add(root.right, val);
			}else{
				root.right = new BSTNode(val);
			}
			root.nright++;
		}else{//val<root.val
			if (root.left!=null){
				add(root.left, val);
			}else{
				root.left = new BSTNode(val);
			}
			root.nleft++;
		}
	}
	
	//count the number of nodes whose value is > val
	public int countBigger(BSTNode root, long val){
		if (root.val==val){
			return root.nright;
		}else if (root.val<val){
			if (root.right!=null){
				return countBigger(root.right, val);
			}else{
				return 0;
			}
		}else{//root.val>val
			if (root.left!=null){
				return countBigger(root.left, val) + root.nright + root.nequal;
			}else{
				return root.nright + root.nequal;
			}
		}
	}
	
	//return the number of nodes whose value is < val
	public int countSmaller(BSTNode root, long val){
		if (root.val==val){
			return root.nleft;
		}else if (root.val<val){
			if (root.right!=null){
				return countSmaller(root.right, val) + root.nleft + root.nequal;
			}else{
				return root.nleft + root.nequal;
			}
		}else{//root.val>val
			if (root.left!=null){
				return countSmaller(root.left, val);
			}else{
				return 0;
			}
		}
	}
	
	public int countInclusiveBetween(BSTNode root, long low, long high){
		int total = root.nequal + root.nright + root.nleft;
		int big = countBigger(root, high);//>
		int small = countSmaller(root, low);//<
		return total - big - small;//[,]
	}
	
	public int countRangeSum(int[] nums, int lower, int upper) {
		if (nums.length==0)
			return 0;
		long[] sum = new long[nums.length+1];
		for (int i=0; i<nums.length; i++){
			sum[i+1] = sum[i] + nums[i];
		}
		int crs = 0;
		BSTNode root = new BSTNode(sum[0]);
		for (int i=1; i<sum.length; i++){
			crs+=countInclusiveBetween(root, sum[i]-upper, sum[i]-lower);
			//logger.info(treeToString(root, 0));
			//logger.info(String.format("crs:%d, %d", i, crs));
			add(root, sum[i]);
		}
		return crs;
    }
	
}
