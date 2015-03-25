package org.cld.util.distribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 *
 * @param <K>: the key type of item to be partitioned, for example Task
 */
public class DistributeKeys<K> {

	private static Logger logger =  LogManager.getLogger(DistributeKeys.class);		
	
	/**
	 * try to rebalance globally
	 * rebalance all tasks according to the capacity distribution
	 * note: moving will occur between non-input nodes
	 * 
	 * 
	 * @param senderId
	 * @param nodeConfMap: the node configuration map.
	 * @param keys: the data key distribution before re-balancing
	 * @return
	 */
	public List<DistResult<K>> rebalance (String senderId, Map<String, SimpleNodeConf> clientConfs, 
			Map<String, Set<K>> dataKeys){	
		
		int tCap=0;		
		Iterator<SimpleNodeConf> itnc = clientConfs.values().iterator();
		while (itnc.hasNext()){
			SimpleNodeConf anc = itnc.next();
			tCap +=anc.getThreadSize();
		}
		int newTCap = tCap;
		
		int tKey=0;
		Iterator<Set<K>> itkeys = dataKeys.values().iterator();
		while (itkeys.hasNext()){
			Set<? extends K> kl = itkeys.next();
			tKey +=kl.size();
		}
		int newTKey = tKey;
		
		//
		List<DistNode<K>> giveNodeList = new ArrayList<DistNode<K>>();
		List<DistNode<K>> takeNodeList = new ArrayList<DistNode<K>>();
		Iterator<String> its = clientConfs.keySet().iterator();
		int remain = 0;
		while (its.hasNext()){
			String nid = its.next();
			SimpleNodeConf anc = clientConfs.get(nid);
			
			Set<? extends K> oldKeys = dataKeys.get(nid);			
			int numOldKeys = oldKeys.size();
			int numMain = 0;
			if (newTCap != 0){
				numMain = (anc.getThreadSize() * newTKey) / newTCap;
				remain += (anc.getThreadSize() * newTKey) % newTCap; //remain is between 0 to #node -1
				if (remain/newTCap >=1){
					//when the residual larger then newTCap
					int a = remain/newTCap;
					numMain +=a;
					remain = remain % newTCap;
				}
			}else{
				numMain = 0;
			}
			

			DistNode<K> dn = new DistNode<K>();
			
			dn.nodeId = nid;
			dn.numKeys = numMain; //number of keys-to-be on this node-id
			dn.numMoves = numOldKeys - numMain;
			dn.keys.addAll(oldKeys);
			
			if (dn.numMoves >0){
				giveNodeList.add(dn);
			}else if (dn.numMoves < 0){
				takeNodeList.add(dn);
			}else{
			}
		}
		
		
		//1.5, generate all the distribution results
		List<DistResult<K>> results = new ArrayList<DistResult<K>>();
		DistResult<K> dr = null;
		while (giveNodeList.size()>=1 && takeNodeList.size() >=1){
			//each step, eliminate at least 1 node in either give/take list and generate 1 dist result
			DistNode<K> curGiveDN = giveNodeList.get(0);
			DistNode<K> curTakeDN = takeNodeList.get(0);	
			dr = new DistResult<K>();
			dr.srcNodeId = curGiveDN.nodeId;
			dr.destNodeId = curTakeDN.nodeId;
			if (curGiveDN.numMoves > -1 * curTakeDN.numMoves){
				//give is more, so give what curTakes needs and remove curTake
				dr.numMoves = -1 * curTakeDN.numMoves;
				//dr.movingKeys.addAll(curGiveDN.keys.subList(0, dr.numMoves));
				dr.movingKeys.addAll(curGiveDN.getLastKeys(dr.numMoves));
				//keys are removed from give node
				curGiveDN.numMoves -= dr.numMoves;
				curGiveDN.keys.removeAll(dr.movingKeys);
				//although it is removed maintain the status is a good habit
				curTakeDN.numMoves = 0;
				curTakeDN.keys.addAll(dr.movingKeys);
				//the take node is removed
				takeNodeList.remove(0);
			}else if (curGiveDN.numMoves < -1 * curTakeDN.numMoves){
				//take is more, so give what curGive have and remove curGive
				dr.numMoves = curGiveDN.numMoves;
				dr.movingKeys.addAll(curGiveDN.getLastKeys(dr.numMoves));
				//
				curTakeDN.numMoves += dr.numMoves;
				curTakeDN.keys.addAll(dr.movingKeys);
				//
				curGiveDN.numMoves=0;
				curGiveDN.keys.removeAll(dr.movingKeys);
				//
				giveNodeList.remove(0);
			}else{//give equals take, remove both
				dr.numMoves = curGiveDN.numMoves;
				dr.movingKeys.addAll(curGiveDN.getLastKeys(dr.numMoves));
				//
				curTakeDN.numMoves += dr.numMoves;
				curTakeDN.keys.addAll(dr.movingKeys);
				//
				curGiveDN.numMoves -=dr.numMoves;
				curGiveDN.keys.removeAll(dr.movingKeys);
				//
				giveNodeList.remove(0);
				takeNodeList.remove(0);		
			}
			results.add(dr);
		}
		
		if (giveNodeList.size()==1){
			DistNode<K> curGiveDN = giveNodeList.get(0);
			dr = new DistResult<K>();
			dr.srcNodeId = curGiveDN.nodeId;
			dr.destNodeId = curGiveDN.nodeId;
			dr.numMoves = curGiveDN.numMoves;
			dr.movingKeys.addAll(curGiveDN.getLastKeys(dr.numMoves));
			curGiveDN.keys.removeAll(dr.movingKeys);
			results.add(dr);
		}
		
		logger.debug("list of moves:" + results);
		
		
		return results;
	}
	
	
}
