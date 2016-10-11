package leet.algo;

import java.util.ArrayList;
import java.util.List;

import algo.tree.TreeNode;

public class LowestCommonAncestorOfBinaryTree {
	
	private List<TreeNode> dfsFind(TreeNode root, TreeNode p, List<TreeNode> parents){
		if (root != p){
			List<TreeNode> lp = null;
			List<TreeNode> rp = null;
			if (root.left!=null){
				parents.add(root);
				lp = dfsFind(root.left, p, parents);
			}
			if (lp!=null) return lp;
			else{
				parents.remove(root);
			}
			if (root.right!=null){
				parents.add(root);
				rp = dfsFind(root.right, p, parents);
			}
			if (rp!=null) return rp;
			else{
				parents.remove(root);
			}
			
			return null;
		}else{
			parents.add(root);
			return parents;
		}
	}
	

	public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
		List<TreeNode> p1 = new ArrayList<TreeNode>();
		List<TreeNode> p2 = new ArrayList<TreeNode>();
		List<TreeNode> pl = dfsFind(root, p, p1);
		List<TreeNode> ql = dfsFind(root, q, p2);
		int min = Math.min(pl.size(), ql.size());
		int i=0;
		for (;i<min; i++){
			if (pl.get(i)!=ql.get(i)){
				break;
			}
		}
		i--;
        return pl.get(i);
    }

}
