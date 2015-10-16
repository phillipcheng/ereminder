package cy.crbook.persist;

import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.JDBCMapper;

import cy.common.entity.Volume;

public class VolumeJDBCMapper implements JDBCMapper{
	Logger logger = LogManager.getLogger(VolumeJDBCMapper.class);
	
	private VolumeJDBCMapper(){
		
	}
	private static VolumeJDBCMapper singleton = new VolumeJDBCMapper();
	
	public static VolumeJDBCMapper getInstance(){
		return singleton;
	}
	
	@Override
	public Object getObject(ResultSet cursor) {
    	Volume v = null;
    	try {
	    	v = new Volume(
					cursor.getString(1), cursor.getInt(2), cursor.getString(3), 
					cursor.getTimestamp(4), cursor.getString(5), cursor.getString(6), 
					cursor.getString(7), cursor.getInt(8), true);
    	}catch(Exception e){
    		logger.error("", e);
    	}
    	return v;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}
