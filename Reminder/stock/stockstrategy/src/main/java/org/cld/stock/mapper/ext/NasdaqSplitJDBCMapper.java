package org.cld.stock.mapper.ext;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.DivSplit;
import org.cld.util.jdbc.JDBCMapper;

public class NasdaqSplitJDBCMapper extends JDBCMapper{
	Logger logger = LogManager.getLogger(NasdaqSplitJDBCMapper.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
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
    		DivSplit b;
    		b= new DivSplit(cursor.getString(1), cursor.getDate(5), cursor.getDate(4), cursor.getString(2));
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
	
	@Override
	public boolean oneFetch() {
		return true;
	}

	//SNFCA,5.000%,2016-02-05,2016-02-05,, 
	//AFSI,2 : 1,2016-02-02,2016-02-03,2015-12-15,
	@Override
	public String getInsertSql(String csv){
		String[] fields = csv.split(",", -1);
		if (fields.length>=5){
			return String.format("'%s','%s','%s','%s','%s'", 
				fields[0], fields[1], fields[2], fields[3], fields[4]);
		}else{
			return null;
		}
	}
}
