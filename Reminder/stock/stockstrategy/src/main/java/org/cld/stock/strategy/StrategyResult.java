package org.cld.stock.strategy;

public class StrategyResult {
	private long numRecords;
	private double avgRate;
	
	public long getNumRecords() {
		return numRecords;
	}
	public void setNumRecords(long numRecords) {
		this.numRecords = numRecords;
	}
	public double getAvgRate() {
		return avgRate;
	}
	public void setAvgRate(double avgRate) {
		this.avgRate = avgRate;
	}
	
	public String toString(){
		return String.format("num:%d, avg rate:%.4f", numRecords, avgRate);
	}
	
	public StrategyResult(long l, double d){
		this.numRecords = l;
		this.setAvgRate(d);
	}
}
