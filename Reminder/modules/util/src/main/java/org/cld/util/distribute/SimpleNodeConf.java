package org.cld.util.distribute;

public class SimpleNodeConf {
	
	private String nodeId;
	private int threadSize=0;
	private boolean isServer=false;
	
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public int getThreadSize() {
		return threadSize;
	}
	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}
	public boolean isServer() {
		return isServer;
	}
	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

}
