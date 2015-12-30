package org.cld.stock.mapper.ext;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqUpcomingDivMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqUpcomingDivMapper.class);
	
	private NasdaqUpcomingDivMapper(){
		
	}
	private static NasdaqUpcomingDivMapper singleton = new NasdaqUpcomingDivMapper();
	
	public static NasdaqUpcomingDivMapper getInstance(){
		return singleton;
	}
/*
+---------+------------+------+------------+------------+------------+-------------+
| Symbol | ExDiv Date | Dividend  | Annual Dividend	| Record Date | Announcement Date | Payment Date |
+---------+------------+------+------------+------------+------------+-------------+
| AEK     | 2015-10-28 | 0.5000 | 2.00  |  2015-10-07 | 2015-11-01 | 2015-11-16  |
+---------+------------+------+------------+------------+------------+-------------+
*/
	@Override
	public Object getObject(ResultSet cursor) {
    	try{
    		DivSplit b= new DivSplit(
	    			cursor.getString(1), cursor.getDate(6), cursor.getDate(2), cursor.getFloat(3));
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
	}

	@Override
	public String getTableName() {
		return "NasdaqUpcomingDividend";
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}
	
	//BEN,2015-12-24,0.18,0.72,2015-12-29,2015-12-16,2016-01-13,
	@Override
	public String getInsertSql(String csv){
		String[] fields = csv.split(",");
		if (fields.length>=7){
			return String.format("'%s','%s',%s,%s,'%s','%s','%s'", 
				fields[0], fields[1], fields[2], fields[3], fields[4], fields[5], fields[6]);
		}else{
			return null;
		}
	}

}
