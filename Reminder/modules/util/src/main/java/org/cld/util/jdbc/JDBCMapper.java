package org.cld.util.jdbc;

import java.sql.ResultSet;

import org.apache.commons.lang.ObjectUtils;


public abstract class JDBCMapper {
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

}
