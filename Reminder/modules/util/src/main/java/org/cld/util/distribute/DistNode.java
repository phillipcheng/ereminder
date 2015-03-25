package org.cld.util.distribute;

import java.util.ArrayList;
import java.util.List;


/**
 * this is the node while doing distribution calculation
 * @author cheyi
 *
 */
public class DistNode<K>{
	public String nodeId;
	public int numKeys; //# keys of this src node after redistribution
	public int numMoves; //total # moves needed, >0 means give, <0 means take
	public List<K> keys = new ArrayList<K>(); // the list of keys on the node now
	public int moveLeft; //# of moves left
	
	public String toString(){
		return "nodeId:" + nodeId + "\n" +
				"numKeys:" + numKeys + "\n" + 
				"numMoves:" + numMoves + "\n" + 
				"keys:" + keys +
				"moveLeft" + moveLeft;
	}
	
	//return the last # of keys of the list
	public synchronized List<K> getLastKeys(int num){
		int len = keys.size();
		return keys.subList(len-num, len);
	}
	
}