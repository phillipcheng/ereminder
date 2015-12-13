package org.cld.stock.strategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.HdfsReader;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.TradeHour;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.trade.BuySellInfo;
import org.cld.stock.trade.BuySellResult;
import org.cld.stock.trade.StockOrder;
import org.cld.util.JsonUtil;
import org.cld.stock.trade.TradeSimulator;

public class SellStrategyByStockReducer extends Reducer<StockIdDatePair, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(SellStrategyByStockReducer.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	private Map<String, Object> sssMap = null;
	private CrawlConf cconf = null;
	private String baseMarketId = null;
	//private MultipleOutputs<Text, Text> mos;
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (sssMap==null){
			String sss = context.getConfiguration().get(SellStrategy.KEY_SELL_STRATEGYS);
			sssMap = (Map<String, Object>) JsonUtil.objFromJson(sss, Map.class);
		}
		if (cconf==null){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			cconf = CrawlTestUtil.getCConf(propFile);
		}
		baseMarketId = context.getConfiguration().get(StockUtil.KEY_BASE_MARKET_ID);
		//mos = new MultipleOutputs<Text,Text>(context);
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
       //mos.close();
    }
	
	/**
	 * input key: StockIdDatePair
	 * input value: stockid, value, buyPrice, dt(submit day), rank, bs.name, bs.params
	 * for each stockid, using all the sell strategy to get the results
	 * output: bs.name, bs.params, sell.params, dt, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
	 */
	@Override
	public void reduce(StockIdDatePair key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		StockConfig sc = StockUtil.getStockConfig(baseMarketId);
		String stockid = key.getStockId().toString();
		HdfsReader hr = StockPersistMgr.getReader(cconf, sc.getBTFQMinuteQuoteMapper(), stockid);
		try{
			for (Text v:values){//dt in ascending order
				String[] vv = v.toString().split(",");
				float buyLimit = Float.parseFloat(vv[2]);
				Date dt = sdf.parse(vv[3]);
				logger.debug(String.format("order by value %s", sdf.format(dt)));
				int rank = Integer.parseInt(vv[4]);
				String bsName = vv[5];
				String bsParams = vv[6];
				
				SellStrategy[] slss = (SellStrategy[]) sssMap.get(bsName);
				for (SellStrategy ss:slss){
					if (ss.getSelectNumber()>0 && ss.getSelectNumber()<rank){//0 means select all
						continue;
					}
					SelectCandidateResult scr = new SelectCandidateResult(stockid, dt, 0, buyLimit);
					List<StockOrder> sol = SellStrategy.makeStockOrders(scr, ss);
					BuySellInfo bsi = new BuySellInfo(String.format("%s,%s", bsName, bsParams), ss, sol, dt);
					
					TradeSimulator.submitStockOrder(bsi, sc, hr, TradeHour.Normal);
					
					List<StockOrder> solist = bsi.getSos();
					BuySellResult bsr = TradeSimulator.calculateBuySellResult(bsi.getSubmitD(), solist);
					if (bsr.getBuyPrice()!=0f && bsr.getSellPrice()==0f){
						//failed to sell
						logger.error(String.format("failed to sell: sos:%s", solist));
					}
					if (bsr!=null){
						String k = String.format("%s,%s", bsi.getBs(), bsi.getSs());
						String value = bsr.toString();
						context.write(new Text(k), new Text(value));
					}
				}
			}
		}catch(Exception e){
			logger.error("error when process.", e);
		}finally{
			hr.close();
		}
	}
}
