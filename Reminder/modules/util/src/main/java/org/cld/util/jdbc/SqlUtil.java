package org.cld.util.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SqlUtil {
	private static Logger logger = LogManager.getLogger(SqlUtil.class);
	
	public static String generateInParameterValues(List<String> params){
		String ret = "(";
		for (int i=0; i<params.size(); i++){
			String paramValue = params.get(i);
			if (i<params.size()-1){
				ret = ret + paramValue + ",";
			}else{
				ret = ret + paramValue;
			}
		}
		return ret+")";
	}
	
	public static String generateInParameters(List<String> params){
		String ret = "(";
		for (int i=0; i<params.size(); i++){
			if (i<params.size()-1){
				ret = ret + "?,";
			}else{
				ret = ret + "?";
			}
		}
		return ret+")";
	}
	
	public static String generateInParameters(String[] params){
		String ret = "(";
		for (int i=0; i<params.length; i++){
			if (i<params.length-1){
				ret = ret + "?,";
			}else{
				ret = ret + "?";
			}
		}
		return ret+")";
	}
	
	public static void closeResources(Connection con, Statement statement){
		try{
			if (statement!=null && !statement.isClosed()){
				statement.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
		
		try{
			if (con!=null && !con.isClosed()){
				con.close();
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public static int deleteBySQL(String sql, String param, DataSource ds){
		Connection con = null;
		PreparedStatement statement=null;
		int ret=-1;
		try{
			con = ds.getConnection();
			statement = con.prepareStatement(sql);
			statement.setString(1, param);
			ret = statement.executeUpdate();
			return ret;
		}catch(Exception e){
			logger.error("",e);
			return ret;
		}finally{
			closeResources(con, statement);
		}
	}
	
	public static void bindParameters(PreparedStatement statement, Object[]param){
		try{
			if (param!=null){
				for (int i=0; i<param.length;){
					if (param[i] instanceof String){
						statement.setString(i+1, (String)param[i]);
						i++;
					}else if (param[i] instanceof Integer){
						statement.setInt(i+1, ((Integer)param[i]).intValue());
						i++;
					}else if (param[i] instanceof Timestamp){
						statement.setTimestamp(i+1, (Timestamp) param[i]);
						i++;
					}else if (param[i] instanceof List){
						List l = (List)param[i];
						for (int j=0; j<l.size(); j++){
							if (l.get(j) instanceof String){
								statement.setString(i+j+1, (String)(l.get(j)));
							}else{
								logger.error("unsupported type:" + l.get(j));
							}
						}
						i=i+l.size();
					}else{
						logger.error("unsupported type:"+param[i]);
						i++;
					}
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	//put the in(List) parameter at the last
	public static long getSingleIntResultSQL(String sql, Object[] param, DataSource ds){
		Connection con = null;
		try{
			con = ds.getConnection();
			return getSingleIntResultSQL(sql, param, con);
		}catch(Exception e){
			logger.error("",e);
			return -1;
		}finally{
			closeResources(con, null);
		}
	}
	
	public static long getSingleIntResultSQL(String sql, Object[] param, Connection con){
		PreparedStatement statement=null;
		try{
			statement = con.prepareStatement(sql);
			bindParameters(statement, param);
			ResultSet rs= statement.executeQuery();
			rs.next();
			int count= rs.getInt(1);
			return count;	
		}catch(Exception e){
			logger.error("",e);
			return -1;
		}
	}
	
	public static Date getSingleDateResultSQL(String sql, Object[] param, Connection con){
		PreparedStatement statement=null;
		try{
			statement = con.prepareStatement(sql);
			bindParameters(statement, param);
			ResultSet rs= statement.executeQuery();
			rs.next();
			return rs.getDate(1);
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	public static void execUpdateSQL(String sql, DataSource ds){
		Connection con = null;
		try{
			con = ds.getConnection();
			execUpdateSQL(con, sql);
		}catch(Exception e){
			logger.error("",e);
		}finally{
			closeResources(con, null);
		}
	}
	
	public static void execUpdateSQL(Connection db, String sql){
		Statement statement=null;
		try{
			statement = db.createStatement();
			statement.executeUpdate(sql);    		
		}catch(Exception e){
			logger.error("",e);
		}finally{
			closeResources(null, statement);
		}
	}
	
	public static int execUpdateSQLWithParams(Connection db, String sql, Object[] params){
		PreparedStatement statement=null;
		try{
			statement = db.prepareStatement(sql);
			bindParameters(statement, params);
			return statement.executeUpdate();    		
		}catch(Exception e){
			logger.error("",e);
			return 0;
		}finally{
			closeResources(null, statement);
		}
	}
	
	/**
	 * @param range: 
	 *     -1: ""
	 * 		0: limit BATCH_SIZE
	 *      n: limit n*BATCH_SIZE, (n+1)*BATCH_SIZE
	 * @return
	 */
	private static String LIMIT_KEY="limit";
	public static int MAX_BATCH_SIZE=50;
	
	private static String getLimitRange(int offset, int limit){
		//<offset>,<limit>
		if (offset<0){
			return "";
		}else if (offset == 0){
			if (limit>0){
				return LIMIT_KEY + " " + limit+"";
			}else{
				return "";
			}
		}else{
			return LIMIT_KEY + " " + offset+","+limit;
		}
	}
	
	public static List<? extends Object> getObjectsByParam(String sql, Object[] param, Connection db, 
			int offset, int limit, String myOrderBy, JDBCMapper mapper, DataSource dataSource){
		ArrayList<Object> objects = new ArrayList<Object>();
		Connection con = null;
		try{
			if (db==null){
				con = dataSource.getConnection();
			}else{
				con = db;
			}
			return getObjectsByParam(sql, param, con, offset, limit, myOrderBy, mapper);
		}catch(Exception e){
			logger.error("",e);
			return objects;
		}finally{
			if (db==null){
				SqlUtil.closeResources(con, null);
			}
		}
	}

	public static List<? extends Object> getObjectsByParam(String sql, Object[] param, Connection con, 
			int offset, int limit, String myOrderBy, JDBCMapper mapper){
		if (offset<0){
    		offset=0;
        	logger.warn("negative offset not allowed on server side. reset to 0.");
    	}
		
    	if(limit>MAX_BATCH_SIZE){
    		limit=MAX_BATCH_SIZE;
        	logger.warn("limit larger than max_batch_size not allowed on server side. reset to "+MAX_BATCH_SIZE);
    	}
    	
    	ArrayList<Object> objects = new ArrayList<Object>();
    	String limitClause = getLimitRange(offset, limit);
    	String orderBy = "";
    	if (myOrderBy==null){
    		orderBy = " order by name ";
    	}else{
    		orderBy = myOrderBy;
    	}
    	
		PreparedStatement statement=null;
		try{
			statement = con.prepareStatement(sql + orderBy + limitClause);
			SqlUtil.bindParameters(statement, param);
			ResultSet rs= statement.executeQuery();
			while(rs.next()){				
				Object c = mapper.getObject(rs);
				logger.debug("got object: " + c);
				objects.add(c);
			}
			if(rs != null){
				rs.close();
			}
			return objects;
		}catch(Exception e){
			logger.error("error exec: " + sql,e);
			return objects;
		}finally{
			SqlUtil.closeResources(null, statement);
		}
    }
	
	public static int tryTimes=10;
	public static Connection getConnection(DBConnConf dbconf){
		Connection con = null;
		try {
			Class.forName(dbconf.getDriver());
		}catch(Exception e){
			logger.error("", e);
		}
		boolean got=false;
		int times=0;
		Random r = new Random();
		while(!got && times<tryTimes){
			times++;
			try {
				con = DriverManager.getConnection(dbconf.getUrl(), dbconf.getUser(), dbconf.getPass());
				got = true;
			}catch(Exception e){
				logger.warn("while use:" + dbconf, e);
				try {
					int rt = r.nextInt(10);
					Thread.sleep(rt*1000);
				} catch (InterruptedException e1) {
				}
			}
		}
		return con;
	}
}
