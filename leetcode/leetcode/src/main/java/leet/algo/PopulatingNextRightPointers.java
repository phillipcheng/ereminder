package leet.algo;

import algo.tree.TreeLinkNode;

public class PopulatingNextRightPointers {
	
	//first is true, return the 1st node, false return the last node
	public void dfs(TreeLinkNode node){
		if (node!=null){
			if (node.left!=null){
				node.left.next=node.right;
				dfs(node.left);
			}
			if (node.right!=null){
				if (node.next!=null)
					node.right.next = node.next.left;
				dfs(node.right);
			}
		}
	}
	
	public void connect(TreeLinkNode root) {
		dfs(root);
	}
}
