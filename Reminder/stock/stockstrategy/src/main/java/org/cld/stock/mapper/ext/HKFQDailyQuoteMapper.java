package org.cld.stock.mapper.ext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HKFQDailyQuoteMapper extends YahooFQDailyQuoteMapper{
	Logger logger = LogManager.getLogger(HKFQDailyQuoteMapper.class);
	
	private HKFQDailyQuoteMapper(){
		
	}
	private static HKFQDailyQuoteMapper singleton = new HKFQDailyQuoteMapper();
	
	public static HKFQDailyQuoteMapper getInstance(){
		return singleton;
	}

	@Override
	public String getTableName() {
		return "HKFqHistory";
	}

	@Override
	public boolean oneFetch() {
		return true;
	}

}
