package org.cld.util.jdbc;

import java.sql.ResultSet;

import org.apache.commons.lang.ObjectUtils;
import org.cld.util.DataMapper;


public abstract class JDBCMapper implements DataMapper {
	public abstract String getTableName();
	public abstract Object getObject(ResultSet cursor);//may return null if want to be filtered
	
	@Override
	public boolean equals(Object obj){
		if (obj!=null && obj instanceof JDBCMapper){
			JDBCMapper jobj=(JDBCMapper)obj;
			return ObjectUtils.equals(this.getTableName(), jobj.getTableName());
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return getTableName().hashCode();
	}
	
	public boolean oneFetch(){
		return true;
	}
	
	public String getInsertSql(String csv){
		return null;
	}
}
