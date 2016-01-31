package cy;

import cy.util.ListNode;


//Given a linked list, reverse the nodes of a linked list k at a time and return its modified list.
//If the number of nodes is not a multiple of k then left-out nodes in the end should remain as it is.
public class ReverseNodesInKGroup {
	
	public ListNode getPreNode(ListNode from, ListNode to){
		ListNode ln = from;
		if (from==to)
			return from;//from equals to
		while(ln.next!=to){
			ln = ln.next;
		}
		return ln;
	}
	
    public ListNode reverseKGroup(ListNode head, int k) {
        //get the first k nodes if have, reverse them and link to the reversed right part
    	ListNode h = head;
    	ListNode tail = h;
    	int count=0;
    	while (h!=null && count<k){
    		tail = h;
    		h = h.next;
    		count++;
    	}
    	if (count==k){
    		//reverse these k nodes, from head to tail nodes (t)
    		ListNode t = tail;
    		ListNode pt; //preTail
    		while (head!=t){
    			pt = getPreNode(head, t);
    			t.next=pt;
    			t = pt;
    		}
    		//reverse tail part
    		ListNode tailPart = reverseKGroup(h, k);
    		
    		//link them
    		head.next=tailPart;
    		return tail;
    	}else{//less then k, so no reverse needed
    		return head;
    	}
    }
}
