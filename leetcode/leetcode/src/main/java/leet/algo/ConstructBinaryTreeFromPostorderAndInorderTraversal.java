package leet.algo;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.tree.TreeNode;

public class ConstructBinaryTreeFromPostorderAndInorderTraversal {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	private TreeNode buildTree(int[] postorder, int startp, int endp, int[] inorder, int starti, int endi){//[start, end)
		//logger.info(String.format("startp:%d, endp:%d, starti:%d, endi:%d", startp, endp, starti, endi));
		if (startp==endp){
			return null;
		}
		if (startp+1==endp){
			return new TreeNode(postorder[startp]);
		}
		int r = postorder[endp-1];
		TreeNode tn = new TreeNode(r);
		int i=starti;
		for (; i<endi; i++){
			if (inorder[i]==r){
				break;
			}
		}
    	TreeNode left = buildTree(postorder, startp, startp+i-starti, inorder, starti, i);
    	TreeNode right = buildTree(postorder, startp+i-starti, endp-1, inorder, i+1, endi);
    	tn.left = left;
    	tn.right = right;
    	return tn;
	}
	public TreeNode buildTree(int[] inorder, int[] postorder) {
		return buildTree(postorder, 0, postorder.length, inorder, 0, inorder.length);
    }
}
