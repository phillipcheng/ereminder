package org.cld.stock.hk;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;

public class HKStockBase extends StockBase{
	private static StockConfig sc = new HKStockConfig();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public StockConfig getStockConfig() {
		return sc;
	}
	
	public HKStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, StockUtil.HK_STOCK_BASE, marketId, sd, ed, StockUtil.HK_STOCK_BASE);
	}

	@Override
	public boolean fqReady(Date today) {
		return false;
	}
}
