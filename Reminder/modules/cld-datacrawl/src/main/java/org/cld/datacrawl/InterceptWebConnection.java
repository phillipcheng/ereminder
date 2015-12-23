package org.cld.datacrawl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

public class InterceptWebConnection extends FalsifyingWebConnection{

	private static Logger logger =  LogManager.getLogger(InterceptWebConnection.class);
	
	String[] skipURL;
    public InterceptWebConnection(WebClient webClient, String[] skipURL) throws IllegalArgumentException{
        super(webClient);
        this.skipURL = skipURL;
    }
    
    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {
        String requestUrl = request.getUrl().toString();
        if (skipURL!=null){
	        for (String url : skipURL){
		        if(requestUrl.contains(url)){
		        	logger.info(String.format("url %s is filtered", requestUrl));
		            return createWebResponse(request, "", "application/javascript", 200, "Ok");
		        }
	        }
        }
        return super.getResponse(request);
    }
}