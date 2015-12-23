package cy.common.wsclient.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.ProxyConf;
import org.junit.Test;

import cy.common.entity.Book;
import cy.common.entity.EntityTest;
import cy.common.entity.Page;
import cy.common.entity.Volume;
import cy.crbook.wsclient.CRBookWSClient;

public class WsClientTest {
	
	private static final String PROXY_HOST = "16.85.88.10";
	private static final int PROXY_PORT = 8080;
	private static final int TIMEOUT=10000;

	public Logger logger = LogManager.getLogger(WsClientTest.class);
    
	//public String MAIN_REQUEST_URL = "http://localhost:8080/crbookws/services/crbookrs";
	public String MAIN_REQUEST_URL = "http://ec2-54-187-167-132.us-west-2.compute.amazonaws.com:8080/crbookws/services/crbookrs";
    private ProxyConf proxyConf = new ProxyConf(true, PROXY_HOST, PROXY_PORT);
	
    @Test
    public void testAddBookIfNotExists() throws Exception {
    	Book b1 = EntityTest.generateTestBook("testAddBook1");
    	CRBookWSClient wsclient = new CRBookWSClient(MAIN_REQUEST_URL, proxyConf, TIMEOUT);
    	long ret = wsclient.insertBookIfNotExists(b1);
    	logger.info(ret);
    	b1 = EntityTest.generateTestBook("testAddBook" + new Random().nextInt());
    	ret = wsclient.insertBookIfNotExists(b1);
    	assert(ret==1);
    }
    
    @Test
    public void testAddVolumeIfNotExists() throws Exception {
    	Volume v1 = EntityTest.generateTestVolume("testAddVolume1");
    	CRBookWSClient wsclient = new CRBookWSClient(MAIN_REQUEST_URL, proxyConf, TIMEOUT);
    	long ret = wsclient.insertVolumeIfNotExists(v1);
    	logger.info(ret);
    	v1 = EntityTest.generateTestVolume("testAddVolume" + new Random().nextInt());
    	ret = wsclient.insertVolumeIfNotExists(v1);
    	assert(ret==1);
    }
    
    @Test
    public void testAddPagesIfNotExists() throws Exception {
    	Random random = new Random();
    	List<Page> pl = EntityTest.generateTestPage("testBookPage"+random.nextInt(), 5);
    	CRBookWSClient wsclient = new CRBookWSClient(MAIN_REQUEST_URL, proxyConf, TIMEOUT);
    	long ret = wsclient.insertPagesIfNotExists(pl);
    	logger.info(ret);
    }
    
    @Test
    public void testAddBookPagesIfNotExists() throws Exception {
    	Random random = new Random();
    	String bookId = "testBook" + random.nextInt();
    	Book b = EntityTest.generateTestBook(bookId);
    	List<Page> pl = EntityTest.generateTestPage(b.getId(), 5);
    	CRBookWSClient wsclient = new CRBookWSClient(MAIN_REQUEST_URL, proxyConf, TIMEOUT);
    	long ret = wsclient.insertBookPagesIfNotExists(b, pl);
    	logger.info(ret);
    	assertTrue(ret==6);
    	
    	//exist, so no add
    	ret = wsclient.insertBookPagesIfNotExists(b, pl);
    	logger.info(ret);
    	assertTrue(ret==0);    	
    }
 }
