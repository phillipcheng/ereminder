package org.cld.stock.strategy;

public class TradeStrategy {
	
	private SelectStrategy bs;
	private SellStrategy ss;
	
	public TradeStrategy(SelectStrategy bs, SellStrategy ss){
		this.setBs(bs);
		this.setSs(ss);
	}

	public SelectStrategy getBs() {
		return bs;
	}

	public void setBs(SelectStrategy bs) {
		this.bs = bs;
	}

	public SellStrategy getSs() {
		return ss;
	}

	public void setSs(SellStrategy ss) {
		this.ss = ss;
	}
}
