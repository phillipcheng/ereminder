package org.cld.trade.evt;

import java.util.ArrayList;
import java.util.Date;
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
	
	private TradeStrategy ts;
	private List<SelectCandidateResult> scrl;
	
	public BuyOppTrdMsg() {
		super(TradeMsgType.buyOppFound);
	}
	
	public BuyOppTrdMsg(TradeStrategy ts, List<SelectCandidateResult> scrl){
		this();
		this.ts = ts;
		this.scrl = scrl;
	}
	
	public BuyOppTrdMsg(TradeStrategy ts, SelectCandidateResult scr){
		this();
		this.ts = ts;
		scrl = new ArrayList<SelectCandidateResult>();
		scrl.add(scr);
	}

	public String toString(){
		return String.format("scrl:%s, bs:%s", scrl, ts.getBs());
	}
	
	@Override
	public TradeMsgPR process(AutoTrader at) {
		List<SelectCandidateResult> trySCRL = null;
		if (ts.getSs().getSelectNumber()>0){//means not take all
			trySCRL = ts.getBs().filterResults(scrl, ts.getSs().getSelectNumber());
		}else{
			trySCRL = scrl;
		}
		TradeMsgPR tmpr = new TradeMsgPR();
		List<TradeMsg> tml = new ArrayList<TradeMsg>();
		for (SelectCandidateResult scr: trySCRL){
			if (scr!=null){
				Balance balance = at.getTm().getBalance();
				float cash = balance.getCash();
				if (cash>at.getUseAmount()){
					Map<StockOrderType, StockOrder> somap = AutoTrader.genStockOrderMap(scr, ts.getSs(), at.getUseAmount());
					StockOrder buyOrder = somap.get(StockOrderType.buy);
					OrderResponse or = at.getTm().trySubmit(buyOrder, at.isPreview());
					if (OrderResponse.SUCCESS.equals(or.getError())){
						StockPosition sp = new StockPosition(scr.getSymbol(), buyOrder.getQuantity(), buyOrder.getLimitPrice(), 
								new Date(), or.getClientorderid(), null, null, somap);
						TradePersistMgr.createStockPosition(at.getDbConf(), sp);
						TradeMsg mbo = new MonitorBuyOrderTrdMsg(or.getClientorderid(), somap);
						tml.add(mbo);//buy order submitted, monitor buy order msg generated
					}else{
						logger.error(String.format("buy error: buy order: %s, response: %s", buyOrder, or));
					}
				}
			}
		}
		tmpr.setExecuted(true);
		tmpr.setNewMsgs(tml);
		return tmpr;
	}
}
