package org.cld.taskmgr.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.client.ClientNodeInf;

public class ServerKeepAliveThread extends Thread {
	private static Logger logger =  LogManager.getLogger(ServerKeepAliveThread.class);
	ServerNodeImpl sn;
	public ServerKeepAliveThread(ServerNodeImpl sn){
		this.sn = sn;
	}
	
	private boolean keepAlive(ClientNodeInf node){
		int maxRetry = 3;
		int i=0;
		for (i=0; i<maxRetry; i++){
			try {
				node.keepAlive(sn.getNC().getNodeId());
				return true;
			}catch(Throwable t){
				logger.info("retried:" + i,t);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("",e);
				}
			}
		}
		return false;
	}
	
	public void run(){
		while (true){
			try {
				Thread.sleep(5000);
				Map<String, ClientNodeInf> map = sn.getClients();
				//instead of using Iterator to prevent concurrent-modification-exception
				List<String> keyList = new ArrayList<String>(map.keySet());
				int maxRetry = 2;
				logger.info("clients size:" + keyList.size());
				for (int j=0; j<keyList.size(); j++){
					String key = keyList.get(j);
					ClientNodeInf node = map.get(key);
					if (node != null) {
						int i=0;
						for (i=0; i<maxRetry; i++){
							if (!keepAlive(node)){	
								logger.info("keep alive error.");
								if (!sn.localReRegister(key)){
									logger.info("re-register fail, keep alive fail, retry:" + i);
								}else{
									logger.info("re-register success, keep alive fail, retry:" + i);
								}
							}else{
								logger.info("keep alive success.");
								break;
							}
						}
						if (i == maxRetry){
							sn.unregister(sn.getNC().getNodeId(), key);
							logger.error("we lost node:" + key);
						}
					}else{
						//rare, the remove client occurs during key-clone and map.get
						sn.unregister(sn.getNC().getNodeId(), key);
						logger.error("xxx we lost node:" + key);
					}
				}
			}catch(Throwable t){
				logger.error("keep alive thread got error. continue...", t);
			}
		}
	}

}
