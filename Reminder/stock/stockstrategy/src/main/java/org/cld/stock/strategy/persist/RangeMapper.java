package org.cld.stock.strategy.persist;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

public class RangeMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(RangeMapper.class);

	private static RangeMapper singleton = new RangeMapper();
	
	public static RangeMapper getInstance(){
		return singleton;
	}
	
	@Override
	public String getTableName() {
		return "RangeStrategy";
	}

	@Override
	public Object getObject(ResultSet cursor) {
		try{
    		RangeEntry b= new RangeEntry(
	    			cursor.getString(1), cursor.getDate(2), cursor.getFloat(3));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

}
