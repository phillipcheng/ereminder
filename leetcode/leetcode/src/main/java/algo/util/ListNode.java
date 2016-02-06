package algo.util;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class ListNode {
	
	public int val;
	
	public ListNode next;
	
	
	public ListNode(int x) {
		val = x;
		next = null;
	}
	
	//2,4,3
	public static ListNode getLN(String s){
		StringTokenizer st = new StringTokenizer(s, "[ ],", false);
		ListNode prev=null;
		ListNode cur;
		ListNode head=null;
		int count =0;
		while (st.hasMoreTokens()){
			count++;
			cur = new ListNode(Integer.parseInt(st.nextToken()));
			if (prev!=null){
				prev.next = cur;
			}
			prev = cur;
			
			if (count==1){
				head = cur;
			}			
		}
		return head;
	}
	
	public String toString(){
		ListNode ln = this;
		StringBuffer sb = new StringBuffer();
		while (ln!=null){
			sb.append(ln.val);
			sb.append(",");
			ln = ln.next;
		}
		return sb.toString();
	}
	
}
