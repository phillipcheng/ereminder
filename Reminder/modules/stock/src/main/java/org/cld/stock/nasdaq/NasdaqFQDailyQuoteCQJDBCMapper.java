package org.cld.stock.nasdaq;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqFQDailyQuoteCQJDBCMapper implements JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqFQDailyQuoteCQJDBCMapper.class);
	
	private NasdaqFQDailyQuoteCQJDBCMapper(){
		
	}
	private static NasdaqFQDailyQuoteCQJDBCMapper singleton = new NasdaqFQDailyQuoteCQJDBCMapper();
	
	public static NasdaqFQDailyQuoteCQJDBCMapper getInstance(){
		return singleton;
	}
	
/**
+--------------------------+---------------------+-----------------------+-----------------------+----------------------+------------------------+-------------------------+---------------------------+--+
| nasdaqfqhistory.stockid  | nasdaqfqhistory.dt  | nasdaqfqhistory.open  | nasdaqfqhistory.high  | nasdaqfqhistory.low  | nasdaqfqhistory.close  | nasdaqfqhistory.volume  | nasdaqfqhistory.adjclose  |
+--------------------------+---------------------+-----------------------+-----------------------+----------------------+------------------------+-------------------------+---------------------------+--+
| AAAP                     | 2015-09-17          | 0.8                   | 0.8                   | 0.8                  | 0.85                   | 75000                   | 0.85                      |
+--------------------------+---------------------+-----------------------+-----------------------+----------------------+------------------------+-------------------------+---------------------------+--+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		float adjclose = cursor.getFloat(8);
    		float close = cursor.getFloat(6);
    		float fqIdx = adjclose/close;
	    	float adjopen = fqIdx * cursor.getFloat(3);
	    	float adjhigh = fqIdx * cursor.getFloat(4);
	    	float adjlow = fqIdx * cursor.getFloat(5);
	    	
	    	CandleQuote b= new CandleQuote(
	    			cursor.getString(1), cursor.getDate(2), adjopen, adjhigh, adjclose, adjlow, cursor.getDouble(7));
	    	b.setFqIdx(fqIdx);
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqFqHistory";
	}

}
