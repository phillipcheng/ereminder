package org.cld.util.jdbc;


import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataSourcePool {
	public static Logger logger = LogManager.getLogger(DataSourcePool.class);
	
    public static void main(String[] args) {
    	
    	DataSourceParams dsp = initFromProperties("crpersist.properties");
        DataSource dataSource = setupDataSource(dsp);
        String sql="select * from pg_tables";

        //
        // Now, we can use JDBC DataSource as we normally would.
        //
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;

        try {
            logger.info("Creating connection.");
            conn = dataSource.getConnection();
            logger.info("Creating statement.");
            stmt = conn.createStatement();
            logger.info("Executing statement.");
            rset = stmt.executeQuery(sql);
            logger.info("Results:");
            int numcols = rset.getMetaData().getColumnCount();
            while(rset.next()) {
                for(int i=1;i<=numcols;i++) {
                    System.out.print("\t" + rset.getString(i));
                }
                logger.info("");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) rset.close(); } catch(Exception e) { }
            try { if (stmt != null) stmt.close(); } catch(Exception e) { }
            try { if (conn != null) conn.close(); } catch(Exception e) { }
        }
    }

    
    
    private static final String DB_DRIVER_KEY="jdbc.driver";
    private static final String DB_URL_KEY="db.url";
    private static final String DB_USER="db.user";
    private static final String DB_PASS="db.pass";
    private static final String MAX_DB_CONN="max.db.conn";
    

    
    //read crpersist.properties
    public static DataSourceParams initFromProperties(String persistProperties){
    	try {
			PropertiesConfiguration properties = new PropertiesConfiguration(persistProperties);
			DataSourceParams dsp = new DataSourceParams();
			dsp.setDriverClass(properties.getString(DB_DRIVER_KEY));
			dsp.setDbUrl(properties.getString(DB_URL_KEY));
			dsp.setDbUser(properties.getString(DB_USER));
			dsp.setDbPass(properties.getString(DB_PASS));
			dsp.setMaxDBConn(properties.getInt(MAX_DB_CONN));
			Class.forName(dsp.getDriverClass());
			return dsp;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
    }
    
    public static DataSource getDataSource(){
    	DataSourceParams dsp = initFromProperties("crpersist.properties");
    	return setupDataSource(dsp);
    }
    
    public static DataSource setupDataSource(DataSourceParams dsp) {    	
    	BasicDataSource bds = new BasicDataSource();
    	bds.setDriverClassName(dsp.getDriverClass());
    	bds.setUrl(dsp.getDbUrl());
    	bds.setUsername(dsp.getDbUser());
    	bds.setPassword(dsp.getDbPass());
    	bds.setLogAbandoned(true);
    	bds.setRemoveAbandoned(true);
    	bds.setRemoveAbandonedTimeout(60);//seconds
    	bds.setMaxActive(dsp.getMaxDBConn());
    	bds.setMinEvictableIdleTimeMillis(30000);//30 seconds
    	return bds;
    }
}
