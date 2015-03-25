package org.cld.taskmgr.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.entity.Task;


/**
 * this remote interface is exposed to client/consumer from server/producer
 * @author Cheng Yi
 *
 */
public interface ServerNodeInf extends Remote {
	
	/*
	 * client un/register to server
	 */
	boolean register(String senderId, NodeConf nc) throws RemoteException;	
	boolean unregister(String senderId, String id) throws RemoteException;
	
	/*
	 * add/remove tasks to the cluster from client:senderId
	 */
	boolean addTasks(String senderId, Set<String> tkl) throws RemoteException;
	boolean removeTasks(String senderId, Collection<String> tkl) throws RemoteException;
	
	/**
	 * client to check whether the server has him in the list, if not, he will register himself.
	 * used in cases when server lost connection with client due to temporarily network issue, 
	 * and treated client as shutdown node, but actually client is alive
	 * @param senderId:client id
	 * @return: true for exists
	 * @throws RemoteException
	 */
	boolean keepAlive(String senderId) throws RemoteException;
	
	void reload() throws RemoteException;
}
