package org.cld.stock.mapper.ext;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public abstract class YahooExDivSplitMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(YahooExDivSplitMapper.class);

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
}
