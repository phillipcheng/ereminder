package leet.algo;

import algo.tree.TreeNode;
import algo.util.ListNode;

public class ConvertSortedListToBST {
	
	private ListNode[] getMid(ListNode head){
		ListNode first =head;
		ListNode mp = null;
		mp = head;
		ListNode premp = null;
		while (head!=null && head.next!=null){
			premp = mp;
			mp = mp.next;
			head = head.next.next;
		}
		premp.next = null;
		return new ListNode[]{first, mp, mp.next};
	}
	
	public TreeNode sortedListToBST(ListNode head) {
        if (head==null)
        	return null;
        else if (head.next==null){
        	return new TreeNode(head.val);
        }else{
        	ListNode[] parts = getMid(head);
        	ListNode left = parts[0];
        	ListNode mid = parts[1];
        	ListNode right = parts[2];
        	TreeNode tn = new TreeNode(mid.val);
        	TreeNode leftT = sortedListToBST(left);
        	TreeNode rightT = sortedListToBST(right);
        	tn.left = leftT;
        	tn.right = rightT;
        	return tn;
        }
    }
}
