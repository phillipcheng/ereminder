package algo.leet;

import java.util.StringTokenizer;

import algo.util.ListNode;

//Sort a linked list in O(n log n) time using constant space complexity.

 
 
public class SortList {

	public static void printList(String comment, ListNode l){
		System.out.println(comment);
		while (l!=null){
			System.out.print(l.val + ",");
			l = l.next;
		}
		System.out.println();
	}
	
	//input str: 4,5,6,7,7,8,10,1
	public String sort(String str){
		StringTokenizer st = new StringTokenizer(str, ", ", false);
		int count = 0;
		ListNode head = null;
		ListNode tail = null;
		while (st.hasMoreTokens()){
			int v = Integer.parseInt(st.nextToken());
			ListNode ln = new ListNode(v);
			if (count==0){
				head = ln;
				tail = head;
			}else{
				tail.next = ln;
				tail = ln;
			}
			count++;
		}
		tail = null;
		
		//printList("before sort:", head);
		
		ListNode sortedList = sortList(head);
		
		//printList("after sort:", sortedList);
		
		StringBuffer sb = new StringBuffer();
		while (sortedList != null){
			sb.append(sortedList.val);
			sb.append(",");
			sortedList = sortedList.next;
		}
		return sb.toString();
	}
	
    public ListNode sortList(ListNode head) {
       if (head == null || head.next==null){
    	   return head;
       }else{
    	   ListNode mark = head;
    	   ListNode n = mark.next;
    	   ListNode smallHead = null;
    	   ListNode smallTail = null;
    	   ListNode bigHead = null;
    	   ListNode bigTail = null;
    	   int count =0;
    	   while (n!=null){
    		   //we need to split the equal node half-half to smallList and bigList to prevent quadratic
 			   count++;
    		   if (n.val<mark.val || (n.val == mark.val && count%2==1)){
    			   if (smallHead == null){
    				   smallHead = n;
    				   smallTail = smallHead;
    			   }else{
    				   smallTail.next=n;
    				   smallTail = n;
    			   }
    		   }else if (n.val > mark.val || (n.val == mark.val && count%2 == 0)){
    			   if (bigHead == null){
    				   bigHead = n;
    				   bigTail = bigHead;
    			   }else{
    				   bigTail.next=n;
    				   bigTail = n;
    			   }
    		   }
    		   n=n.next;
    	   }
    	   if (smallTail != null)
    		   smallTail.next=null;
    	   if (bigTail != null)
    		   bigTail.next=null;
    	   
    	   ListNode sortedSmallHead = sortList(smallHead);
    	   ListNode sortedBigHead = sortList(bigHead);
    	   
    	   if (sortedSmallHead!=null){
    		   ListNode sortedSmallTail = sortedSmallHead;
    		   while (sortedSmallTail.next != null){
    			   sortedSmallTail = sortedSmallTail.next;
    		   }
    		   sortedSmallTail.next=mark;
    		   mark.next = sortedBigHead;
    		   return sortedSmallHead;
    	   }else{
    		   mark.next = sortedBigHead;
    		   return mark;
    	   }
       }
    }
}
