package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.tree.TreeNode;

public class CountCompleteTreeNodes {
	private static Logger logger =  LogManager.getLogger(CountCompleteTreeNodes.class);
	private int getMaxHeight(TreeNode root){
		int h = 0;
		TreeNode node = root;
		while (node!=null){
			h++;
			node = node.left;
		}
		return h;
	}
	
	/*
	 * mh <= h
	 * mh: the height of the node being checked. 
	 * h: the height of the tree
	 * return ture: has the leaf node
	 */
	private boolean checkNode(TreeNode node, int mh, int h){
		//check whether the half of the tree reached h, go left then all right
		if (mh == h) return true;
		node = node.left;
		int rh = mh;
		while (node!=null){
			rh++;
			node = node.right;
		}
		return rh==h;
	}
	
	public int countNodes(TreeNode root) {
        TreeNode node = root;
        int h = getMaxHeight(root);
        //logger.info(String.format("height:%d", h));
        int mh = 1;
        int idx=1;
        while (mh<h){
        	if (checkNode(node, mh, h)){
        		node = node.right;
        		idx = idx*2+1;
        	}else{
        		node = node.left;
        		idx = idx*2;
        	}
        	mh++;
        }
        if (node==null) idx--;
        return idx;
    }

}
