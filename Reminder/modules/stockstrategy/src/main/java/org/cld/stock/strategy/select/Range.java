package org.cld.stock.strategy.select;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockUtil;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.util.JsonUtil;

public class Range extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(Range.class);
	public static final String PROP_TABLE="scs.table.data";
	
	public Range(){
	}
	
	float buyPrice;

	public String toString(){
		return String.format("buy price:%.3f", buyPrice);
	}
	
	//called after initProp, before init
	@Override
	protected Map<String, SelectStrategy> genBsMap(PropertiesConfiguration pc){
		Map<String, SelectStrategy> bsMap = new HashMap<String, SelectStrategy>();
		String tableFile = pc.getString(PROP_TABLE);
		for (String[] d:StockUtil.getTableData(tableFile)){
			Range r = (Range) JsonUtil.deepClone(this);
			float bp = Float.parseFloat(d[1]);
			r.setBuyPrice(bp);
			bsMap.put(d[0], r);
		}
		return bsMap;
	}
	
	@Override
	public void init(){
		super.init();
	}

	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		if (cqi.getCq().getLow()<buyPrice){
			return new SelectCandidateResult(cqi.getCq().getSymbol(), cqi.getCq().getStartTime(), 0f, cqi.getCq().getLow());
		}else{
			return null;
		}
	}
	
	public float getBuyPrice() {
		return buyPrice;
	}
	public void setBuyPrice(float buyPrice) {
		this.buyPrice = buyPrice;
	}
}
