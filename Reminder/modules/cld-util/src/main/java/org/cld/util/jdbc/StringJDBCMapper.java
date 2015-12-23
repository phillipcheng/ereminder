package org.cld.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class StringJDBCMapper extends JDBCMapper{
	public Logger logger = LogManager.getLogger(StringJDBCMapper.class);
	private static StringJDBCMapper singleton = new StringJDBCMapper();
	
	private StringJDBCMapper(){	
	}

	public static StringJDBCMapper getInstance(){
		return singleton;
	}
	
	//return a list
	@Override
	public String getObject(ResultSet cursor) {
		try {
			return cursor.getString(1);
		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public String getTableName() {
		return null;//for general it can be any
	}

}
