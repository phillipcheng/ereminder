package org.cld.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;


public class RSHttpClient {
	
	//host configuration
	protected boolean useProxy=false;
	protected String proxyHost;
	protected int proxyPort;
	
	//client parameters
	protected int timeout;
	protected MultiThreadedHttpConnectionManager connectionManager;
	
	
	public RSHttpClient(int timeout){
		this.timeout = timeout*1000;
		connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.getParams().setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, 50);
		connectionManager.getParams().setMaxTotalConnections(60);
	}
	
	//timeout in seconds
	public RSHttpClient(String proxyHost, int proxyPort, int timeout){
		this(timeout);
		this.useProxy=true;
		this.proxyHost=proxyHost;
		this.proxyPort = proxyPort;
	}
	
	public HttpClient getHttpClient(){
		HttpClient httpclient = new HttpClient(connectionManager);
		if (useProxy){
			HostConfiguration config = httpclient.getHostConfiguration();
	        config.setProxy(this.proxyHost, this.proxyPort);
		}
        HttpClientParams params = httpclient.getParams();
        params.setConnectionManagerTimeout(timeout);
        params.setSoTimeout(timeout);
        return httpclient;
	}
	
	protected String getEncodedURL(String url){
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return url;
		}
	}
}
