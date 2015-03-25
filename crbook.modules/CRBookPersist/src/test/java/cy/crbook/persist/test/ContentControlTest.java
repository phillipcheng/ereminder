package cy.crbook.persist.test;

import javax.sql.DataSource;

import org.cld.util.jdbc.DataSourcePool;
import org.junit.BeforeClass;
import org.junit.Test;

import cy.crbook.persist.ContentControl;
import cy.crbook.persist.JDBCPersistService;

public class ContentControlTest {
	public static DataSource ds = null;
	public static JDBCPersistService pService=null;
	
	@BeforeClass
	public static void setUp() {
		ds = DataSourcePool.setupDataSource(DataSourcePool.initFromProperties("crpersist.properties"));
		pService = new JDBCPersistService(ds);
	}
	
	@Test
	public void testContentControl(){
		ContentControl cc = new ContentControl("ContentControl.xml", pService);
		cc.main();
	}
}
