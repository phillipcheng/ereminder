package org.cld.stock.mapper.ext;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public class SinaDividendJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(SinaDividendJDBCMapper.class);
	
	private SinaDividendJDBCMapper(){
		
	}
	private static SinaDividendJDBCMapper singleton = new SinaDividendJDBCMapper();
	
	public static SinaDividendJDBCMapper getInstance(){
		return singleton;
	}
/*
+---------+------------+--------+-----------+----------+----------+------------+------------+------------------+---------+
| stockid | dt         | SongGu | ZhuanZeng | Devidend | progress | ExDate     | RegDate    | XStockPublicDate | comment |
+---------+------------+--------+-----------+----------+----------+------------+------------+------------------+---------+
| 002075  | 2015-10-09 |   0.00 |      4.00 |     0.00 | ??       | 2015-10-16 | 2015-10-15 | 2015-10-16       | ??      |
+---------+------------+--------+-----------+----------+----------+------------+------------+------------------+---------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
	    	DivSplit b= new DivSplit(
	    			cursor.getString(1), cursor.getDate(2), cursor.getDate(7), cursor.getFloat(5)/10);//translate to per share
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "SinaShareBonusDividend";
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}

}
