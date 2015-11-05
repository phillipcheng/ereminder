package org.cld.util.jdbc;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class GeneralJDBCMapper extends JDBCMapper{
	public Logger logger = LogManager.getLogger(GeneralJDBCMapper.class);
	private static GeneralJDBCMapper singleton = new GeneralJDBCMapper();
	
	private GeneralJDBCMapper(){	
	}

	public static GeneralJDBCMapper getInstance(){
		return singleton;
	}
	
	//return a list
	@Override
	public Object getObject(ResultSet cursor) {
		List<Object> ol = new ArrayList<Object>();
		try{
			int cc = cursor.getMetaData().getColumnCount();
			Object obj = null;
			for (int i=1; i<=cc; i++){
				int type = cursor.getMetaData().getColumnType(i);
				if (type==Types.DATE){
					obj = cursor.getDate(i);
				}else if (type==Types.TIMESTAMP){
					obj = cursor.getTimestamp(i);
				}else if (type==Types.DECIMAL){
					obj = cursor.getDouble(i);
				}else{
					obj = cursor.getString(i);
				}
				ol.add(obj);
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return ol;
	}

	@Override
	public String getTableName() {
		return null;//for general it can be any
	}

}
