package org.cld.stock.strategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SellStrategyByStockMapper extends Mapper<Object, Text, Text, Text>{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(SellStrategyByStockMapper.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	/**
	 * ordered by value
	 * input value: stockid, value, buyPrice, dt, rank, bs.name, bs.params
	 */
	@Override
	public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
		String stockid = value.toString().split(",")[0];
		context.write(new Text(stockid), value);
	}
}
