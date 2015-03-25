package org.cld.util.jdbc;

import java.sql.ResultSet;


public interface JDBCMapper {
	
	public Object getObject(ResultSet cursor);

}
