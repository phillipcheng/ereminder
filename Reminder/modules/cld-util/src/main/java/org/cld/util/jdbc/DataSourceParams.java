package org.cld.util.jdbc;

public class DataSourceParams {
    private String driverClass=null;
    private String dbUrl=null;
    private String dbUser=null;
    private String dbPass=null;
    private int maxDBConn=10;
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPass() {
		return dbPass;
	}
	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}
	public int getMaxDBConn() {
		return maxDBConn;
	}
	public void setMaxDBConn(int maxDBConn) {
		this.maxDBConn = maxDBConn;
	}
}
