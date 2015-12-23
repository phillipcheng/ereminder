package org.cld.stock.analyze;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class StockIdDateGroupingComparator extends WritableComparator {
	 public StockIdDateGroupingComparator() {
		 super(StockIdDatePair.class, true);
	 }
	 
	 @Override
	 /**
	 * This comparator controls which keys are grouped
	 * together into a single call to the reduce() method
	 */
	 public int compare(WritableComparable wc1, WritableComparable wc2) {
		 StockIdDatePair pair = (StockIdDatePair) wc1;
		 StockIdDatePair pair2 = (StockIdDatePair) wc2;
		 return pair.getStockId().compareTo(pair2.getStockId());
	 }
}
