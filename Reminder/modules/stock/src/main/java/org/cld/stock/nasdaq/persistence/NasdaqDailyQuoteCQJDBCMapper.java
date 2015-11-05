package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqDailyQuoteCQJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqDailyQuoteCQJDBCMapper.class);
	
	private NasdaqDailyQuoteCQJDBCMapper(){
		
	}
	private static NasdaqDailyQuoteCQJDBCMapper singleton = new NasdaqDailyQuoteCQJDBCMapper();
	
	public static NasdaqDailyQuoteCQJDBCMapper getInstance(){
		return singleton;
	}
/*
+-----------------------------+------------------------+--------------------------+--------------------------+-------------------------+---------------------------+----------------------------+--+
| nasdaqquotehistory.stockid  | nasdaqquotehistory.dt  | nasdaqquotehistory.open  | nasdaqquotehistory.high  | nasdaqquotehistory.low  | nasdaqquotehistory.close  | nasdaqquotehistory.volume  |
+-----------------------------+------------------------+--------------------------+--------------------------+-------------------------+---------------------------+----------------------------+--+
| AAC                         | 2015-10-05             | 24.71                    | 28.72                    | 24.71                   | 27.73                     | 578755                     |
+-----------------------------+------------------------+--------------------------+--------------------------+-------------------------+---------------------------+----------------------------+--+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		CandleQuote b= new CandleQuote(
	    			cursor.getString(1), cursor.getDate(2), cursor.getFloat(3), cursor.getFloat(4), 
	    			cursor.getFloat(6), cursor.getFloat(5), cursor.getDouble(7));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqQuoteHistory";
	}

}
