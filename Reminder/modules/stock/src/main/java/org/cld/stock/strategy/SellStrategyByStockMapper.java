package org.cld.stock.strategy;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SellStrategyByStockMapper extends Mapper<Object, Text, StockIdDatePair, Text>{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SellStrategyByStockMapper.class);
	
	/**
	 * ordered by value
	 * input value: stockid, value, buyPrice, dt, rank, bs.name, bs.params
	 */
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String[] vs = value.toString().split(",");
		String stockId = vs[0];
		context.write(new StockIdDatePair(new Text(stockId), value), value);
	}
}
