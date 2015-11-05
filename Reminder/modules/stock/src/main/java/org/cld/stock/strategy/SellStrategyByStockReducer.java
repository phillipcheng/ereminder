package org.cld.stock.strategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.stock.CandleQuote;
import org.cld.stock.StockBase;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.trade.BuySellInfo;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.*;
import org.cld.util.JsonUtil;
import org.cld.stock.trade.TradeSimulator;

public class SellStrategyByStockReducer extends Reducer<Text, Text, Text, Text>{
	private static Logger logger =  LogManager.getLogger(SellStrategyByStockReducer.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	private Map<String, Object> sssMap = null;
	private CrawlConf cconf = null;
	private String baseMarketId = null;
	
	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		if (sssMap==null){
			String sss = context.getConfiguration().get(SellStrategy.KEY_SELL_STRATEGYS);
			sssMap = (Map<String, Object>) JsonUtil.objFromJson(sss, Map.class);
		}
		if (cconf==null){
			String propFile = context.getConfiguration().get(CrawlUtil.CRAWL_PROPERTIES);
			logger.info(String.format("conf file for mapper job is %s", propFile));
			cconf = CrawlTestUtil.getCConf(propFile);
		}
		baseMarketId = context.getConfiguration().get(StockBase.KEY_BASE_MARKET_ID);
	}
	
	/**
	 * input key: stockid
	 * input value: stockid, value, buyPrice, dt(submit day), rank, bs.name, bs.params
	 * for each stockid, using all the sell strategy to get the results
	 * output: bs.name, bs.params, sell.params, dt, stockid, buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent
	 */
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		try{
			StockConfig sc = StockUtil.getStockConfig(baseMarketId);
			String stockid = key.toString();
			Date minDate = null;
			Date maxDate = null;
			int maxHolding = 0;
			
			List<BuySellInfo> bsil= new ArrayList<BuySellInfo>();//
			
			for (Text v:values){
				String[] vv = v.toString().split(",");
				//
				float buyLimit = Float.parseFloat(vv[2]);
				Date dt = sdf.parse(vv[3]);
				int rank = Integer.parseInt(vv[4]);
				String bsName = vv[5];
				String bsParams = vv[6];
				if (minDate==null){
					minDate = dt;
				}else{
					if (minDate.after(dt)){
						minDate = dt;
					}
				}
				if (maxDate==null){
					maxDate = dt;
				}else{
					if (maxDate.before(dt)){
						maxDate = dt;
					}
				}
				
				SellStrategy[] slss = (SellStrategy[]) sssMap.get(bsName);
				for (SellStrategy ss:slss){
					if (ss.getHoldDuration()>maxHolding){
						maxHolding = ss.getHoldDuration();
					}
					if (ss.getSelectNumber()<rank){
						continue;
					}
					List<StockOrder> sol = SellStrategy.makeStockOrders(stockid, dt, buyLimit, ss);
					BuySellInfo bsi = new BuySellInfo(String.format("%s,%s", bsName, bsParams), ss, sol, dt);
					bsil.add(bsi);
				}
			}
			List<Object> lo = StockPersistMgr.getDataPivotByStockDate(cconf.getSmalldbconf(), sc.getFQDailyQuoteTableMapper(), stockid, 
					minDate, maxDate, 1, maxHolding);
			TreeMap<Date, CandleQuote> dcmap = new TreeMap<Date, CandleQuote>();
			for (Object o:lo){
				CandleQuote cq = (CandleQuote) o;
				dcmap.put(cq.getStartTime(), cq);
			}
			
			TradeSimulator.submitDailyOrder(bsil, dcmap, sc);
			
			for (BuySellInfo bsi: bsil){
				List<StockOrder> solist = bsi.getSos();
				List<StockOrder> buySOs = new ArrayList<StockOrder>();
				List<StockOrder> sellSOs = new ArrayList<StockOrder>();
				for (StockOrder so:solist){
					if (so.getAction()==ActionType.buy){
						buySOs.add(so);
					}else if (so.getAction() == ActionType.sell){
						sellSOs.add(so);
					}
				}
				float buyPrice=0;
				Date buyTime=null;
				for (StockOrder so: buySOs){
					if (so.getStatus()==StatusType.executed){
						buyPrice = so.getExecutedPrice();
						buyTime = so.getExecuteTime();
						break;
					}
				}
				float sellPrice=0;
				Date sellTime=null;
				OrderType sellOrderType = OrderType.market;
				for (StockOrder so: sellSOs){
					if (so.getStatus()==StatusType.executed){
						sellPrice = so.getExecutedPrice();
						sellTime = so.getExecuteTime();
						sellOrderType = so.getOrderType();
						break;
					}
				}
				String output = null;
				if (buyPrice !=0 && sellPrice !=0){
					float percent = (sellPrice-buyPrice)/buyPrice;
					output = String.format("%s, %s, %s, %s, %s, %s, %s, %s", sdf.format(bsi.getSubmitD()), stockid, 
							buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent);
					context.write(new Text(String.format("%s,%s", bsi.getBs(), bsi.getSs())), new Text(output));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
