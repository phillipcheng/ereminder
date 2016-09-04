package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.ListNode;
import leet.algo.test.TestAdditiveNumber;

public class InsertionSortList {
	private static Logger logger =  LogManager.getLogger(TestAdditiveNumber.class);
	public ListNode insertionSortList(ListNode head) {
		if (head==null) return null;
		ListNode ret = new ListNode(0);
		ret.next = head;
		ListNode cur = head;
		ListNode preCur = head;
		ListNode next = cur.next;
		while (next!=null){
			//preCur = cur;
			cur = next;
			next = next.next;
			//logger.info("outer:" + cur.val);
			//cur is the node we need to insert to the ordered list pointed by head
			ListNode start = ret.next;
			ListNode preStart = ret.next;
			while (cur.val>start.val){
				//logger.info("inner:" + start.val);
				preStart = start;
				start = start.next;
			}
			if (start==preStart){//cur is the smallest
				ret.next = cur;
				cur.next = start;
				preCur.next = next;
				
			}else if (start==cur){//cur is the last
				//do nothing
				preCur = cur;
			}else{
				preStart.next = cur;
				cur.next = start;
				preCur.next = next;
				
			}
			
		}
		return ret.next;
    }
}
