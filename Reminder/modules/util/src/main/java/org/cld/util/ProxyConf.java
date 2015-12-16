package org.cld.util;

import org.apache.commons.configuration.PropertiesConfiguration;

public class ProxyConf {
	public static final String KEY_USE_PROXY="use.proxy";
	public static final String KEY_HOST="proxy.ip";
	public static final String KEY_PORT="proxy.port";
	
	boolean useProxy;
	String host;
	int port;
	
	public ProxyConf(PropertiesConfiguration pc){
		useProxy = pc.getBoolean(KEY_USE_PROXY);
		host = pc.getString(KEY_HOST);
		port = pc.getInt(KEY_PORT);
	}
	
	public ProxyConf(boolean useProxy, String host, int port){
		this.useProxy = useProxy;
		this.host = host;
		this.port = port;
	}
	
	public boolean isUseProxy() {
		return useProxy;
	}
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	

}
