package org.cld.stock.nasdaq.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.yahoo.YahooFQDailyQuoteMapper;

public class NasdaqExDivSplitMapper extends YahooFQDailyQuoteMapper{
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
}
