package org.cld.stock.strategy.select;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CqIndicators;
import org.cld.stock.common.StockUtil;
import org.cld.stock.strategy.OrderFilled;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StockOrder.ActionType;
import org.cld.stock.strategy.StockOrder.OrderType;
import org.cld.util.JsonUtil;

public class Range extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(Range.class);
	
	public static final String PROP_TABLE="scs.table.data";
	public static final String SHIFT_RATE="shift.rate";
	public static final float default_shiftRate=0.02f;
	
	public Range(){
	}
	
	private float shiftRate = default_shiftRate;
	private float orgBuyPrice;
	private float currentPrice;

	public String toString(){
		return String.format("buy price:%.3f", orgBuyPrice);
	}
	
	//called after initProp, before init
	@Override
	protected Map<String, SelectStrategy> genBsMap(PropertiesConfiguration pc){
		Map<String, SelectStrategy> bsMap = new HashMap<String, SelectStrategy>();
		String tableFile = pc.getString(PROP_TABLE);
		for (String[] d:StockUtil.getTableData(tableFile)){
			Range r = (Range) JsonUtil.deepClone(this);
			float bp = Float.parseFloat(d[1]);
			r.setOrgBuyPrice(bp);;
			bsMap.put(d[0], r);
		}
		return bsMap;
	}
	
	@Override
	public void init(){
		super.init();
		if (super.getParams().containsKey(SHIFT_RATE)){
			shiftRate = Float.parseFloat((String) super.getParams().get(SHIFT_RATE));
		}
		logger.info(String.format("shift rate is %.3f", shiftRate));
	}

	
	@Override
	public void tradeCompleted(OrderFilled or, boolean success){
		if (success){
			currentPrice = or.getAvgPrice() * (1-shiftRate);
		}else{
			if (or.getSide()==ActionType.buy){//failed to buy recover the price
				currentPrice = or.getAvgPrice() / (1-shiftRate);
			}else{
				logger.info(String.format("failed to sell %s, maybe cancelled, keep the price.", or));
			}
		}
		if (currentPrice>orgBuyPrice){
			currentPrice = orgBuyPrice;
		}
		logger.info(String.format("tradeCompleted current price for %s is changed to %.3f", or.getSymbol(), currentPrice));
		if (or.getSide()==ActionType.buy){//going down	
		}else if (or.getSide()==ActionType.sell){
			if (or.getTyp()==OrderType.limit){//going up
			}else if (or.getTyp()==OrderType.stoplimit){//going down
			}
		}
	}
	
	@Override
	public SelectCandidateResult selectByStream(CqIndicators cqi) {
		if (cqi.getCq().getLow()<currentPrice){
			currentPrice = currentPrice*(1-shiftRate);//to prevent tons of opp generated
			logger.info(String.format("range price for %s is changed to %.3f", cqi.getCq().getSymbol(), currentPrice));
			return new SelectCandidateResult(cqi.getCq().getSymbol(), cqi.getCq().getStartTime(), 0f, cqi.getCq().getLow());
		}else{
			return null;
		}
	}

	public float getOrgBuyPrice() {
		return orgBuyPrice;
	}
	public void setOrgBuyPrice(float orgBuyPrice) {
		this.orgBuyPrice = orgBuyPrice;
		this.currentPrice = orgBuyPrice;
	}
}
