package org.cld.stock.hk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.yahoo.YahooFQDailyQuoteMapper;

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

}
