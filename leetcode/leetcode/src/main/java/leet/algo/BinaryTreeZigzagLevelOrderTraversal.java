package leet.algo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import algo.tree.TreeNode;

public class BinaryTreeZigzagLevelOrderTraversal {
	
	class LeveledTreeNode{
		int level;
		TreeNode node;
		public LeveledTreeNode(int lvl, TreeNode node){
			this.level = lvl;
			this.node = node;
		}
	}
	public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
		List<List<Integer>> out = new ArrayList<List<Integer>>();
		if (root==null) return out;
		LinkedList<LeveledTreeNode> queue = new LinkedList<LeveledTreeNode>();
		queue.add(new LeveledTreeNode(1, root));
		int curLvl = 0;
		boolean dir = true;//left to right
		while (!queue.isEmpty()){
			LeveledTreeNode ltn = queue.poll();
			int lvl = ltn.level;
			if (lvl!=curLvl){
				//new level found
				List<Integer> ol = new ArrayList<Integer>();
				if (dir){
					ol.add(ltn.node.val);
				}else{
					ol.add(0, ltn.node.val);
				}
				curLvl = lvl;
				//fetch all node with the curLvl and add to output
				ListIterator<LeveledTreeNode> li = queue.listIterator();
				while (li.hasNext()){
					LeveledTreeNode n = li.next();
					if (n.level==lvl){
						if (dir){
							ol.add(n.node.val);
						}else{
							ol.add(0, n.node.val);
						}
					}else{
						break;
					}
				}
				out.add(ol);
				dir = !dir;
			}
			if (ltn.node.left!=null){
				LeveledTreeNode n = new LeveledTreeNode(lvl+1, ltn.node.left);
				queue.add(n);
			}
			if (ltn.node.right!=null){
				LeveledTreeNode n = new LeveledTreeNode(lvl+1, ltn.node.right);
				queue.add(n);
			}
		}
		return out;
    }
}
