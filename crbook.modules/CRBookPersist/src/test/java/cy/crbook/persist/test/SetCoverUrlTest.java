package cy.crbook.persist.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.DataSourcePool;
import org.cld.util.jdbc.SqlUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import cy.common.entity.Reading;
import cy.common.entity.Volume;
import cy.crbook.persist.JDBCPersistService;
import cy.crbook.persist.SetVolumeCover;

public class SetCoverUrlTest {
	public static Logger logger = LogManager.getLogger(SetCoverUrlTest.class);
	
	public static DataSource ds = null;
	public static JDBCPersistService pService=null;
	
	@BeforeClass
	public static void setUp() {
		logger.info("setup");
		ds = DataSourcePool.setupDataSource(DataSourcePool.initFromProperties("crpersist.properties"));
		pService = new JDBCPersistService(ds);
	}

	@Test
	public void testSetAllVolume(){
		SetVolumeCover svc = new SetVolumeCover(pService);
		svc.setAllCoverUrl();
	}
	
	@Test
	public void setOneVolumeCoverUrl(){
		SetVolumeCover svc = new SetVolumeCover(pService);
		Volume v = pService.getVolumeById("a8z8.a8z8");
		svc.dfsSetCover(v);
	}
	
	@Test
	public void fixCrawlProblems(){
		//update root volume names
		String sql = "update volumes set name=id where name='noname' and pcat like '99999%'";
		SqlUtil.execUpdateSQL(sql, ds);
	}

}
