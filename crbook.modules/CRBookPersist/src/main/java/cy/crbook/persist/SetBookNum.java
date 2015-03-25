package cy.crbook.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.SqlUtil;

import cy.common.entity.Volume;

public class SetBookNum {

	public Logger logger = LogManager.getLogger(SetBookNum.class);
	
	private JDBCPersistService pService;
	
	public SetBookNum(JDBCPersistService pService){
		this.pService = pService;
	}
	
	public void setBookNum(){
    	Connection con = null;
    	try{
    		setBCForLeafVolume();
    		con = pService.getDataSource().getConnection();
    		//only set the non leaf category, since the number of books each leaf category has already set
    		for (String rootId:Volume.ROOT_VOLUMES.keySet()){
    			setBCForNoneLeafVolume(rootId, con);
    		}
    	}catch(Exception e){
    		logger.error("", e);
    	}finally{
    		SqlUtil.closeResources(con, null);
    	}
    }
    
    public void setBCForLeafVolume(){
    	Connection con = null;
    	try{
    		con = pService.getDataSource().getConnection();
			//set the total book number as the count of all books belongs to this v
			String sql = "update volumes set volumes.booknum = (select count(*) from books where books.cat=volumes.id)";			
			SqlUtil.execUpdateSQL(con, sql);
    	}catch(Exception e){
    		logger.error("", e);
    	}finally{
    		SqlUtil.closeResources(con, null);
    	}    	
	}
    
    
    private void setBCForNoneLeafVolume(String volumeId, Connection db){
		String where = " where exists (select * from volumes v2 where v2.pcat=volumes.id) and volumes.pcat=?";
		String sqlCnt= "select count(*) as cnt from " + DBHeader.TABLE_VOL;
		String sqlStart = "select " + JDBCPersistService.allVolumeDBFields + " from " + DBHeader.TABLE_VOL;
		long vc = SqlUtil.getSingleIntResultSQL(sqlCnt+where, new String[]{volumeId}, db);
		
		if (vc!=0){
			Connection con = null;
			PreparedStatement statement=null;
			try{
				if (db==null){
					con = pService.getDataSource().getConnection();
				}else{
					con = db;
				}
				statement = con.prepareStatement(sqlStart + where);
				statement.setString(1, volumeId);
				ResultSet rs= statement.executeQuery();
				while (rs.next()){
    				Volume c = (Volume) VolumeJDBCMapper.getInstance().getObject(rs);
    				setBCForNoneLeafVolume(c.getId(), db);
	    		}
	    		if(rs != null && !rs.isClosed()){
	    			rs.close();
	    		}
			}catch(Exception e){
				logger.error("",e);
			}finally{
				SqlUtil.closeResources(null, statement);
				if (db==null){
					SqlUtil.closeResources(con, null);
				}
			}
		}
		//get the sum of all the v's total book num
		String sql0 = "select sum(" + DBHeader.COL_BOOKNUM + ") from " + DBHeader.TABLE_VOL + " where pcat = ?";
		long total = SqlUtil.getSingleIntResultSQL(sql0, new String[]{volumeId}, db);
		
		//set the total book number 
		String sql1 = "update " + DBHeader.TABLE_VOL + 
				" set booknum=" + total + " where " + DBHeader.COL_ID + "=?";
		SqlUtil.execUpdateSQLWithParams(db, sql1, new String[]{volumeId});
		logger.info("update for:" + volumeId);	
	}
}
