package org.cld.stock.strategy;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class StockIdDatePair implements Writable, WritableComparable<StockIdDatePair>{
	
	private Text stockId = new Text(); // natural key
	private Text value = new Text(); // secondary key, format:  stockid, value, buyPrice, dt, rank, bs.name, bs.params

	public StockIdDatePair(){
	}
	
	public StockIdDatePair(Text stockId, Text value){
		this.stockId = stockId;
		this.value = value;
	}
	
	@Override
	public int compareTo(StockIdDatePair o) {
		int compareValue = stockId.compareTo(o.getStockId());
		if (compareValue == 0) {
			String[] thisvs = value.toString().split(",");
			String[] thatvs = o.getValue().toString().split(",");
			String thisDt = thisvs[3];
			String thatDt = thatvs[3];
			compareValue = thisDt.compareTo(thatDt);
		}
		return compareValue;    // sort ascending
		//return -1*compareValue;   // sort descending
	}

	@Override
	public void write(DataOutput out) throws IOException {
		stockId.write(out);
		value.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		stockId.readFields(in);
		value.readFields(in);
	}

	public Text getStockId() {
		return stockId;
	}

	public void setStockId(Text stockId) {
		this.stockId = stockId;
	}

	public Text getValue() {
		return value;
	}

	public void setValue(Text value) {
		this.value = value;
	}


}
