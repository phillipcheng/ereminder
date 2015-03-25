package cy.crbook.persist.test;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DataSourcePool;
import org.cld.util.jdbc.SqlUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import cy.common.entity.Book;
import cy.common.entity.Volume;
import cy.common.persist.RemotePersistManager;
import cy.common.xml.XmlImporter;
import cy.crbook.persist.DBHeader;
import cy.crbook.persist.JDBCPersistService;

public class PersistServiceTest {
	public static Logger logger = LogManager.getLogger(PersistServiceTest.class);
	
	public static DataSource ds = null;
	public static JDBCPersistService pService=null;
	

	public static void setUp() {
		logger.info("setup");
		ds = DataSourcePool.setupDataSource(DataSourcePool.initFromProperties("crpersist.properties"));
		pService = new JDBCPersistService(ds);
	}
	

	public void recreateDB(){
		pService.dropDB(ds);
		pService.createDB(ds);
	}

	public void updateDBSchema(){
		SqlUtil.execUpdateSQL(DBHeader.VOL_AUTHOR_INDEX_CREATE, ds);
	}
	

	public void testAddRemoveUser(){
		String retStr = pService.addUser("cy", "cy");
		assertTrue(RemotePersistManager.SIGN_UP_SUCCEED.equals(retStr));
		boolean ret = pService.removeUser("cy");
		assertTrue(ret);
	}
	

	public void testAddRemoveMyReadings(){
		String userId="cy";
		List<String> ids = new ArrayList<String>();
		ids.add("10year");
		ids.add("11eyes");
		ids.add("12wangzi");
		ids.add("14r");
		int ret=0;
		ret = pService.addMyReadings(userId, ids);
		assertTrue(ret==ids.size());
		
		ret = pService.deleteMyReadings(userId, ids);
		assertTrue(ret==ids.size());
	}

	public void testGetMyVolumes(){
		String userId="cy";
		List<String> ids = new ArrayList<String>();
		ids.add("10year");
		ids.add("11eyes");
		ids.add("12wangzi");
		ids.add("14r");
		int ret=0;
		ret = pService.addMyReadings(userId, ids);
		assertTrue(ret==ids.size());
		
		ret = pService.getMyVolumesCountLike(userId, "", Volume.TYPE_PIC);
		assertTrue(ret==ids.size());
		
		List<Volume> vl = pService.getMyVolumesLike(userId, "", Volume.TYPE_PIC, 0, 10);
		List<String> idsCopy = new ArrayList<String>();
		for (String id : ids){
			idsCopy.add(id);
		}
		for (int i=0; i<vl.size(); i++){
			Volume v = vl.get(i);
			assertTrue(idsCopy.contains(v.getId()));
			idsCopy.remove(v.getId());
		}
		
		ret = pService.deleteMyReadings(userId, ids);
		assertTrue(ret==ids.size());
	}
	

	public void testGetMyBooks(){
		String userId="cy";
		List<String> ids = new ArrayList<String>();
		ids.add("0zzxs-8521");
		ids.add("1001ye-19281");
		ids.add("100dqr-11068");
		ids.add("10year-19278");
		int ret=0;
		ret = pService.addMyReadings(userId, ids);
		assertTrue(ret==ids.size());
		
		ret = pService.getMyBooksCountLike(userId, "", Volume.TYPE_PIC);
		assertTrue(ret==ids.size());
		
		List<Book> vl = pService.getMyBooksLike(userId, "", Volume.TYPE_PIC, 0, 10);
		List<String> idsCopy = new ArrayList<String>();
		for (String id : ids){
			idsCopy.add(id);
		}
		for (int i=0; i<vl.size(); i++){
			Book b = vl.get(i);
			assertTrue(idsCopy.contains(b.getId()));
			idsCopy.remove(b.getId());
		}
		
		ret = pService.deleteMyReadings(userId, ids);
		assertTrue(ret==ids.size());
	}
	
	@Test
	public void testGetBookById(){
		setUp();
		Book b = pService.getBookById("52xrs.1001");
		logger.info(b);
	}

}
