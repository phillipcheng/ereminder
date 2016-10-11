package leet.algo;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.ListNode;

public class ReversedLinkedListII {
	private static Logger logger =  LogManager.getLogger(ReversedLinkedListII.class);
	//1 ≤ m ≤ n ≤ length of list.
	public ListNode reverseBetween(ListNode head, int m, int n) {
        ListNode start = null;
        ListNode first = null;
        if (m>1){
        	start=head;
	        for (int i=1;i<m-1; i++){
	        	start = start.next;
	        }
	        first = start.next;
        }else{
        	start=null;
        	first=head;
        }
        
        ListNode cur = first;
        ListNode next = cur.next;
        ListNode pre = null;
        for (int i=m; i<n; i++){
        	pre = cur;
        	cur = next;
        	if (next!=null){
        		next = next.next;
        	}
        	if (cur!=null)
        		cur.next = pre;
        }
        //logger.info(String.format("start:%d, cur:%d, first:%d", start.val, cur.val, first.val));
        first.next = next;
        if (start!=null){
        	start.next = cur;
        	return head;
        }
        else{
        	start = cur;
        	return start;
        }
    }
}
