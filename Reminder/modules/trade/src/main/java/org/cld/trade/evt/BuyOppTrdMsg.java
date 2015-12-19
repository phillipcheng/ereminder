package org.cld.trade.evt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.StockOrder;
import org.cld.stock.strategy.TradeStrategy;
import org.cld.trade.AutoTrader;
import org.cld.trade.StockOrderType;
import org.cld.trade.TradeMsg;
import org.cld.trade.TradeMsgPR;
import org.cld.trade.TradeMsgType;
import org.cld.trade.persist.StockPosition;
import org.cld.trade.persist.TradePersistMgr;
import org.cld.trade.response.Balance;
import org.cld.trade.response.OrderResponse;

/*
 * buy opportunity found trd-msg
 */
public class BuyOppTrdMsg extends TradeMsg {
	private static Logger logger =  LogManager.getLogger(BuyOppTrdMsg.class);
	
	TradeStrategy ts;
	
	public BuyOppTrdMsg(TradeStrategy ts, SelectCandidateResult scr){
		super(TradeMsgType.buyOppFound, scr, ts.getBs().getName(), null);
		this.ts = ts;
	}

	public String toString(){
		return String.format("scr:%s, ts:%s", scr, ts);
	}
	
	@Override
	public TradeMsgPR process(AutoTrader at) {
		logger.info(String.format("ts:%s", ts));
		
		TradeMsgPR tmpr = new TradeMsgPR();
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		if (scr!=null){
			Balance balance = at.getTm().getBalance();
			float cash = balance.getCash();
			if (cash>at.getUseAmount()){
				Map<String, StockOrder> somap = AutoTrader.genStockOrderMap(scr, ts.getSs(), at.getUseAmount());
				StockOrder buyOrder = somap.get(StockOrderType.buy.name());
				if (buyOrder!=null){
					OrderResponse or = at.getTm().trySubmit(buyOrder, at.isPreview());
					if (OrderResponse.SUCCESS.equals(or.getError()) && !at.isPreview()){
						StockPosition sp = new StockPosition(scr, bsName, somap, or.getClientorderid());
						TradePersistMgr.createStockPosition(at.getDbConf(), sp);
						TradeMsg mbo = new MonitorBuyOrderTrdMsg(or.getClientorderid(), scr, bsName, somap);
						tml.add(mbo);//buy order submitted, monitor buy order msg generated
					}else{
						logger.error(String.format("buy error: buy order: %s, response: %s", buyOrder, or));
					}
				}else{
					logger.error(String.format("buy order not found in somap:%s", somap));
				}
			}else{
				logger.error(String.format("cash amount %.2f less then needed %d", cash, at.getUseAmount()));
			}
		}else{
			logger.error(String.format("scr is null"));
		}
		
		tmpr.setExecuted(true);
		tmpr.setNewMsgs(tml);
		return tmpr;
	}
}
