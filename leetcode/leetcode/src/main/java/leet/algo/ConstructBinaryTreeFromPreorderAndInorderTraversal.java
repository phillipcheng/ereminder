package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.tree.TreeNode;

public class ConstructBinaryTreeFromPreorderAndInorderTraversal {
	private static Logger logger =  LogManager.getLogger(ConstructBinaryTreeFromPreorderAndInorderTraversal.class);
	private TreeNode buildTree(int[] preorder, int startp, int endp, int[] inorder, int starti, int endi){//[start, end)
		//logger.info(String.format("startp:%d, endp:%d, starti:%d, endi:%d", startp, endp, starti, endi));
		if (startp==endp){
			return null;
		}
		if (startp+1==endp){
			return new TreeNode(preorder[startp]);
		}
		int r = preorder[startp];
		TreeNode tn = new TreeNode(r);
		int i=starti;
		for (; i<endi; i++){
			if (inorder[i]==r){
				break;
			}
		}
    	TreeNode left = buildTree(preorder, startp+1, startp+1+i-starti, inorder, starti, i);
    	TreeNode right = buildTree(preorder, startp+1+i-starti, endp, inorder, i+1, endi);
    	tn.left = left;
    	tn.right = right;
    	return tn;
	}
	public TreeNode buildTree(int[] preorder, int[] inorder) {
		return buildTree(preorder, 0, preorder.length, inorder, 0, inorder.length);
    }

}
