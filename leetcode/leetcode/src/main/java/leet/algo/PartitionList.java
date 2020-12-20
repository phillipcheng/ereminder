package leet.algo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.ListNode;

public class PartitionList {
	private static Logger logger =  LogManager.getLogger(PartitionList.class);
	public ListNode partition(ListNode head, int x) {
		//find the 1st one >=x
		//for all the node after y but bigger than x will be moved right before y
		ListNode preYn = null;
		ListNode yn = head;
		while (yn!=null){
			if (yn.val>=x){
				break;
			}else{
				preYn = yn;
				yn = yn.next;
			}
		}
        if (yn==null) return head;
		ListNode zn = yn;
		ListNode preZn = preYn;
		ListNode ret = new ListNode(0);
		if (preYn!=null){
			ret.next = head;
		}else{
			ret.next = zn;
			preYn = ret;
		}
        while(zn!=null){
        	//logger.info(zn.val);
        	if (zn.val<x){
        		//move zn before yn
        		ListNode oldZn = zn;
    			preZn.next = zn.next;
    			zn.next = yn;
    			preYn.next = zn;
    			zn = preZn.next;
    			preYn = oldZn;
        	}else{
        		preZn = zn;
        		zn = zn.next;
        	}
        }
        return ret.next;
    }

}
