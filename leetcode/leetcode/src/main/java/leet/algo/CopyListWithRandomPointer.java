package leet.algo;

import java.util.HashMap;
import java.util.Map;

public class CopyListWithRandomPointer {
	
	public RandomListNode copyRandomList(RandomListNode head) {
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

}
