package org.cld.stock.hk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.yahoo.YahooExDivSplitMapper;

public class HKExDivSplitMapper extends YahooExDivSplitMapper{
	Logger logger = LogManager.getLogger(HKExDivSplitMapper.class);
	
	private HKExDivSplitMapper(){
		
	}
	private static HKExDivSplitMapper singleton = new HKExDivSplitMapper();
	
	public static HKExDivSplitMapper getInstance(){
		return singleton;
	}

	@Override
	public String getTableName() {
		return "HKExDivSplit";
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}
}
