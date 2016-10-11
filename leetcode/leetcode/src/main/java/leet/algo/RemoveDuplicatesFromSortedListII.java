package leet.algo;

import algo.util.ListNode;

public class RemoveDuplicatesFromSortedListII {
	
	public ListNode deleteDuplicates(ListNode head) {
		if (head==null) return head;
		ListNode ret = null;
		ListNode pre = null;
        ListNode cur = head;
        ListNode ahead = cur.next;
        int batchCount=1;//batch num of the cur value
        while (ahead!=null){
        	if (cur.val==ahead.val){
        		batchCount++;
        	}else{
        		if (batchCount==1){
        			if (pre!=null){
        				pre.next = cur;
        				pre = pre.next;
        			}else{
        				pre = cur;
        				ret = pre;
        			}
        		}
        		batchCount=1;
        	}
        	cur = ahead;
    		ahead = ahead.next;
        }
        if (batchCount==1){
			if (pre!=null){
				pre.next = cur;
			}else{
				pre = cur;
				ret = pre;
			}
			pre = pre.next;
		}
        if (pre!=null)
        	pre.next=null;
		return ret;
    }
}
