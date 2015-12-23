package org.cld.util.distribute;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author cheyi
 *
 * @param <K>
 */
public class DistResult<K>{
	public String srcNodeId;
	public String destNodeId;
	public int numKeys; //# keys of this src node after redistribution
	public int numMoves; // # moves needed, <0 means to src node, >0 means from src node
	public List<K> movingKeys = new ArrayList<K>(); // the list of keys to move
	
	public String toString(){
		return "srcNodeId:" + srcNodeId + "\n" + 
				"destNodeId:" +  destNodeId + "\n" + 
				"numKeys:" + numKeys + "\n" + 
				"numMoves:" + numMoves + "\n" + 
				"movingKeys:" + movingKeys;
	}
	
}