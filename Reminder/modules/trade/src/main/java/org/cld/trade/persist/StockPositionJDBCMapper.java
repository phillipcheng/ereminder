package org.cld.trade.persist;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

/**
 *
 */
public class StockPositionJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(StockPositionJDBCMapper.class);
	
	//symbol varchar(50), orderqty decimal(10,2), orderprice decimal(10,2), buySubmitDt DATETIME, buyOrderId varchar(50), 
	//stopSellOrderId varchar(50), limitSellOrderId varchar(50), soMap varchar(500)
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
	    			cursor.getString(1), cursor.getInt(2), cursor.getFloat(3), cursor.getDate(4), cursor.getString(5), 
	    			cursor.getString(6), cursor.getString(7), cursor.getString(8));
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
