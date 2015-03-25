package org.cld.taskmgr.client;

import java.rmi.RemoteException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.NodeConf;
import org.cld.taskmgr.server.ServerNodeInf;

public class ClientKeepAliveThread extends Thread {
	private static Logger logger =  LogManager.getLogger(ClientKeepAliveThread.class);
	ClientNodeImpl cn;
	NodeConf nc;
	
	public static final int CN_STATE_NOT_CONNECTED=0; //not connected to server
	public static final int CN_STATE_CONNECTED=1; //connected to server, but not registered to server
	
	private int preState = CN_STATE_NOT_CONNECTED;
	
	public static final int LONG_WAIT=5000;
	public static final int SHORT_WAIT=100;
	
	private int waitTime = SHORT_WAIT;
	private boolean stop = false;
	
	public ClientKeepAliveThread(ClientNodeImpl cn){
		this.cn = cn;
		this.nc = cn.getNC();
	}
	
	public void stopMe(){
		stop = true;
	}
	
	public void run(){
		while (!stop){
			preState = cn.getConnState();
			if (preState == CN_STATE_NOT_CONNECTED){
				cn.startC();
				
				cn.retryProducer();
				
				ServerNodeInf node = cn.getSNI();
				if (node != null) {
					try {		
						if (!node.register(nc.getNodeId(), nc)){
							logger.error("failed to register. duplicated node id." + nc.getNodeId());
							return;
						}else{
							cn.setConnState(CN_STATE_CONNECTED);
						}
					} catch (RemoteException e1) {
						logger.error(nc.getNodeId() + " register failed. retry later." , e1);
						cn.stopC();
					}
				}else{
					logger.error("Server not found, retry later.");
					cn.stopC();
				}
			}else if (preState == CN_STATE_CONNECTED){
				ServerNodeInf node = cn.getSNI();
				if (node != null) {					
					try {
						if (node.keepAlive(nc.getNodeId())){
						}else{
							cn.setConnState(CN_STATE_NOT_CONNECTED);
							cn.stopC();
						}
					}catch(Throwable t){
						cn.setConnState(CN_STATE_NOT_CONNECTED);
						cn.stopC();
					}
				}else{
					logger.fatal("Internal error.");
				}
			}else{
				logger.error("unknown state:" + preState + ".");
				return;
			}
			
			if (cn.getConnState()!=preState){
				waitTime = SHORT_WAIT;
			}else{
				waitTime = LONG_WAIT;
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					logger.error("Interrupted exception.", e);
				}
			}
			
			
			
			//logger.info("current state:" + cn.getConnState());
			//logger.info("current wait time:" + waitTime);
			
		}
		
		logger.info("keep alive stopped." + nc.getNodeId());
		cn.stopC();
	}

}
