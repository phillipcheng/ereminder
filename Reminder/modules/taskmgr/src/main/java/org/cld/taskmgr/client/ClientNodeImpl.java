package org.cld.taskmgr.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.BatchTaskOperation;
import org.cld.taskmgr.LocalTaskManager;
import org.cld.taskmgr.Node;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.NodeConfPropChangedEvent;
import org.cld.taskmgr.NodeConfPropListener;
import org.cld.taskmgr.TaskOperation;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.ServerNodeInf;

public class ClientNodeImpl implements ClientNodeInf, NodeConfPropListener {

	private static Logger logger =  LogManager.getLogger(ClientNodeImpl.class);
	
	public static final int Q_CMD_ADD=1;
	public static final int Q_CMD_DEL=2;	

	private boolean needDispatch = false; //flag to dispatch tasks
	private ServerNodeInf serverNode;
	private int connState= ClientKeepAliveThread.CN_STATE_NOT_CONNECTED;

	private AppClientNodeInf acn; //
	
	private ClientKeepAliveThread kt;
	
	private ClientNodeInf consumerStub = null; //hold it here to prevent GC
	private Registry clientRegistry;
	private LocalTaskManager localTaskManager;
	private NodeConf nc;
	
	public NodeConf getNC(){
		return nc;
	}
	public ServerNodeInf getSNI(){
		return serverNode;
	}
	public AppClientNodeInf getACN(){
		return acn;
	}
	public int getConnState() {
		return connState;
	}
	public void setConnState(int connState) {
		this.connState = connState;
	}
	public LocalTaskManager getTaskInstanceManager(){
		return localTaskManager;
	}
	
	public ClientNodeImpl(NodeConf nc){
		this.nc = nc;
		
		localTaskManager = new LocalTaskManager(nc);
		
		//System.setSecurityManager(new SecurityManager());
		if (nc.getRmiCodebase()!=null){
			System.setProperty("java.rmi.server.codebase", nc.getRmiCodebase());
		}
		if (nc.getLocalIP()!=null)
			System.setProperty("java.rmi.server.hostname", nc.getLocalIP());
		
		//2.
		try {
			this.acn = (AppClientNodeInf) Class.forName(nc.getAppClientImpl()).newInstance();
		}catch (Exception e){
			logger.error("", e);
		}

		//3.
		localTaskManager.setup(acn, nc);
		
		//4.
		kt = new ClientKeepAliveThread(this);
	}
	

	public boolean retryProducer(){
		logger.debug("serverIP:" + nc.getServerIP() + ";serverPort:" + nc.getServerPort());
	    serverNode = Node.getProducer(nc.getServerIP(), nc.getServerPort());
		if (serverNode == null){
			logger.error("failed to get the producer");
			return false;
		}
		else{
			logger.error("get producer success.");
			return true;
		}
	}
	
	
	/**
	 * stop communication with the outside world
	 */
	public void startC(){
		try {
			consumerStub = (ClientNodeInf) UnicastRemoteObject.exportObject(this, 0);		
		    clientRegistry = LocateRegistry.createRegistry(nc.getLocalPort());
		    clientRegistry.rebind("Consumer", consumerStub);
		} catch (RemoteException e) {
			logger.error("", e);
		}
		
	}	
	

	/**
	 * stop communication with the outside world
	 */
	public void stopC(){
		try {				
 		    clientRegistry.unbind("Consumer");
 		    UnicastRemoteObject.unexportObject(this, true);
 		    UnicastRemoteObject.unexportObject(clientRegistry,true);  
		}catch(Throwable t) {
			logger.error("exception.", t);
		}
	}
	
	
	public void start(AppConf aconf){
		//0.
		nc.addNodeConfPropListener(this);
		
		//2. remove all tasks (these are dead tasks, since last time force shutdown
		TaskPersistMgr.removeMyTasks(localTaskManager.getTaskSF(), nc.getNodeId());
		
		//3. start application, should before register (which will receive tasks)
		if (acn != null){
			acn.start(this, aconf);
			nc.addNodeConfListener(aconf);
		}
		
		//5. start keep alive thread
		kt.start();	
		
	}
	
	public void stop(){
		//1. stop keep alive thread
		kt.stopMe();
		
		//2. stop application, do not know how to stop running threads
		//acn.stop(this);
		
		//3. unregister
		try {
			serverNode.unregister(nc.getNodeId(), nc.getNodeId());
		}catch (RemoteException e1) {
			logger.error("", e1);
		}
		
		//4. stop app db, can't tear this down, 
		//since i donot know how to stop the threads, the running threads need db access
		//DBFactory.tearDown(nc);		
		
		//
	}
	
	@Override
	public boolean keepAlive(String senderId) {
		logger.debug("Client Keep Alive ...: " + senderId);
		return true;
	}

	@Override
	public void nodeConfPropChanged(NodeConfPropChangedEvent ncpce) {
		localTaskManager.nodeConfPropChanged(ncpce);
		
		//
		
	}

	@Override
	public boolean clientAddTasks(List<String> tkl) throws RemoteException {
		return localTaskManager.clientAddTasks(tkl);
	}

	@Override
	public boolean clientRemoveTasks(List<String> tkl) throws RemoteException {
		boolean ret = localTaskManager.clientRemoveTasks(tkl);
		return ret;
	}
	
	@Override
	public void reload() throws RemoteException{
		logger.warn("reload called for:" + nc.getNodeId());
		acn.reload();
	}
}
