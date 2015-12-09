package org.cld.stock.sina.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.jdbc.JDBCMapper;

public class SinaDailyQuoteCQJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(SinaDailyQuoteCQJDBCMapper.class);
	
	private SinaDailyQuoteCQJDBCMapper(){
		
	}
	private static SinaDailyQuoteCQJDBCMapper singleton = new SinaDailyQuoteCQJDBCMapper();
	
	public static SinaDailyQuoteCQJDBCMapper getInstance(){
		return singleton;
	}
/*
+--------------------------+---------------------+-----------------------+-----------------------+------------------------+----------------------+-------------------------+-------------------------+--+
| sinamarketdaily.stockid  | sinamarketdaily.dt  | sinamarketdaily.open  | sinamarketdaily.high  | sinamarketdaily.close  | sinamarketdaily.low  | sinamarketdaily.volume  | sinamarketdaily.amount  |
+--------------------------+---------------------+-----------------------+-----------------------+------------------------+----------------------+-------------------------+-------------------------+--+
| 600000                   | 2012-06-29          | 8.04                  | 8.16                  | 8.13                   | 8.02                 | 50974236                | 412653792               |
+--------------------------+---------------------+-----------------------+-----------------------+------------------------+----------------------+-------------------------+-------------------------+--+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
	    	CandleQuote b= new CandleQuote(
	    			cursor.getString(1), cursor.getDate(2), cursor.getFloat(3), cursor.getFloat(4), 
	    			cursor.getFloat(5), cursor.getFloat(6), cursor.getDouble(7), cursor.getDouble(8));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "SinaMarketDaily";
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}

}
