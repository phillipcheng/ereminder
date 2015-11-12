package org.cld.trade.persist;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

public class StockPositionJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(StockPositionJDBCMapper.class);
	
	private StockPositionJDBCMapper(){
		
	}
	private static StockPositionJDBCMapper singleton = new StockPositionJDBCMapper();
	
	public static StockPositionJDBCMapper getInstance(){
		return singleton;
	}
/*
*/
	@Override
	public StockPosition getObject(ResultSet cursor) {
    	try{
    		StockPosition b= new StockPosition(
	    			cursor.getDate(1), cursor.getInt(2), cursor.getFloat(3), 
	    			cursor.getString(4), cursor.getInt(5));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}
	
	@Override
	public String getTableName() {
		return "StockPosition";
	}

}
