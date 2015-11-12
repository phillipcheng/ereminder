package org.cld.stock.nasdaq.persistence;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqSplitJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqSplitJDBCMapper.class);
	
	private NasdaqSplitJDBCMapper(){
		
	}
	private static NasdaqSplitJDBCMapper singleton = new NasdaqSplitJDBCMapper();
	
	public static NasdaqSplitJDBCMapper getInstance(){
		return singleton;
	}
/*
+---------+-------+------------+------------+------------+
| stockid | ratio | paydate    | exdt       | dt         |
+---------+-------+------------+------------+------------+
| SHEN    | 2 : 1 | 2016-01-04 | 2016-01-05 | 2015-10-19 |
+---------+-------+------------+------------+------------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		DivSplit b= new DivSplit(
	    			cursor.getString(1), cursor.getDate(5), cursor.getDate(4), cursor.getString(2));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqSplit";
	}

}
