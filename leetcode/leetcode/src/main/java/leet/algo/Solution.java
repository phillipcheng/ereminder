package leet.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import algo.tree.TreeLinkNode;
import algo.tree.TreeNode;
import algo.tree.TreeNodeUtil;
import algo.util.ListNode;
import algo.util.ListNodeUtil;

public class Solution {
	public void reorderList(ListNode head) {
        if (head==null) return;
        ListNode fast=head;
        ListNode slow=head;
        while(fast.next!=null && fast.next.next!=null){
            fast=fast.next.next;
            slow=slow.next;
        }
        fast=slow.next;//fast
        slow.next=null;//head
        System.out.println("slow:" + ListNodeUtil.toString(head));
        System.out.println("fast:" + ListNodeUtil.toString(fast));
        ListNode pre=null;
        while(fast!=null){
            ListNode n=fast.next;
            fast.next=pre;
            pre=fast;
            fast=n;
        }
        System.out.println("reverse fast:" + ListNodeUtil.toString(pre));
        //pre
        //head
        ListNode start=head;
        boolean first=false;
        ListNode c=head;
        start=start.next;
        ListNode next=first?start:pre;
        while(next!=null){
            c.next=next;
            if (first){
            	start=start.next;
            }else{
            	pre=pre.next;
            }
            first=!first;
            next=first?start:pre;
            c=c.next;
        }
    }
	
	public static void main(String[] args){
		Solution sol = new Solution();
		ListNode l = ListNodeUtil.getLN("1,2,3");
		sol.reorderList(l);
		System.out.println(ListNodeUtil.toString(l));
	}
}
