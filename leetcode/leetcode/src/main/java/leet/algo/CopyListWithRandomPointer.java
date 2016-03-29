package leet.algo;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {
	
	/*
	 * Using hashtable is wrong check following example
	//b->d2->d1
	//|------^

	//b->d2->d1
	//|--^
	 * */
	public RandomListNode copyRandomListUsingHashTable(RandomListNode head) {
		if (head==null) return null;
        Map<Integer, RandomListNode> map =  new HashMap<Integer, RandomListNode>();
        RandomListNode retHead = new RandomListNode(head.label);
        map.put(head.label, retHead);
        RandomListNode retCur = retHead;
        RandomListNode cur = head.next;
        while(cur!=null){
        	RandomListNode node = new RandomListNode(cur.label);
        	map.put(cur.label, node);
        	retCur.next = node;
        	retCur = node;
        	cur = cur.next;
        }
        cur = head;
        retCur = retHead;
        while(cur!=null){
        	if (cur.random!=null){
        		retCur.random = map.get(cur.random.label);
        	}else{
        		retCur.random = null;
        	}
        	retCur = retCur.next;
        	cur = cur.next;
        }
        return retHead;
    }
	
	public RandomListNode copyRandomList(RandomListNode head) {
		if (head==null) return head;
		RandomListNode cur = head;
		//insert a new copy of each node directly after the node
		while (cur!=null){
			RandomListNode newCur = new RandomListNode(cur.label);
			newCur.next = cur.next;
			cur.next = newCur;
			cur = newCur.next;
		}
		//set the random for each new node
		cur = head;
		while (cur!=null){
			RandomListNode newCur = cur.next;
			if (cur.random!=null){
			    newCur.random = cur.random.next;
			}
			cur = newCur.next;
		}
		//restore the random for each old node, and the next fro each new node
		cur = head;
		RandomListNode ret = cur.next;
		while (cur!=null){//cur.next!=null
			RandomListNode newCur = cur.next;
			cur.next=cur.next.next;
			if (cur.next!=null){
				newCur.next = cur.next.next;
			}else{
				newCur.next = null;
			}
			cur = cur.next;
		}
		return ret;
	}
	
	
}
