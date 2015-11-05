package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.Dividend;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqDividendJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqDividendJDBCMapper.class);
	
	private NasdaqDividendJDBCMapper(){
		
	}
	private static NasdaqDividendJDBCMapper singleton = new NasdaqDividendJDBCMapper();
	
	public static NasdaqDividendJDBCMapper getInstance(){
		return singleton;
	}
/*
+---------+------------+------+------------+------------+------------+-------------+
| stockid | EffDate    | Type | CashAmount | dt         | RecordDate | PaymentDate |
+---------+------------+------+------------+------------+------------+-------------+
| AEK     | 2015-10-28 | Cash |     0.5000 | 2015-10-07 | 2015-11-01 | 2015-11-16  |
+---------+------------+------+------------+------------+------------+-------------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		Dividend b= new Dividend(
	    			cursor.getString(1), cursor.getDate(2), cursor.getFloat(4));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqDividendHistory";
	}

}
