package org.cld.stock.nasdaq;

import java.util.Date;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.nasdaq.NasdaqStockConfig;
import org.cld.stock.nasdaq.task.QuotePostProcessTask;

public class NasdaqStockBase extends StockBase{
	private static StockConfig sc = new NasdaqStockConfig();
	
	@Override
	public StockConfig getStockConfig() {
		return sc;
	}
	
	public NasdaqStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, marketId, sd, ed);
	}
}
