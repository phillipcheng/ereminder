package org.cld.stock.mapper.ext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
