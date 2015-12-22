package org.cld.stock.analyze;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class StockIdDatePartitioner extends Partitioner<StockIdDatePair, Text> {

	@Override
	public int getPartition(StockIdDatePair key, Text value, int numPartitions) {
		return Math.abs(key.getStockId().hashCode()) % numPartitions;
	}
}
