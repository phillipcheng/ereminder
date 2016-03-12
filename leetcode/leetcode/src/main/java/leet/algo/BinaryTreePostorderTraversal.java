package leet.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import leet.algo.test.TestBinaryTreePostorderTraversal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.tree.TreeNode;

public class BinaryTreePostorderTraversal {
	private static Logger logger =  LogManager.getLogger(BinaryTreePostorderTraversal.class);
	class MoreNode{
		TreeNode node;
		int relation;//0 for root, 1 for left and 2 right
		public MoreNode(TreeNode node, int relation){
			this.node = node;
			this.relation = relation;
		}
	}
	public List<Integer> postorderTraversal(TreeNode root) {
		List<Integer> ret = new ArrayList<Integer>();
		if (root==null) return ret;
		Stack<MoreNode> stack = new Stack<MoreNode>();
		stack.add(new MoreNode(root,0));
		while (!stack.isEmpty()){
			MoreNode mn = stack.peek();
			TreeNode tn = mn.node;
			while (tn.left!=null){
				tn = tn.left;
				stack.push(new MoreNode(tn, 1));
			}
			if (tn.right!=null){//tn left is null, tn right is not null
				stack.push(new MoreNode(tn.right,2));
			}else{//tn left and right are null
				mn = stack.pop();
				ret.add(tn.val);
				if (mn.relation==0){//root
					break;
				}else if (mn.relation==1){
					stack.peek().node.left=null;
				}else{
					stack.peek().node.right=null;
				}
			}
			//logger.info(String.format("stack:%s, ret:%s",  stack, ret));
		}
		return ret;
    }

}
