/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package cy.common.wsclient.test;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.junit.Test;

import cy.common.entity.Book;
import cy.common.entity.Volume;
import cy.common.persist.RemotePersistManager;
import cy.crbook.wsserver.CRBookWS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestClient {
	private static final String PROXY_HOST = "16.85.88.10";
	private static final int PROXY_PORT = 8080;

	public Logger logger = LogManager.getLogger(RestClient.class);
    
	//public String MAIN_REQUEST_URL = "http://localhost:8080/crbookws/services/crbookrs";
	public String MAIN_REQUEST_URL = "http://ec2-54-187-167-132.us-west-2.compute.amazonaws.com:8080/crbookws/services/crbookrs";
    
	private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }
    
    @Test
    public void testGetBook() throws Exception {
        // Sent HTTP GET request to query customer info
        System.out.println("Sent HTTP GET request to query customer info");
        //address:/crbookrs  , path: /crbookrs
        URL url = new URL(MAIN_REQUEST_URL + "/crbookrs/books/450514");
        InputStream in = url.openStream();
        String bookStr = getStringFromInputStream(in);
        Book b = new Book();
        b.fromTopJSONString(bookStr);
        logger.info("get book:" + b);
    }    
    
    @Test
    public void testGetBookByName(){
    	try {
	        URL url = new URL(MAIN_REQUEST_URL + "/crbookrs/books/" + CRBookWS.METHOD_NAME_BYNAME + "10/0/10");
	        InputStream in = url.openStream();
	        String result = getStringFromInputStream(in);
	        List<Book> bl = Book.fromTopJSONListString(result);
	        logger.info("book list:" + bl);
		}catch(Exception e){
			logger.error("", e);
		}
    }
    
    //test volumes
    @Test
    public void testGetVolumesByName(){
    	try {
	        URL url = new URL(MAIN_REQUEST_URL + "/crbookrs/volumes/" + CRBookWS.METHOD_NAME_BYNAME +"/10/0/10");
	        InputStream in = url.openStream();
	        String result = getStringFromInputStream(in);
	        List<Volume> vl = Volume.fromTopJSONListString(result);
	        logger.info("volume list:" + vl);
		}catch(Exception e){
			logger.error("", e);
		}
    }
    
    //test session
    @Test
    public void testSession(){
    	try {
    		String stime = RemotePersistManager.SDF_SERVER_DTZ.format(new Date());
	        URL url = new URL(MAIN_REQUEST_URL + "/crbookrs/login/" + "cydevice/" + stime);
	        InputStream in = url.openStream();
	        String sessionId = getStringFromInputStream(in);
	        logger.info("sessionId:" + sessionId);
	 
	        String etime = RemotePersistManager.SDF_SERVER_DTZ.format(new Date());
	        url = new URL(MAIN_REQUEST_URL + "/crbookrs/logout/" + sessionId + "/" + etime);
	        in = url.openStream();
	        String ret = getStringFromInputStream(in);
	        logger.info("logout result:" + ret);	        
		}catch(Exception e){
			logger.error("", e);
		}
    }

    

}
