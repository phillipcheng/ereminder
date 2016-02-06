package algo.leet;

import algo.util.ListNode;

/*
 * 
You are given two linked lists representing two non-negative numbers. 
The digits are stored in reverse order and each of their nodes contain a single digit. 
Add the two numbers and return it as a linked list.

Input: (2 -> 4 -> 3) + (5 -> 6 -> 4)
Output: 7 -> 0 -> 8
 */


public class AddTwoNumber {
	public static boolean isDebug = false;
	
	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
		ListNode a = l1;
		ListNode b = l2;
		int carry=0;
		ListNode ans = null;
		ListNode prevAns=null;
		ListNode curAns=null;
		
		int count =0;
		while (a!=null || b!=null){
			if (isDebug){
				System.out.println("a:" + a);
				System.out.println("b:" + b);
			}
			count++;
			if (a!=null && b!= null){
				
				int sum = a.val + b.val + carry;
				carry = sum / 10;
				int val = sum % 10;
				curAns = new ListNode(val);
				a=a.next;
				b=b.next;
			}else if (a==null && b!= null){				
				int sum = b.val + carry;
				carry = sum / 10;
				int val = sum % 10;
				curAns = new ListNode(val);
				b=b.next;
			}else if (a!=null && b== null){
				
				int sum = a.val + carry;
				carry = sum / 10;
				int val = sum % 10;
				curAns = new ListNode(val);
				a=a.next;
			}
			
			if (prevAns!=null){
				prevAns.next = curAns;
			}
			prevAns = curAns;
			if (count==1){
				ans = prevAns;
			}
		}
		
		if (carry>0){
			//add last node
			curAns = new ListNode(carry);
		}
		
		prevAns.next = curAns;
		prevAns = curAns;
		prevAns.next = null;
		
		
		
        return ans;
    }
}
