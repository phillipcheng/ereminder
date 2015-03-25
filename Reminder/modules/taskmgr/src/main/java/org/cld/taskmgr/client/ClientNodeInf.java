package org.cld.taskmgr.client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.cld.taskmgr.entity.Task;



/**
 * this remote interface is exposed from any node
 * @author Cheng Yi
 *
 */
public interface ClientNodeInf extends Remote{
	
	/*
	 * assign those tasks to this client
	 */
	boolean clientAddTasks(List<String> tkl) throws RemoteException;
	
	/*
	 * un-assign those tasks from this client
	 */
	boolean clientRemoveTasks(List<String> tkl) throws RemoteException;
	
	
	boolean keepAlive(String senderId) throws RemoteException;
	
	/**
	 * Reload
	 * @throws RemoteException
	 */
	void reload() throws RemoteException;
		
}
