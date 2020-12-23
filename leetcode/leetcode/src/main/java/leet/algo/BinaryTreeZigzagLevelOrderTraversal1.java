package leet.algo;

import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class BinaryTreeZigzagLevelOrderTraversal1 {

	private static Logger logger =  LogManager.getLogger(BinaryTreeZigzagLevelOrderTraversal1.class);

	public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
		List<List<Integer>> out = new ArrayList<List<Integer>>();
		if (root==null) return out;
		boolean dir=true; //true: left to right
		Stack<TreeNode> newStack = new Stack<>();//
		newStack.push(root);
		while(!newStack.isEmpty()) {
			Stack<TreeNode> stack = newStack;
			List<Integer> level = new ArrayList<>();
			newStack = new Stack<>();
			while (!stack.isEmpty()) {
				TreeNode tr = stack.pop();
				level.add(tr.val);
				if (dir) {
					if (tr.left!=null)
						newStack.push(tr.left);
					if (tr.right!=null)
						newStack.push(tr.right);
				} else {
					if (tr.right!=null)
						newStack.push(tr.right);
					if (tr.left!=null)
						newStack.push(tr.left);
				}
			}
			dir = !dir;
			out.add(level);
		}

		return out;
    }

    public static void main(String[] args){
		BinaryTreeZigzagLevelOrderTraversal1 btz = new BinaryTreeZigzagLevelOrderTraversal1();
		TreeNode tn = TreeNodeUtil.bfsFromString("3,9,20,#,#,15,7");
		List<List<Integer>> out = btz.zigzagLevelOrder(tn);
		logger.info(out);
	}
}
