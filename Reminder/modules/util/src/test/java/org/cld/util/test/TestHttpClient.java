package org.cld.util.test;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.RSHttpClient;
import org.junit.Test;

public class TestHttpClient {
	//private static final String proxyhost="192.168.2.7";
	private static final String proxyhost="52.1.96.115";
	private static final int proxyport=8080;
	private static final int timeout=30;
	
	private static String HEADER_CMD = "command";
	private static String HEADER_CMDVAL_START = "start";
	private static String HEADER_CMDVAL_STOP = "stop";
	private static String HEADER_SESSIONID = "dsessionid";
	private static String HEADER_USERID = "userid";
	private static String HEADER_REASON = "rejectreason";
	
	private static String REASON_VAL_SUCCESS="command succeed";
	private static String REASON_VAL_NOREQHEAD="no req head";
	private static String REASON_VAL_REQHEAD_NOUSERIP="no user/ip in the start request header";
	private static String REASON_VAL_NOUSER="no such user";
	private static String REASON_VAL_NOBAL="no balance";
	private static String REASON_VAL_NOIPSESSION="no ip session";
	private static String REASON_VAL_USERONLINE="user already online";
	private static String REASON_VAL_IPINUSE="ip already in use";
	private static String REASON_VAL_NORSPHEAD="no rsp head";
	private static String REASON_VAL_UNKNOWN="unknown";
	
	private static final String START_URL="http://www.google.com";
	
	public Logger logger = LogManager.getLogger(TestHttpClient.class);
	
	RSHttpClient httpClient = new RSHttpClient(proxyhost, proxyport, timeout);
	
	//return sessionId or NULL for error
	public String startSession(String userId){
		HttpMethod method = new GetMethod(START_URL);
		try {
			method.setRequestHeader(HEADER_CMD, HEADER_CMDVAL_START);
			method.setRequestHeader(HEADER_USERID, userId);
	        httpClient.getHttpClient().executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
            	Header header = method.getResponseHeader(HEADER_SESSIONID);
            	if (header!=null){
                    logger.info(String.format("start ok. session id got:%s", header.getValue()));
                    return header.getValue();
            	}else{
            		return null;
            	}
            }else{
            	Header header = method.getResponseHeader(HEADER_REASON);
            	if (header!=null){
            		logger.error(String.format("status code %d, rejected reason:%s", method.getStatusCode(), header.getValue()));
            		return null;
            	}else{
            		logger.error(String.format("no rejected reason found. error status code %s:", method.getStatusCode()));
            		return null;
            	}
            }
		}catch(Exception e){
			logger.error("",e);
			return null;
		}finally{
			method.releaseConnection();
		}
	}
	//
	public String stopSession(String sessionId){
		HttpMethod method = new GetMethod(START_URL);
		try {
			method.setRequestHeader(HEADER_CMD, HEADER_CMDVAL_STOP);
			method.setRequestHeader(HEADER_SESSIONID, sessionId);
	        httpClient.getHttpClient().executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                String result = method.getResponseBodyAsString();
                logger.info(String.format("start ok. result body:%s", result));
                return REASON_VAL_SUCCESS;
            }else{
            	Header header = method.getResponseHeader(HEADER_REASON);
            	if (header!=null){
            		logger.error(String.format("status code %d, rejected reason:%s", method.getStatusCode(), header.getValue()));
            		return header.getValue();
            	}else{
            		logger.error(String.format("error status code %s:", method.getStatusCode()));
            		return REASON_VAL_NORSPHEAD;
            	}
            }
		}catch(Exception e){
			logger.error("",e);
			return REASON_VAL_UNKNOWN;
		}finally{
			method.releaseConnection();
		}
	}
	
	public int normalUsage(String sessionId, String url){
		HttpMethod method = new GetMethod(url);
		try {
			method.setRequestHeader(HEADER_SESSIONID, sessionId);
	        httpClient.getHttpClient().executeMethod(method);
	        int rspLength = method.getResponseBody().length;
            if (method.getStatusCode() == HttpStatus.SC_OK) {
            	logger.info(String.format("normal usage response length:%d", rspLength));
            }else{
            	logger.info(String.format("response statuscode:%d", method.getStatusCode()));
            }
            return rspLength;
		}catch(Exception e){
			logger.error("",e);
			return 0;
		}finally{
			method.releaseConnection();
		}
	}
	
	@Test
	public void test1(){
		String user="cy2";
		String url1= "http://news.sina.com.cn";
		String url2= "http://finance.sina.com.cn/";
		String sid = startSession(user);
		logger.info("sid get from start sessin:" + sid);
		assert(sid!=null);
		normalUsage(sid, url1);
		normalUsage(sid, url2);
		normalUsage(sid, url1);
		//cross threshold
		normalUsage(sid, url2);
		stopSession(sid);
	}
}
