package org.cld.stock.sina;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.jdbc.JDBCMapper;

public class SinaFQDailyQuoteCQJDBCMapper implements JDBCMapper{
	Logger logger = LogManager.getLogger(SinaFQDailyQuoteCQJDBCMapper.class);
	
	private SinaFQDailyQuoteCQJDBCMapper(){
		
	}
	private static SinaFQDailyQuoteCQJDBCMapper singleton = new SinaFQDailyQuoteCQJDBCMapper();
	
	public static SinaFQDailyQuoteCQJDBCMapper getInstance(){
		return singleton;
	}
/*
+-----------------------+------------------+--------------------+--------------------+---------------------+-------------------+----------------------+----------------------+---------------------+--+
| sinamarketfq.stockid  | sinamarketfq.dt  | sinamarketfq.open  | sinamarketfq.high  | sinamarketfq.close  | sinamarketfq.low  | sinamarketfq.volume  | sinamarketfq.amount  | sinamarketfq.fqidx  |
+-----------------------+------------------+--------------------+--------------------+---------------------+-------------------+----------------------+----------------------+---------------------+--+
| 600000                | 2012-06-29       | 55.517             | 56.346             | 56.139              | 55.379            | 50974236             | 412653792            | 6.905               |
+-----------------------+------------------+--------------------+--------------------+---------------------+-------------------+----------------------+----------------------+---------------------+--+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
	    	CandleQuote b= new CandleQuote(
	    			cursor.getString(1), cursor.getDate(2), cursor.getFloat(3), cursor.getFloat(4), 
	    			cursor.getFloat(5), cursor.getFloat(6), cursor.getDouble(7), cursor.getDouble(8));
	    	b.setFqIdx(cursor.getFloat(9));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "sinamarketfq";
	}

}
