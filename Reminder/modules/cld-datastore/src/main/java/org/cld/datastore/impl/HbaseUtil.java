package org.cld.datastore.impl;

import java.util.NavigableMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HbaseUtil {

	private static Logger logger = LogManager.getLogger(HbaseUtil.class);
	public static void copyRow(String tableName, String rowkeyFrom, String rowKeyTo){
		Configuration hbaseConf = HBaseConfiguration.addHbaseResources(new Configuration());
		HTable table = null;
		try{
			table = new HTable(hbaseConf, tableName);
	        Get get = new Get(rowkeyFrom.getBytes());
	        Result rs = table.get(get);
	        
			// lets say your already got the result from table.get(Bytes.toBytes("key1"))
		    Put put = new Put(Bytes.toBytes(rowKeyTo));
		
		    NavigableMap<byte[], NavigableMap<byte[], byte[]>> familyQualifierMap = rs.getNoVersionMap();
		    for (byte[] familyBytes : familyQualifierMap.keySet()) {
		        NavigableMap<byte[], byte[]> qualifierMap = familyQualifierMap.get(familyBytes);
		
		        for (byte[] qualifier : qualifierMap.keySet()) {
		            put.add(familyBytes, qualifier, qualifierMap.get(qualifier));
		        }            
		    }            
		    table.put(put);
		    table.flushCommits();
		    table.close();
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
