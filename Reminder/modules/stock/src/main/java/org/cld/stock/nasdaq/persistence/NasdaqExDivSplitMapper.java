package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.stock.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqExDivSplitMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqExDivSplitMapper.class);
	
	private NasdaqExDivSplitMapper(){
		
	}
	private static NasdaqExDivSplitMapper singleton = new NasdaqExDivSplitMapper();
	
	public static NasdaqExDivSplitMapper getInstance(){
		return singleton;
	}
/*
+---------+------------+---------------+
| stockid | dt         | info          |
+---------+------------+---------------+
| LBY     | 2015-08-07 | 0.11 Dividend |
+---------+------------+---------------+
+---------+------------+--------------------------------+
| LCUT    | 1997-02-13 | 11:             10 Stock Split |
+---------+------------+--------------------------------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		DivSplit b= new DivSplit(
	    			cursor.getString(1), null, cursor.getDate(2), cursor.getString(3));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqExDivSplit";
	}
}
