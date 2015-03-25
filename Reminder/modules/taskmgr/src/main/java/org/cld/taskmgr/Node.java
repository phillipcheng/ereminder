package org.cld.taskmgr;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.client.AppHadoopClientNodeInf;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.client.ClientNodeInf;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.cld.taskmgr.server.ServerNodeInf;

public class Node {
	private static Logger logger =  LogManager.getLogger(Node.class);
	
	private NodeConf nc = null;
	private AppConf aconf = null;
	private String propFile = null;
	
	public NodeConf getNodeConf(){
		return nc;
	}
	
	public AppConf getAppConf(){
		return aconf;
	}
	
	public Node(){
		this("dd.properties");
	}
	
	private ServerNodeImpl producerStubImpl = null; //hold it here to prevent GC??
	private ClientNodeImpl consumerStubImpl = null; //hold it here to prevent GC ??
	
	public Node(String prop){
		this.propFile = prop;
		nc = new NodeConf(prop);
		try {
			aconf = (AppConf) Class.forName(nc.getAppConfImpl()).newInstance();
			aconf.setup(prop, nc);
		} catch (Exception e) {
			logger.error("", e);
		}	
	}
	
	/**
	 * 
	 * @param clean: true: means clean all the task data store
	 */
	public void start(boolean clean){
		if (NodeConf.tmframework_old.equals(nc.getTaskMgrFramework())){
			if (nc.isServer()){
				try {
					producerStubImpl = new ServerNodeImpl(nc);			    
				    //start the node
				    producerStubImpl.start(aconf, clean);	    
				} catch (Throwable t) {
				   logger.error("exception.", t);
				}
			}else{
				try {
					consumerStubImpl = new ClientNodeImpl(nc);	
					//start the node
					consumerStubImpl.start(aconf);
				}catch(Throwable t){
					logger.error("Throwable caught in main. continue.", t);
				}
			}
		}else{
			try {
				AppHadoopClientNodeInf hadoopClientNode = (AppHadoopClientNodeInf) Class.forName(nc.getAppHadoopClientImpl()).newInstance();
				hadoopClientNode.start(aconf, propFile);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				logger.error("", e);
			}
		}
	}
	
	public void stop(){
		if (nc.isServer()){
			try {
				if (producerStubImpl!=null){
					producerStubImpl.stop();
				}
			    
			} catch (Throwable t) {
			   logger.error("exception.", t);
			}
		}else{
			try {
				if (consumerStubImpl!=null){
					consumerStubImpl.stop();
				}
			}catch(Throwable t){
				logger.error("Throwable caught in main. continue.", t);
			}
		}
	}
	
	public static ServerNodeInf getProducer(String serverIP, int serverPort){
		Registry serverRegistry = null;
		ServerNodeInf producer = null;
		boolean waitBounded=true;
		while (waitBounded){
			try {
				serverRegistry = LocateRegistry.getRegistry(serverIP, serverPort);
				producer = (ServerNodeInf) serverRegistry.lookup("Producer");
				waitBounded=false;
			} catch (AccessException e) {
				logger.error("", e);
				waitBounded=false;
			} catch (RemoteException e) {
				logger.error("", e);
				waitBounded=false;
			} catch (NotBoundException e) {
				logger.error("", e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					logger.error("", e1);
				}
				//but still wait
			}
		}
		return producer;
	}
	
	public static ClientNodeInf getConsumer(String clientIP, int clientPort){
		Registry clientRegistry = null;
		ClientNodeInf consumer = null;
		boolean waitBounded=true;
		while (waitBounded){
			try {
				clientRegistry = LocateRegistry.getRegistry(clientIP, clientPort);
				consumer = (ClientNodeInf) clientRegistry.lookup("Consumer");
				waitBounded=false;
			} catch (AccessException e) {
				logger.warn("consumer can't be accessed.", e);
				waitBounded=false;
			} catch (RemoteException e) {
				logger.warn("get consumer remote exception.", e);
				waitBounded=false;
			} catch (NotBoundException e) {
				logger.warn("consumer not bounded.", e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					logger.error("", e1);
				}
				//but still wait
				//TODO set max wait
			}
		}
		
		try {
			if (consumer != null)
				if (consumer.keepAlive("server")){
					return consumer;
				}else{
					return null;
				}
			else
				return null;
		} catch (RemoteException e) {
			logger.error("remote exception while call keepalive after getConsumer", e);
			return null;
		}
	}
	
	public ServerNodeImpl getServer(){
		return producerStubImpl;
	}
	
	public ClientNodeImpl getClient(){
		return consumerStubImpl;
	}
}
