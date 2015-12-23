package org.cld.datastore.impl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HbaseDbAdminImpl {
	
	private static Logger logger = LogManager.getLogger(HbaseDbAdminImpl.class);
	
	private Configuration hbaseConf;
	
	public HbaseDbAdminImpl(){
		hbaseConf = HBaseConfiguration.create();
	}
	
	public void createTable(String tableName, String[] cf) {
		HBaseAdmin admin = null;
		try{
			admin = new HBaseAdmin(hbaseConf);
	        if (admin.tableExists(tableName)) {
	           logger.error(String.format("table exists %s.", tableName));
	        } else {
	            HTableDescriptor tableDesc = new HTableDescriptor(tableName);
	            for (int i = 0; i < cf.length; i++) {
	                tableDesc.addFamily(new HColumnDescriptor(cf[i]));
	            }
	            admin.createTable(tableDesc);
	            logger.info(String.format("table created %s.", tableName));
	        }
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (admin!=null){
				try{
					admin.close();
				}catch(Exception e){
					logger.error("error close admin.", e);
				}
			}
		}
	}
	
	public void deleteTable(String tableName) {
		HBaseAdmin admin = null;
		try{
			admin = new HBaseAdmin(hbaseConf);
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (admin!=null){
				try{
					admin.close();
				}catch(Exception e){
					logger.error("error clos admin.", e);
				}
			}
		}
	}
}
