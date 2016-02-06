package algo.leet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import algo.util.ListNode;

class TreeMapListNode{
	//1st node's value to the array of node list
	TreeMap<Integer, ArrayList<ListNode>> tm = new TreeMap<Integer, ArrayList<ListNode>>();
	
	void put(int v, ListNode ln){
		ArrayList<ListNode> aln = tm.get(v);
		if (aln!=null){
			aln.add(ln);
		}else{
			aln= new ArrayList<ListNode>();
			aln.add(ln);
			tm.put(v, aln);
		}
	}
	
	//can't be empty, checked before call
    ListNode removeHead(){
    	Iterator<Integer> it = tm.keySet().iterator();
    	int v = it.next();
    	ArrayList<ListNode> all = tm.get(v);
    	if (all.size()==0){
    		tm.remove(v);
    		return null;
    	}else{
    		ListNode ln = all.remove(all.size()-1);
    		if (all.size()==0){
    			tm.remove(v);
    		}
    		return ln;
    	}
    }
    
    boolean isEmpty(){
    	return tm.isEmpty();
    }
	
    public String toString(){
    	return tm.toString();
    }
		
}

//Merge k sorted linked lists and return it as one sorted list. 
//Analyze and describe its complexity.
public class MergeKSortedList {
	public static boolean isDebug=false;
	public static void log(Object obj){
		if (isDebug){
			System.out.println(obj);
		}
	}
	
	public ListNode mergeKLists(ArrayList<ListNode> lists) {
    	ListNode ret=null, tail=null;
    	TreeMapListNode tmln = new TreeMapListNode();
    	for (int i=0; i<lists.size(); i++){
    		ListNode l = lists.get(i);
    		if(l!=null){
	    		tmln.put(l.val, l);
    		}
    	}
    	//log("hmln:" + tmln);
    
    	
    	int count =0;
    	
    	while (!tmln.isEmpty()){
	    	ListNode l = tmln.removeHead();
	    	//log("remove l:" + l);
	    	//log("hmln:" + tmln);
	    	
	    	if (count==0){
    			ret = l;
    			tail = l;
    		}else{
    			tail.next=l;
    			tail = tail.next;
    		}
	    	if (l.next!=null){
	    		l=l.next;
	    		tmln.put(l.val, l);
		    	//log("add l:" + l);
		    	//log("hmln:" + tmln);
	    	}
	    	count++;
    	}
    	
    	return ret;
    }
}
