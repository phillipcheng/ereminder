package org.cld.stock.mapper.ext;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.AnnounceTime;
import org.cld.stock.common.QEarnEvent;
import org.cld.stock.mapper.EarnJDBCMapper;

public class SinaEarnJDBCMapper extends EarnJDBCMapper{
	Logger logger = LogManager.getLogger(SinaEarnJDBCMapper.class);
	
	private SinaEarnJDBCMapper(){
		
	}
	private static SinaEarnJDBCMapper singleton = new SinaEarnJDBCMapper();
	
	public static SinaEarnJDBCMapper getInstance(){
		return singleton;
	}
/*
+---------+------------+----------------+----------------+-------------+----------+------------+------------+
| stockid | dt         | BusinessIncome | BusinessProfit | TotalProfit | BasicEPS | DilutedEPS | pubDt      |
+---------+------------+----------------+----------------+-------------+----------+------------+------------+
| 600277  | 2015-09-30 |      566005.00 |        2318.69 |     9989.74 |     0.05 |       0.05 | 2015-10-15 |
+---------+------------+----------------+----------------+-------------+----------+------------+------------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		QEarnEvent b= new QEarnEvent(
	    			AnnounceTime.beforeMarket, cursor.getString(1), cursor.getFloat(7), cursor.getDate(8), cursor.getDate(2), QEarnEvent.NO_VALUE);
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "SinaFrProfitStatement";
	}
	
	@Override
	public boolean cumulativeEps() {
		return true;
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}

}
