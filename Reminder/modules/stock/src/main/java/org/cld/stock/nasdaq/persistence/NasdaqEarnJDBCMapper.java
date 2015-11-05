package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.AnnounceTime;
import org.cld.stock.QEarnEvent;
import org.cld.stock.persistence.EarnJDBCMapper;

public class NasdaqEarnJDBCMapper extends EarnJDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqEarnJDBCMapper.class);
	
	private NasdaqEarnJDBCMapper(){
		
	}
	private static NasdaqEarnJDBCMapper singleton = new NasdaqEarnJDBCMapper();
	
	public static NasdaqEarnJDBCMapper getInstance(){
		return singleton;
	}
/*
+--------------+---------+------------+---------------+--------------+----------------+--------+
| announceTime | stockid | dt         | fiscalQuarter | consensusEps | numberEstimate | eps    |
+--------------+---------+------------+---------------+--------------+----------------+--------+
| after        | AAPL    | 2010-01-25 | Dec 2009      |       2.0800 |             36 | 3.6700 |
+--------------+---------+------------+---------------+--------------+----------------+--------+
*/
	public static final String AFTER_MARKET="after";
	public static final String BEFORE_MARKET="before";
	public static final String NA="na";
	public static final SimpleDateFormat fsdf = new SimpleDateFormat("MMM yyyy");
	
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		AnnounceTime at;
    		String atStr = cursor.getString(1);
    		if (BEFORE_MARKET.equals(atStr)){
    			at = AnnounceTime.beforeMarket;
    		}else{
    			at = AnnounceTime.afterMarket;//default
    		}
    		String fiscalQ = cursor.getString(4);
    		Date fiscalQDt = fsdf.parse(fiscalQ);
    		QEarnEvent b= new QEarnEvent(
	    			at, cursor.getString(2), (float) cursor.getDouble(7), cursor.getDate(3), fiscalQDt, cursor.getFloat(5));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqEarnAnnounce";
	}
	
	@Override
	public boolean cumulativeEps() {
		return false;
	}

}
