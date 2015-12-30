package org.cld.trade;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.StockOrder;
import org.cld.trade.evt.TradeMsgType;

public abstract class TradeMsg {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	
	private TradeMsgType msgType;
	protected SelectCandidateResult scr;
	protected String bsName;
	protected Map<String, StockOrder> somap; //the somap context
	
	public TradeMsg(TradeMsgType msgType, SelectCandidateResult scr, String bsName, Map<String, StockOrder> somap){
		this.msgType = msgType;
		this.scr = scr;
		this.bsName = bsName;
		this.somap = somap;
	}
	
	public String getMsgId(){
		return String.format("%s_%s_%s", msgType, scr.getSymbol(), sdf.format(scr.getDt()));
	}

	public abstract TradeMsgPR process(AutoTrader at);
	
	public TradeMsgType getMsgType() {
		return msgType;
	}
	public void setMsgType(TradeMsgType msgType) {
		this.msgType = msgType;
	}
	public Map<String, StockOrder> getSomap() {
		return somap;
	}
	public void setSomap(Map<String, StockOrder> somap) {
		this.somap = somap;
	}
	public SelectCandidateResult getScr() {
		return scr;
	}
	public void setScr(SelectCandidateResult scr) {
		this.scr = scr;
	}
	public String getBsName() {
		return bsName;
	}
	public void setBsName(String bsName) {
		this.bsName = bsName;
	}
}
