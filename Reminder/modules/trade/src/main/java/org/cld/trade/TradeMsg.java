package org.cld.trade;

import java.util.Map;

import org.cld.stock.strategy.StockOrder;


public abstract class TradeMsg {
	private TradeMsgType msgType;
	protected Map<String, StockOrder> somap; //the somap context
	
	public TradeMsg(TradeMsgType msgType){
		this.msgType = msgType;
	}
	
	public String getMsgId(){
		return String.format("%s", msgType);
	}


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
	
	public abstract TradeMsgPR process(AutoTrader at);

}
