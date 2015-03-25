package cy.crbook.persist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.SqlUtil;

import cy.common.entity.Volume;

class SiteControl{
	String siteId;
	List<String> allowedAuthors;
	public SiteControl(String siteId, List<String> allowedAuthors){
		this.siteId = siteId;
		this.allowedAuthors = allowedAuthors;
	}
	public String toString(){
		return "site:" + siteId + ", allowedAuthors:" + allowedAuthors;
	}
}

public class ContentControl {
	public static final String SITES_KEY="site";
	public static final String NAME_KEY="name";
	public static final String ALLOW_AUTHOR_KEY="allowedAuthor";
	
	public Logger logger = LogManager.getLogger(ContentControl.class);
	
	private JDBCPersistService pService;
	
	String confFile;
	List<SiteControl> siteControls = new ArrayList<SiteControl>();
	
	public ContentControl(String confFile, JDBCPersistService pService){
		this.confFile = confFile;
		this.pService = pService;
	}
	
	private void setUp(){
		try {
			XMLConfiguration properties = new XMLConfiguration(confFile);
			int siteCount= properties.getList(String.format("%s.%s", SITES_KEY, NAME_KEY)).size();
			for (int i=0; i<siteCount; i++){
				String siteId = properties.getString(
						String.format("%s(%d).%s", SITES_KEY, i, NAME_KEY));
				List<Object> authorsObj = properties.getList(
						String.format("%s(%d).%s", SITES_KEY, i, ALLOW_AUTHOR_KEY));
				List<String> authors = new ArrayList<String>();
				for (Object authorObj: authorsObj){
					authors.add((String)authorObj);
				}
				siteControls.add(new SiteControl(siteId, authors));
			}
			
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long applyControl(SiteControl sc){
		//update to ROOT_VOLUME_BANNED+"_"+"Original_PCAT" to volumes
		String partSql = "update volumes set pcat=? where pcat=? and author not in ";
		String inSql = SqlUtil.generateInParameters(sc.allowedAuthors);
		String sql = partSql + inSql;
		String dbSiteId = sc.siteId + "." + sc.siteId;
		Connection con = null;
		long count=0;
		try {
			con = pService.getDataSource().getConnection();
			count=SqlUtil.execUpdateSQLWithParams(con, sql, new Object[]{Volume.ROOT_VOLUME_BANNED+"_"+dbSiteId, 
					dbSiteId, sc.allowedAuthors});
		} catch (SQLException e) {
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return count;
	}
	
	public void main(){
		setUp();
		for (SiteControl sc:siteControls){
			long count = applyControl(sc);
			logger.info(String.format("%d volumes been controlled for site: %s", count, sc.toString()));
		}
	}
}
