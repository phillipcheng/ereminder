package org.cld.util.jdbc;

import java.sql.ResultSet;


public interface JDBCMapper {
	public String getTableName();
	public Object getObject(ResultSet cursor);

}
