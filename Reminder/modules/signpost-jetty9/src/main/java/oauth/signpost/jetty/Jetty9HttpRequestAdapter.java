
package oauth.signpost.jetty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import oauth.signpost.http.HttpRequest;

public class Jetty9HttpRequestAdapter implements HttpRequest {

	private static Logger logger =  LogManager.getLogger(Jetty9HttpRequestAdapter.class);
	
    private Request request;

    private String requestUrl;

    public Jetty9HttpRequestAdapter(Request request) {
        this.request = request;
        buildRequestUrl();
    }

    public String getContentType() {
        HttpFields fields = request.getHeaders();
        String str = fields.get("Content-Type");
        logger.debug(String.format("get content type:%s", str));
        return str;
    }

    public InputStream getMessagePayload() throws IOException {
    	ContentProvider cp = request.getContent();
    	Iterator<ByteBuffer> bbi = cp.iterator();
    	byte[] prevArray = new byte[0];
    	byte[] finalArray = null;
    	while(bbi.hasNext()){
    		ByteBuffer bb = bbi.next();
    		byte[] newBytes = bb.array();
    		finalArray = new byte[prevArray.length + newBytes.length];
    		System.arraycopy(prevArray, 0, finalArray, 0, prevArray.length);
    		System.arraycopy(newBytes, 0, finalArray, prevArray.length, newBytes.length);
    		prevArray = finalArray;
    	}
    	if (finalArray!=null){
    		logger.debug(String.format("getMessagePayload:%s", finalArray.toString()));
    	}else{
    		logger.debug(String.format("getMessagePayload:%s", "null"));
    	}
        return new ByteArrayInputStream(finalArray);
    }

    public String getMethod() {
        String str= request.getMethod();
        logger.debug(String.format("getMethod:%s", str));
        return str;
    }

    public String getRequestUrl() {
    	logger.debug(String.format("getRequestUrl:%s", requestUrl));
        return requestUrl;
    }

    public void setRequestUrl(String url) {
        throw new RuntimeException(new UnsupportedOperationException());
    }

    public void setHeader(String name, String value) {
    	logger.debug(String.format("setHeader:%s,%s", name, value));
        request.header(name, value);
    }

    public String getHeader(String name) {
        HttpFields fields = request.getHeaders();
        String value = fields.get(name);
        logger.debug(String.format("getHeader:%s,%s", name, value));
        return value;
    }

    public Map<String, String> getAllHeaders() {
        HttpFields fields = request.getHeaders();
        Iterator<HttpField> iter = fields.iterator();
        HashMap<String, String> headers = new HashMap<String, String>();
        while (iter.hasNext()) {
        	HttpField field = iter.next();
            headers.put(field.getName(), field.getValue());
        }
        logger.debug(String.format("getAllHeaders:%s", headers));
        return headers;
    }

    // Jetty has some very weird mechanism for handling URLs... we have to
    // reconstruct it here.
    private void buildRequestUrl() {
        this.requestUrl = request.getURI().toString();
        logger.debug(String.format("buildRequestUrl:%s", requestUrl));
    }

    public Object unwrap() {
    	logger.debug(String.format("unwrap:%s", request));
        return request;
    }
}
