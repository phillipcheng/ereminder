package algo.util;

import java.util.StringTokenizer;

import leet.algo.test.TestAdditiveNumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListNodeUtil {
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
	
	public static String toString(ListNode ln){
		StringBuffer sb = new StringBuffer();
		while (ln!=null){
			sb.append(ln.val);
			sb.append(",");
			ln = ln.next;
		}
		return sb.toString();
	}
}
