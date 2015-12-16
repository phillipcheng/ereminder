package org.cld.stock.stockbase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.config.HKStockConfig;
import org.cld.stock.framework.StockBase;

public class HKStockBase extends StockBase{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public HKStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, StockUtil.HK_STOCK_BASE, marketId, sd, ed, StockUtil.HK_STOCK_BASE);
	}

	@Override
	public boolean fqReady(Date today) {
		return false;
	}
}
