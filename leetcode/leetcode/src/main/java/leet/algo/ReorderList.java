package leet.algo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.ListNode;

public class ReorderList {
	private static Logger logger =  LogManager.getLogger(ReorderList.class);
	public void reorderList(ListNode head) {
		if (head==null) return;
		ListNode mid = head; // n/2
		ListNode faster=head;
		
		//find mid
		while (faster!=null && faster.next!=null){
			mid=mid.next;
			faster=faster.next.next;
		}
		//reverse second half
		ListNode end,b;
		end= mid;
		b= mid.next;
		while(b!=null){
			ListNode c = b.next;
			b.next = end;
			end = b;
			b = c;
		}
		mid.next=null;
		ListNode h = head;
		//
		while (h!=mid && end!=mid){
			ListNode hn = h.next;
			ListNode en = end.next;
			h.next=end;
			end.next=hn;
			h = hn;
			end = en;
		}
    }

}
