package leet.algo;

import java.util.concurrent.ConcurrentHashMap;

class DLNode {
	int key;
	int val;
	DLNode prev;
	DLNode next;
	
	DLNode(int k, int v){
		key = k;
		val =v;
	}
	
	public String toString(){
		return key + ":" + val;
	}
	
}

public class LRUCache {

	public ConcurrentHashMap<Integer, DLNode> hm;
	DLNode head, tail;//head is the newest, tail is the oldest
	int curSize;
	int capacity;
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		DLNode node = head;
		while (node!=null){
			sb.append(node.key + ":" + node.val + ",");
			node = node.next;
		}
		sb.append("\n");
		node = tail;
		while (node!=null){
			sb.append(node.key + ":" + node.val + ",");
			node = node.prev;
		}
		sb.append("\n");
		sb.append(hm + "\n");
		return sb.toString();		
	}
	
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.curSize = 0;
    	hm = new ConcurrentHashMap<Integer, DLNode>(capacity);
    }
    
    void becomeHead(DLNode node){
    	if (head == node){
    		return;
    	}else{
    		DLNode preNode = node.prev;
    		if (preNode != null){
    			preNode.next = node.next;
    		}
    		if (node.next!=null){
    			node.next.prev = node.prev;
    		}
    		node.next = head;
    		head.prev = node;
    		
    		head = node;
    		head.prev = null;
    		
    		if (node == tail){
    			tail = preNode;
    			tail.next = null;
    		}
    	}
    }
    
    public int get(int key) {    	
        if (hm.containsKey(key)){
        	DLNode node = hm.get(key);
        	becomeHead(node);
        	return node.val;
        }else{
        	return -1;
        }
    	
    }    
    
    public void set(int key, int value) {
    	
        if (hm.containsKey(key)){
        	DLNode node = hm.get(key);
        	node.val = value;
        	becomeHead(node);
        }else{
        	DLNode node;
        	if (curSize == capacity){
        		hm.remove(tail.key);
        		tail.key = key;
        		tail.val = value;
        		becomeHead(tail);
        	}else if (curSize < capacity){
        		node = new DLNode(key, value);
        		if (curSize==0){
        			head = node;
        			tail = node;
        		}else{
	        		head.prev = node;
	        		node.next = head;
	        		head = node;
        		}
        		curSize++;        				
        	}else{
        		System.err.println("out of capacity, impossible.");
        		return;
        	}
    		hm.put(key, head);
        }
    	
    }
}
