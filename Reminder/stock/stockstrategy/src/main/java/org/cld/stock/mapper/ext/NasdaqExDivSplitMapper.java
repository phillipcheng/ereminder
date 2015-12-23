package org.cld.stock.mapper.ext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NasdaqExDivSplitMapper extends YahooExDivSplitMapper{
	Logger logger = LogManager.getLogger(NasdaqExDivSplitMapper.class);
	
	private NasdaqExDivSplitMapper(){
		
	}
	private static NasdaqExDivSplitMapper singleton = new NasdaqExDivSplitMapper();
	
	public static NasdaqExDivSplitMapper getInstance(){
		return singleton;
	}

	@Override
	public String getTableName() {
		return "NasdaqExDivSplit";
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}
}
