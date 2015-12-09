package org.cld.stock.nasdaq.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.yahoo.YahooFQDailyQuoteMapper;

public class NasdaqFQDailyQuoteMapper extends YahooFQDailyQuoteMapper{
	Logger logger = LogManager.getLogger(NasdaqFQDailyQuoteMapper.class);
	
	private NasdaqFQDailyQuoteMapper(){
		
	}
	private static NasdaqFQDailyQuoteMapper singleton = new NasdaqFQDailyQuoteMapper();
	
	public static NasdaqFQDailyQuoteMapper getInstance(){
		return singleton;
	}

	@Override
	public String getTableName() {
		return "NasdaqFqHistory";
	}

	@Override
	public boolean oneFetch() {
		return true;
	}
}
