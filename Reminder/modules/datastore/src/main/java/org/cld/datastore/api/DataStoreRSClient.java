package org.cld.datastore.api;

import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ProxyConf;
import org.cld.util.RSHttpClient;
import org.cld.util.entity.SiteConf;


public class DataStoreRSClient extends RSHttpClient{
	public Logger logger = LogManager.getLogger(DataStoreRSClient.class);
    
	public String MAIN_REQUEST_URL = "http://localhost:8080/cldwebconf/services/crawlconf";
	
	private String mainRequestUrl = MAIN_REQUEST_URL;
	
	public DataStoreRSClient(String mainRequestUrl, ProxyConf proxyConf, int timeout){
		super(proxyConf, timeout);
		this.mainRequestUrl = mainRequestUrl;
	}
	
	public List<SiteConf> getDeployedSiteConf() throws Exception {
        String url = mainRequestUrl + "/crawlconf/siteconfs/";
        HttpMethod method = new GetMethod(url);
		try {	        
	        getHttpClient().executeMethod(method);
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                String result = method.getResponseBodyAsString();
                List<SiteConf> sclist = SiteConf.fromTopJSONListString(result);       
    	        return sclist;
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
}
