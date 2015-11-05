package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqFQDailyQuoteCQJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqFQDailyQuoteCQJDBCMapper.class);
	
	private NasdaqFQDailyQuoteCQJDBCMapper(){
		
	}
	private static NasdaqFQDailyQuoteCQJDBCMapper singleton = new NasdaqFQDailyQuoteCQJDBCMapper();
	
	public static NasdaqFQDailyQuoteCQJDBCMapper getInstance(){
		return singleton;
	}
	
/**
+---------+------------+------+------+------+-------+--------+----------+
| stockid | dt         | open | high | low  | close | volume | adjClose |
+---------+------------+------+------+------+-------+--------+----------+
| ABEO    | 2014-10-23 | 0.20 | 0.23 | 0.19 |  0.22 |   2900 |    11.00 |
+---------+------------+------+------+------+-------+--------+----------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		float adjclose = cursor.getFloat(8);
    		float close = cursor.getFloat(6);
    		if (close==0 || adjclose==0){
    			return null;
    		}
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
