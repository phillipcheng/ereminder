package org.cld.stock.strategy;


public class TradeStrategy {
	
	private SelectStrategy bs;
	private SellStrategy ss;
	
	public TradeStrategy(){	
	}
	
	public TradeStrategy(SelectStrategy bs, SellStrategy ss){
		this.setBs(bs);
		this.setSs(ss);
	}
	
	public String toString(){
		return String.format("bs:%s,ss:%s", bs, ss);
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
	
	@Override
	public int hashCode(){
		return (int) (bs.hashCode() + ss.hashCode());
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof TradeStrategy){
			TradeStrategy ts = (TradeStrategy) o;
			if (bs.equals(ts.getBs()) && ss.equals(ts.getSs())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
}
