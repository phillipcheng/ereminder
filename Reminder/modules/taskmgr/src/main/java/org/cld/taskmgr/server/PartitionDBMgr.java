package org.cld.taskmgr.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ListUtil;
import org.cld.util.distribute.DistResult;
import org.cld.util.distribute.DistributeKeys;
import org.cld.util.distribute.SimpleNodeConf;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.client.ClientNodeInf;


/**
 *
 *
 * @param <Task>: the key type of item to be partitioned, for example Task
 */
public class PartitionDBMgr {

	private static Logger logger =  LogManager.getLogger(PartitionDBMgr.class);	
	
	public static final int OP_REMOVE=1;
	public static final int OP_JOIN=2;
	public static final int OP_DISTRIBUTE=3;
	public static final String DISTRIBUTE_NODE="cld.DistributeNode";
	
	//nodeConfs contain all the nodes (including server)
	private ConcurrentHashMap<String, NodeConf> nodeConfs = new ConcurrentHashMap<String, NodeConf>(); //
	//dataKeys contain the current distribution of the tasks
	private SortedMap<String, Set<String>> dataKeys = new TreeMap<String, Set<String>>(); //node id to task keys
	
	private ServerNodeImpl server;
	
	public PartitionDBMgr(){
	}
	
	public PartitionDBMgr(ServerNodeImpl server){
		this.server = server;
	}
	
	public NodeConf getNC(String nodeId){
		return nodeConfs.get(nodeId);
	}
	
	public Map<String, Set<String>> getDataKeys(){
		return dataKeys;
	}
	
	private String dataKeysToSimpleString(){
		String res = "\n";
		Iterator<String> itdk=dataKeys.keySet().iterator();
		while (itdk.hasNext()){
			String clientid = itdk.next();
			Set<String> kl = dataKeys.get(clientid);
			res = res + clientid + ":" + kl.size() + "\n";
		}
		return res;
	}
	
	//return the node-id managing the key
	private String contains(String ke){
		Iterator<String> itdk=dataKeys.keySet().iterator();
		while (itdk.hasNext()){
			String clientid = itdk.next();
			Set<String> kl = dataKeys.get(clientid);
			if (kl.contains(ke)){
				return clientid;
			}
		}
		return null;
	}

	protected boolean addNode(NodeConf nc, Set<String> TL){
		if (!nodeConfs.containsKey(nc.getNodeId())){
			nodeConfs.put(nc.getNodeId(), nc);
			//preventing multi-nc has the same reference			
			dataKeys.put(nc.getNodeId(), TL);
			return true;
		}else{
			return false;
		}
	}
	
	//only for data moving operations, not used to calculate distribution
	protected void addDistributeData(Set<String> TL){
		dataKeys.put(DISTRIBUTE_NODE, TL);
	}
	protected void removeDistributeData(){
		dataKeys.remove(DISTRIBUTE_NODE);
	}
	
	private boolean removeNode(String nodeId){
		nodeConfs.remove(nodeId);
		dataKeys.remove(nodeId);
		return true;
	}
	
	public synchronized boolean removeKeySet(String senderId, Collection<String> kl){
		
		boolean isRemote = senderId.equals(server.getNC().getNodeId());
		
		Iterator<String> it = kl.iterator();
		while (it.hasNext()){
			String t = it.next();
			String nodeId = contains(t);
			if (nodeId == null){
				logger.error("task not found when remove." + t);
			}else{
				boolean physicalFailed = false;
				if (isRemote){
					//physical remove
					ClientNodeInf cni = server.getClient(nodeId);
					List<String> tkl = new ArrayList<String>();
					tkl.add(t);
					try {
						cni.clientRemoveTasks(tkl);
					} catch (RemoteException e) {
						logger.error("error when remove client task.", e);
						physicalFailed = true;
					}
				}
				if (!physicalFailed){
					//logical remove
					dataKeys.get(nodeId).remove(t);
				}				
			}
		}
		logger.info("confClients:\n" + nodeConfs);
		logger.debug("datakeys:\n" + dataKeys + "\n");
		logger.info("datakeys:" + dataKeysToSimpleString());
		return true;		
	}
	
	//distribute the new keylist to all nodes according to the capacity distribution from server-node
	public synchronized boolean distributeKeySet(String senderId, Set<String> kl){
		NodeConf nc = nodeConfs.get(senderId);
		if (nc != null){
			return join(senderId, nc, kl, true, OP_DISTRIBUTE);
		}else{
			return false;
		}
	}
	
	//for PDBMgr distribution algorithm testing purpose
	public synchronized boolean localDistributeKeySet(Set<String> kl){		
		return join(server.getNC().getNodeId(), server.getNC(), kl, false, OP_DISTRIBUTE);
	}
	
	public synchronized boolean quit(String senderId, String nodeId, boolean isRemote){
		if (nodeConfs.containsKey(nodeId) && dataKeys.containsKey(nodeId)){
			NodeConf nc = nodeConfs.get(nodeId);
			Set<String> kl = dataKeys.get(nodeId);
			if (nc!=null && kl != null){
				int orginTS = nc.getThreadSize();
				nc.setThreadSize(0);
				join(senderId, nc, kl, isRemote, OP_REMOVE);		
				nc.setThreadSize(orginTS);
				return true;
			}else{
				logger.error("node not found:" + nodeId);
				return false;
			}
		}
		return false;
	}
	
	/**
	 * @param senderId: the nodeId who send this command
	 * @param nodeId: the node to quit
	 */
	public synchronized boolean quit(String senderId, String nodeId){
		return quit(senderId, nodeId, true);
	}
	
	/**
	 * @param senderId
	 * @param nc: the configuration of the node to join
	 * @return
	 */
	public synchronized boolean join(String senderId, NodeConf nc){		
		return join(senderId, nc, new LinkedHashSet<String>(), true, OP_JOIN);
	}
	
	/**
	 * @param senderId
	 * @param nc: the configuration of the node to join
	 * @param keys
	 * @return
	 */
	public synchronized boolean join(String senderId, NodeConf nc, Set<String> keys){		
		return join(senderId, nc, keys, true, OP_JOIN);
	}
	
	/**
	 * try to rebalance globally
	 * rebalance all tasks according to the capacity distribution
	 * note: moving will occur between non-input nodes
	 * 
	 * Life cycle:
	 * server found the tasks  
	 * --> when there is no client nodes, server put them on the "server queue" identified by the nc (called 1st-distribute)
	 * 
	 * when client joined, from server-queue (server node) to client nodes.
	 * 
	 * when the last client quits, all back to the server node. (called lastRemove)
	 * 
	 * can be called only by
	 * 1. join/register
	 * 2. remove (can't be server node)
	 * 3. distribute (only be server-gen node)
	 * 
	 * @param senderId
	 * @param nc: the node to join the cluster
	 * @param keys: the keys to join the cluster
	 * @param isRemote: true: needs move task to other nodes, false: not used in production, only for PDBMgr testing
	 * @param isRemove
	 * @return
	 */
	public synchronized boolean join (String senderId, NodeConf nc, Set<String> keys, boolean isRemote, int opType){	
		
		logger.info("join: sender:" + senderId + ", nodeId:" + nc.getNodeId() + ", keys:" + keys.size() + 
				", isRemote:" + isRemote + ", opType:" + opType);
		
		//for restore, too tricky, very bad
		int orgServerThreadSize = 0;
		if (nodeConfs.containsKey(server.getNC().getNodeId())){
			orgServerThreadSize =  nodeConfs.get(server.getNC().getNodeId()).getThreadSize();
			//set server thread size to 0 means server will not take tasks if there is client
			nodeConfs.get(server.getNC().getNodeId()).setThreadSize(0);
		}
		
		//this is a last remove (after remove only 1 server node left) 
		if (nodeConfs.size()==2 && (opType == OP_REMOVE) && 
				nodeConfs.containsKey(nc.getNodeId()) && nodeConfs.containsKey(server.getNC().getNodeId())){
			//before remove, change server node capacity to 1, let server to host all tasks, trick very bad
			nodeConfs.get(server.getNC().getNodeId()).setThreadSize(1);
		}
		
		//1st distribute (from server-find to server queue, distribute when there is only 1 server node)
		if (nodeConfs.size()==1 && (opType == OP_DISTRIBUTE) && 
				nodeConfs.containsKey(server.getNC().getNodeId()) && senderId.equals(server.getNC().getNodeId())){
			//before join, change server node capacity to 1, let server to host all tasks, trick very bad
			nodeConfs.get(server.getNC().getNodeId()).setThreadSize(1);
		}
		
		//1st register with tasks
		if (nodeConfs.size()==0 && (opType== OP_JOIN) && keys.size()!=0){
			Set<String> emptyTL = new HashSet<String>();
			join (senderId, nc, emptyTL, false, OP_JOIN);
			join (senderId, nc, keys, false, OP_DISTRIBUTE);
			return true;
		}
		
		//1. calculate the new distribution
		if (opType == OP_JOIN)
			//add both capacity and data
			addNode(nc, keys);
		else if (opType == OP_DISTRIBUTE){
			//add only data
			this.addDistributeData(keys);
		}
		
		Map<String, SimpleNodeConf> sncMap = TaskUtil.getSimpleNodeConfs(nodeConfs);
		
		
		//add distribute as a system node, it should move all its keys to the cluster
		if (opType == OP_DISTRIBUTE){
			SimpleNodeConf snc = new SimpleNodeConf();
			snc.setNodeId(DISTRIBUTE_NODE);
			snc.setServer(false);
			snc.setThreadSize(0);
			sncMap.put(DISTRIBUTE_NODE, snc);
			dataKeys.put(DISTRIBUTE_NODE, keys);
		}
		
		DistributeKeys<String> distKeys = new DistributeKeys<String>();
		
		List<DistResult<String>> results = distKeys.rebalance(senderId, sncMap, dataKeys);
		
		
		//2. apply the result		
		for (int i=0; i<results.size(); i++){
			DistResult<String> dr = results.get(i);
			boolean physicalRes=false;
			if (isRemote){
				//simulated transaction [physical and logical moving]
				physicalRes = moveDataPhysical(dr);
				if (physicalRes)
					moveDataLogical(dr);
			}else{
				moveDataLogical(dr);
			}
		}
		
		//3. clean up
		if (opType==OP_REMOVE) {
			//called from quit, to remove client
			removeNode(nc.getNodeId());
		}else if (opType == OP_DISTRIBUTE){
			removeDistributeData();
		}
		
		//after this turn server node capacity back to original thread size
		if (nodeConfs.containsKey(server.getNC().getNodeId())){
			//tricky very bad
			nodeConfs.get(server.getNC().getNodeId()).setThreadSize(orgServerThreadSize);
		}
		
		logger.info("confClients:\n" + nodeConfs);
		logger.debug("datakeys:\n" + dataKeys + "\n");
		logger.info("datakeys:" + dataKeysToSimpleString());
		
		return true;
	}
	
	//moving the data logically on the server node
	private void moveDataLogical(DistResult<String> dr){
		logger.debug("perform this logical moving:\n" + dr + "\n");
		Set<String> srcKeys = dataKeys.get(dr.srcNodeId);
		Set<String> destKeys = dataKeys.get(dr.destNodeId);
		try {
			//ask FROM_NODE to give TO_NODE the movingKeys
			if (dr.numMoves > 0){
				//
				srcKeys.removeAll(dr.movingKeys);
				destKeys.addAll(dr.movingKeys);
			}else if (dr.numMoves < 0){
				//
				destKeys.removeAll(dr.movingKeys);
				srcKeys.addAll(dr.movingKeys);
			}
		}catch(Throwable t){
			logger.error("", t);
		}		
	}
	
	//moving the data physically on the server node
	private boolean moveDataPhysical(DistResult<String> dr){
		logger.debug("perform this physical moving:" + dr + "\n");
		//the clients list might not contain the node if it is a server node or distribution node
		//for distribution node: no operation needed for physical operation
		//for server node: db update is executed to record the tasks assigned to server (which means not dispatched to other nodes)
		ClientNodeInf srcNode = null;
		ClientNodeInf destNode = null;
		
		srcNode = server.getClient(dr.srcNodeId);
		destNode = server.getClient(dr.destNodeId);
		try {
			//ask FROM_NODE to give TO_NODE the movingKeys
			if (dr.numMoves > 0){
				//moving from src to dest
				if (srcNode != null){
					//from 1 client
					logger.debug("keys to remove from " + dr.srcNodeId + ":" + " size:" + dr.movingKeys.size() + ":" + dr.movingKeys);
					
					batchedRemoveKeys(srcNode, dr.movingKeys);
				}else{
					if (DISTRIBUTE_NODE.equals(dr.srcNodeId)){
						//from server-find/distribution node
					}else{
						//from server node/server queue
						
						//do not remove tasks in server, optimized for having the tasks when cluster starts
						//server.localRemoveTasks(dr.movingKeys);
					}
				}
				
				if (destNode != null){
					//to 1 client
					logger.debug("keys to move to: " + dr.destNodeId + ":" + " size:" + dr.movingKeys.size() + ":" + dr.movingKeys);
					
					batchedAddKeys(destNode, dr.movingKeys);
					
				}else{
					if (DISTRIBUTE_NODE.equals(dr.destNodeId)){
						//to server-find/distribution node, not possible
					}else {
						//to server node/server queue
						server.moveDBTasksToMe(dr.movingKeys);
					}
				}
			}else if (dr.numMoves < 0){
				if (destNode != null){
					//from 1 client
					logger.debug("keys to remove from " + dr.destNodeId + ":" + " size:" + dr.movingKeys.size() + ":" + dr.movingKeys);
					
					batchedRemoveKeys(destNode, dr.movingKeys);
				}else{
					if (DISTRIBUTE_NODE.equals(dr.destNodeId)){
						//from server-find/distribution node
					}else{
						//from server node/server queue
						
						//do not remove tasks in server, optimized for having the tasks when cluster starts
						//server.localRemoveTasks(dr.movingKeys);
					}
				}
				
				if (srcNode != null){
					//to 1 client
					logger.debug("keys to move to: " + dr.srcNodeId + ":" + " size:" + dr.movingKeys.size() + ":" + dr.movingKeys);
					
					batchedAddKeys(srcNode, dr.movingKeys);
					
				}else{
					if (DISTRIBUTE_NODE.equals(dr.srcNodeId)){
						//to server-find/distribution node, not possible
					}else{
						//to server node/server queue
						server.moveDBTasksToMe(dr.movingKeys);
					}
				}
			}
			return true;
		}catch(Throwable t){
			logger.error("", t);
			return false;
		}		
	}
	
	public static final int BATCH_SIZE=200;
	
	private void batchedAddKeys(ClientNodeInf cni, List<String> tkl){		
		List<List<?>> lists = ListUtil.generateBatches(tkl, BATCH_SIZE);
		for (int i=0; i<lists.size(); i++){
			//change from the default none-serializable Random Access List to serializable ArrayList
			List<?> oneList = lists.get(i);
			List<String> goList = new ArrayList<String>();
			for (int j=0; j<oneList.size(); j++){
				goList.add((String)oneList.get(j));
			}
			int maxRetry = 3;
			int retry = 0;
			while (retry<maxRetry){
				try {
					cni.clientAddTasks(goList);
					break;
				} catch (RemoteException e) {
					retry ++;
					logger.info("exception while adding tasks:" + oneList, e);
				}
			}
			if (retry == maxRetry){
				logger.error("failed to adding tasks to:" + cni + " after retry:" + maxRetry);
			}
		}
	}
	
	private void batchedRemoveKeys(ClientNodeInf cni, List<String> tkl){		
		List<List<?>> lists = ListUtil.generateBatches(tkl, BATCH_SIZE);
		for (int i=0; i<lists.size(); i++){
			//change from the default none-serializable Random Access List to serializable ArrayList
			List<?> oneList = lists.get(i);
			List<String> goList = new ArrayList<String>();
			for (int j=0; j<oneList.size(); j++){
				goList.add((String)oneList.get(j));
			}
			List<String> goKeyList = new ArrayList<String>();
			for (int j=0; j<goList.size(); j++){
				goKeyList.add(goList.get(j));
			}
			int maxRetry = 3;
			int retry = 0;
			while (retry<maxRetry){
				try {
					cni.clientRemoveTasks(goKeyList);
					break;
				} catch (RemoteException e) {
					retry ++;
					logger.info("exception while removing tasks:" + oneList, e);
				}
			}
			if (retry == maxRetry){
				logger.error("failed to removing tasks from:" + cni + " after retry:" + maxRetry);
			}
		}
	}
	
}
