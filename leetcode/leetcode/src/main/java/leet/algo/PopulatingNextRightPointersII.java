package leet.algo;

import algo.tree.TreeLinkNode;

public class PopulatingNextRightPointersII {
	public void dfs(TreeLinkNode node){
		if (node!=null){
			TreeLinkNode nn = node;//next node in this lvl
			TreeLinkNode cn = null;//current node in next lvl
			if (nn.left!=null){
				cn = nn.left;
			}else if (nn.right!=null){
				cn = nn.right;
			}else{//no child
				return;
			}
			while (nn!=null && cn!=null){
				if (nn.left!=null && nn.left!=cn){
					cn.next = nn.left;
					cn = cn.next;
				}else if (nn.right!=null && nn.right!=cn){
					cn.next = nn.right;
					cn = cn.next;
					nn = nn.next;
				}else{
					nn = nn.next;
				}
			}
			dfs(node.left);
			dfs(node.right);
		}
	}
	
	public void connect(TreeLinkNode root) {
		dfs(root);
	}
}
