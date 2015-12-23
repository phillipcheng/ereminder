package cy.crbook.wsclient;

import java.io.IOException;
import java.net.BindException;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ProxyConf;
import org.cld.util.RSHttpClient;

import cy.common.entity.Book;
import cy.common.entity.BookPages;
import cy.common.entity.Page;
import cy.common.entity.Volume;
/*
 * Web Service Restful Client for java application 
 */
public class CRBookWSClient extends RSHttpClient{
	
	public Logger logger = LogManager.getLogger(CRBookWSClient.class);
	
	private String mainRequestUrl;
	
	//timeout in seconds
	public CRBookWSClient(String mainRequestUrl, ProxyConf proxyConf, int timeout){
		super(proxyConf, timeout);
		this.mainRequestUrl= mainRequestUrl;
	}
	
	private List<Volume> getVolumesByParam(String param, int offset, int limit) {
		HttpMethod method = new GetMethod(mainRequestUrl + "/crbookrs/volumes/" + param + "/" + offset + "/" + limit);
		try {	        
	        getHttpClient().executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                String result = method.getResponseBodyAsString();
    	        List<Volume> vl = Volume.fromTopJSONListString(result);	        
    	        return vl;
            }else{
            	logger.error("not ok status code:" + method.getStatusCode());
            	return null;
            }
		}catch(Exception e){
			logger.error("",e);
			return null;
		}finally{
			method.releaseConnection();
		}
	}
	
	//param: volumesCount/cat/
	private long getCountByParam(String param){
		HttpMethod method = new GetMethod(mainRequestUrl + "/crbookrs/" + param);
		try {
			getHttpClient().executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
            	String result = method.getResponseBodyAsString();
    	        return Long.parseLong(result);
            }else{
            	logger.error("not ok status code:" + method.getStatusCode());
            	return -1;
            }
		}catch(Exception e){
			logger.error("", e);
			return -1;
		}finally{
			method.releaseConnection();
		}
	}

	private static int MAX_RETRY_BIND_FAILURE = 4;
	private void executePutWithRetry(PutMethod put) throws HttpException, IOException{
		boolean connected=false;
        int retryTime=1;
        while (retryTime<MAX_RETRY_BIND_FAILURE && !connected){
            try{
            	getHttpClient().executeMethod(put);
            	connected = true;
            }catch(BindException be){
            	logger.warn("binding exception retry..:" + retryTime);
            	retryTime++;
            }
        }       
	}
	
	public long insertVolumeIfNotExists(Volume v) {
		String volStr = v.toTopJSONString();
		PutMethod put = new PutMethod(mainRequestUrl + "/crbookrs/volumes/");
        try {
        	RequestEntity entity = new StringRequestEntity(volStr, "application/json", "UTF-8");
            put.setRequestEntity(entity);
            executePutWithRetry(put);
	        if (put.getStatusCode() == HttpStatus.SC_OK) {
		        String ret = put.getResponseBodyAsString();
		        return Integer.parseInt(ret);
	        }else{
	        	logger.error("not ok status code:" + put.getStatusCode());
            	return -1;
	        }
        }catch(Exception e) {
            logger.error("", e);
            return -1;
        }finally{
        	put.releaseConnection();
        }
	}

	//return negative integer, the status code
	public long insertBookIfNotExists(Book b) {
		
		String bookStr = b.toTopJSONString();
		PutMethod put = new PutMethod(mainRequestUrl + "/crbookrs/books/");
    	try {
        	RequestEntity entity = new StringRequestEntity(bookStr, "application/json", "UTF-8");
            put.setRequestEntity(entity);
            executePutWithRetry(put);
	        if (put.getStatusCode() == HttpStatus.SC_OK) {
		        String ret = put.getResponseBodyAsString();
		        return Integer.parseInt(ret);
	        }else{
	        	String rsp = put.getResponseBodyAsString();
	        	logger.error("not ok status code:" + put.getStatusCode() + ", rsp:" + rsp);
            	return -1*put.getStatusCode();
	        }
        }catch(Exception e){
            logger.error("", e);
            return -1;
        }finally{
        	put.releaseConnection();
        }
	}
	
	public long insertPagesIfNotExists(List<Page> pageList) {		
		PutMethod put = new PutMethod(mainRequestUrl + "/crbookrs/pages/");
    	try {
        	String pageListStr = Page.toTopJSONListString(pageList);
        	RequestEntity entity = new StringRequestEntity(pageListStr, "application/json", "UTF-8");
            put.setRequestEntity(entity);
            executePutWithRetry(put);
	        if (put.getStatusCode() == HttpStatus.SC_OK) {
		        String ret = put.getResponseBodyAsString();
		        return Integer.parseInt(ret);
	        }else{
	        	logger.error("not ok status code:" + put.getStatusCode());
            	return -1;
	        }
        }catch(Exception e) {
            logger.error("", e);
            return -1;
        }finally{
        	put.releaseConnection();
        }
	}
	
	public long insertBookPagesIfNotExists(Book b, List<Page> pageList) {		
		PutMethod put = new PutMethod(mainRequestUrl + "/crbookrs/bookpages/");
    	try {
        	BookPages bps=new BookPages(b, pageList);
    		String jsonStr = bps.toTopJSONString();
    		RequestEntity entity = new StringRequestEntity(jsonStr, "application/json", "UTF-8");
            put.setRequestEntity(entity);
            executePutWithRetry(put);
	        if (put.getStatusCode() == HttpStatus.SC_OK) {
		        String ret = put.getResponseBodyAsString();
		        return Integer.parseInt(ret);
	        }else{
	        	logger.error("not ok status code:" + put.getStatusCode());
            	return -1;
	        }
        }catch(Exception e) {
            logger.error("", e);
            return -1;
        }finally{
        	put.releaseConnection();
        }
	}
}
