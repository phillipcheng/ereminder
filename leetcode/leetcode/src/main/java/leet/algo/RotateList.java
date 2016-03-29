package leet.algo;

import algo.util.ListNode;

public class RotateList {
	
	public ListNode rotateRight(ListNode head, int k) {
		
		if (head==null) return null;
		int n=0;
        ListNode end = head;
        while (end.next!=null){
        	end = end.next;
        	n++;
        }
        n++;
        int pos = 0;
        if (k%n!=0)
        	pos = n-k%n-1;//0 based
        else
        	return head;
        ListNode node = head;//node point to the node before the new head
        int i=0;
        while (i<pos){
        	node = node.next;
        	i++;
        }
        ListNode newHead = node.next;
        end.next=head;
        node.next=null;
        return newHead;
    }

}
