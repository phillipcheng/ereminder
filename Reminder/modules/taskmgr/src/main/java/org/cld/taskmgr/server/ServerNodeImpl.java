package org.cld.taskmgr.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.LocalTaskManager;
import org.cld.taskmgr.LocalTaskMgrInf;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.NodeConfPropChangedEvent;
import org.cld.taskmgr.NodeConfPropListener;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.client.ClientNodeInf;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ServerNodeImpl implements ServerNodeInf, NodeConfPropListener {
	private static Logger logger =  LogManager.getLogger(ServerNodeImpl.class);
	
	private ServerKeepAliveThread kt;
	private AppServerNodeInf asn;
	private ServerNodeInf producerStub = null; //hold it here to prevent GC
	private Registry serverRegistry;
	private boolean isLocal=false;
	private NodeConf nc;
	private SessionFactory taskMgrSF;

	private ConcurrentHashMap<String, ClientNodeInf> clients = new ConcurrentHashMap<String, ClientNodeInf>(); //
	
	public String toString(){
		String str = "ServerNodeImpl: nc:" + nc.toString();
		return str;
	}
	
	private PartitionDBMgr pdbmgr = new PartitionDBMgr(this);
	
	public AppServerNodeInf getASN(){
		return asn;
	}
	public SessionFactory getTaskMgrSF() {
		return taskMgrSF;
	}
	public void setTaskMgrSF(SessionFactory taskMgrSF) {
		this.taskMgrSF = taskMgrSF;
	}
	public ServerNodeImpl(NodeConf nc){
		this(nc, false);
	}
	
	public ServerNodeImpl(NodeConf nc, boolean local){
		this.nc = nc;
		
		this.isLocal = local;
		//1.
		
		//2.
		try {
			asn = (AppServerNodeInf) Class.forName(nc.getAppServerImpl()).newInstance();
		} catch (Exception e){
			logger.error("", e);
		}
		
		if (!local){
			//3.
			kt = new ServerKeepAliveThread(this);
		
		
			//5.
			//System.setSecurityManager(new SecurityManager());
			System.setProperty("java.rmi.server.codebase", nc.getRmiCodebase());
			System.setProperty("java.rmi.server.hostname", nc.getServerIP());
			
			startC();  
		}	
	}
	
	/**
	 * stop communication with the outside world
	 */
	public void startC(){
		try {
			producerStub = (ServerNodeInf) UnicastRemoteObject.exportObject(this, 0);		
			serverRegistry = LocateRegistry.createRegistry(nc.getServerPort());
			serverRegistry.rebind("Producer", producerStub); //async call might not bounded before client fetch.
		} catch (RemoteException e) {
			logger.error("", e);
		}
		logger.info("Server ready");
	}
	
	/**
	 * stop communication with the outside world
	 */
	public void stopC(){
		try{				
 		    serverRegistry.unbind("Producer");
 		    UnicastRemoteObject.unexportObject(this, true);
 		    UnicastRemoteObject.unexportObject(serverRegistry,true);  
		}catch(Throwable t) {
		   logger.error("exception.", t);
		}
		
	}
	
	
	/**
	 * 
	 * @param aconf: application conf
	 * @param clean: start by cleans the tasks?
	 */
	public void start(AppConf aconf, boolean clean){
		
		//0.
		nc.addNodeConfPropListener(this);
		//1. start keep alive
		kt.start();
		
		//2. setup task sf
		Configuration cfg = DBFactory.setUpCfg(nc.getNodeId(), TaskMgr.moduleName, nc.getDBConf());		
		taskMgrSF = DBFactory.setUpSF(nc.getNodeId(), TaskMgr.moduleName, cfg);
				
		if (clean){
			TaskPersistMgr.removeAllTasks(this.taskMgrSF);
		}
		
		//6. start app
		if (asn != null) {
			asn.start(this, aconf);
			nc.addNodeConfListener(aconf);
		}

	}
	
	public void stop(){		
		//2. stop dispatch thread
		
		//3. stop keep alive thread
		
		//4. stop C
		if (!isLocal){
			stopC();
		}
	}
	

	/////////////////////////
	/////remote server Inf
	//////////////////////////
	@Override
	public boolean register(String senderId, NodeConf nc) {
		logger.debug("register from:" + senderId);
		ClientNodeInf ni = Node.getConsumer(nc.getLocalIP(), nc.getLocalPort());
		if (clients.containsKey(nc.getNodeId())){
			return false;
		}
		clients.put(nc.getNodeId(), ni);
		//join with empty data-keys
		pdbmgr.join(senderId, nc);
		return true;
	}

	@Override
	public boolean unregister(String senderId, String id) {
		logger.debug("unregister from:" + senderId + ", to unregister:" + id);
		pdbmgr.quit(senderId, id);
		clients.remove(id);
		logger.error("unregister for:" + id + " finished, another kill can be issued.");
		return true;
	}

	@Override
	public boolean addTasks(String senderId, Set<String> tkl) {
		logger.debug("add Tasks, from:" + senderId + ", tasks:" + tkl);
		return pdbmgr.distributeKeySet(senderId, tkl);
	}

	@Override
	public boolean removeTasks(String senderId, Collection<String> tkl) {
		logger.debug("remove Tasks, from:" + senderId + ", tasks:" + tkl);
		removeTasksFromDB(tkl);
		return pdbmgr.removeKeySet(senderId, tkl);
	}
	
	@Override
	public boolean keepAlive(String senderId) throws RemoteException {
		logger.debug("Server Keep Alive ...: " + senderId);
		return clients.containsKey(senderId);
	}
	
	@Override
	public void reload() throws RemoteException{
		//let client has the updated task conf
		for (ClientNodeInf cni: clients.values()){
			cni.reload();
		}
		//then let server send them the tasks
		asn.reload();
		
		
	}
	///////////////////////////
	/////local Inf
	/////////////////////////////
	public Map<String, ClientNodeInf>  getClients() {
		return clients;
	}
	
	public ClientNodeInf getClient(String id){
		return clients.get(id);
	}
	
	public void localRegisterNoDistribute(String senderId, NodeConf nc, Set<Task> curKeys) {
		logger.debug("local register no distribute from:" + senderId);
		//clone all these pass by reference params for local calls, since these are assumed to be remote calls
		Set<String> tkl = TaskUtil.getKeySet(curKeys);
		NodeConf clonedNC = nc.clone();
		pdbmgr.addNode(clonedNC, tkl);
	}
	
	public boolean localRegister(String senderId, NodeConf nc, Set<Task> curKeys) {
		logger.debug("local register from:" + senderId);
		Set<String> tkl = TaskUtil.getKeySet(curKeys);
		NodeConf clonedNC = nc.clone();
		return pdbmgr.join(senderId, clonedNC, tkl, false, PartitionDBMgr.OP_JOIN);
	}
	
	public boolean localUnregister(String senderId, String id) {
		logger.debug("local unregister from:" + senderId + ", to unregister:" + id);
		pdbmgr.quit(senderId, id, false);
		return true;
	}
	
	//for testing PDBMgr purpose only
	public boolean localDistributeKeyList(Set<Task> tl){
		Set<String> tkl = TaskUtil.getKeySet(tl);
		return pdbmgr.localDistributeKeySet(tkl);
	}

	public boolean removeTasksFromDB(Collection<String> tkl) {
		return TaskPersistMgr.removeTasksById(taskMgrSF, tkl);
	}
	
	public void moveDBTasksToMe(List<String> tkl){
		TaskPersistMgr.moveTasks(taskMgrSF, tkl, nc.getNodeId());
	}
	

	//re-register with node-id, used when keep-alive found one client is unavailable
	public boolean localReRegister(String key) {
		NodeConf nodeNC = pdbmgr.getNC(key);
		if (nodeNC != null){
			ClientNodeInf ni = Node.getConsumer(nodeNC.getLocalIP(), nodeNC.getLocalPort());
			if (ni != null){
				clients.put(nodeNC.getNodeId(), ni);
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/////////////////////////
	/////getter and setter
	//////////////////////////
	public NodeConf getNC(){
		return nc;
	}
	
	public PartitionDBMgr getPDBMgr(){
		return pdbmgr;
	}

	//////////////
	//configure changed 
	///////////////////
	private void handleAddTask(Task t){
		//save to db
		List<Task> tl = new ArrayList<Task>();
		tl.add(t);
		TaskPersistMgr.addTasks(taskMgrSF, tl);
		//add new task
		Set<String> tks = TaskUtil.getKeySet(tl);
		logger.info("add task:" + t);
		addTasks(nc.getNodeId(), tks);		
	}
	
	private void handleRemoveTask(String tn){
		//remove all instances of this task and it's next's
		List<String> ids = TaskPersistMgr.getTaskIds(taskMgrSF, tn, 0, 0);
		logger.info("remove tasks:" + ids + " for task:" + tn);
		removeTasks(nc.getNodeId(), ids);		
	}
	@Override
	public void nodeConfPropChanged(NodeConfPropChangedEvent ncpce) {	
		logger.info("get conf prop change event:" + ncpce);
		if (TaskMgr.taskName_Key.equals(ncpce.getPropName())){
			if (NodeConfPropChangedEvent.OP_ADD==ncpce.getOpType()){
				handleAddTask((Task) ncpce.getObjectValue());
			}
			
			if (NodeConfPropChangedEvent.OP_REMOVE==ncpce.getOpType()){
				//remove all instances of this task and it's next's
				handleRemoveTask(ncpce.getStrValue());		
			}
			
			if (NodeConfPropChangedEvent.OP_UPDATE == ncpce.getOpType()){
				Task oldT = (Task) ncpce.getOldObjValue();
				handleRemoveTask(oldT.getName());
				handleAddTask((Task) ncpce.getObjectValue());
			}
		}
	}
}
