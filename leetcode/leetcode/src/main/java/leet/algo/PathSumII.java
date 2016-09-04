package leet.algo;

import java.util.ArrayList;
import java.util.List;

import algo.tree.TreeNode;

public class PathSumII {
	//path has to extend to the very lowest level
	public List<List<Integer>> pathSum(TreeNode root, int sum) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
        if (root==null) {
        	return result;
        }else{
        	if (root.left==null && root.right==null){
        		if (sum == root.val){
        			List<Integer> l = new ArrayList<Integer>();
        			l.add(root.val);
        			result.add(l);
        		}
        	}else{
            	int s = sum - root.val;
            	List<List<Integer>> ll = pathSum(root.left, s);
        		ll.addAll(pathSum(root.right, s));
        		for (List<Integer> l: ll){
            		l.add(0, root.val);
            		result.add(l);
            	}
        	}
        	
        	return result;
        }
    }

}
