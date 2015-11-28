package org.cld.stock.strategy;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;
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
import org.cld.stock.trade.BuySellResult;
import org.cld.stock.trade.StockOrder;
import org.cld.util.JsonUtil;
import org.cld.stock.trade.TradeSimulator;

public class SellStrategyByStockReducer extends Reducer<Text, Text, Text, Text>{
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
			logger.info(String.format("conf file for mapper job is %s", propFile));
			cconf = CrawlTestUtil.getCConf(propFile);
		}
		baseMarketId = context.getConfiguration().get(StockBase.KEY_BASE_MARKET_ID);
		//mos = new MultipleOutputs<Text,Text>(context);
	}
	
    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
       //mos.close();
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
					SelectCandidateResult scr = new SelectCandidateResult(stockid, dt, 0, buyLimit);
					List<StockOrder> sol = SellStrategy.makeStockOrders(scr, ss);
					BuySellInfo bsi = new BuySellInfo(String.format("%s,%s", bsName, bsParams), ss, sol, dt);
					bsil.add(bsi);
				}
			}
			TreeMap<Date, CandleQuote> dcmap = TradeSimulator.getCQMap(cconf, sc, stockid, minDate, maxDate, maxHolding);
			TradeSimulator.submitStockOrder(bsil, dcmap, sc);
			
			for (BuySellInfo bsi: bsil){
				List<StockOrder> solist = bsi.getSos();
				BuySellResult bsr = TradeSimulator.calculateBuySellResult(bsi, solist);
				if (bsr!=null){
					String k = String.format("%s,%s", bsi.getBs(), bsi.getSs());
					String v = bsr.toString();
					//String filepart = bsi.getBs()+","+bsi.getSs();
					//filepart = filepart.replaceAll(":", "-").replaceAll("\\.", "_").replaceAll(",", "_");
					context.write(new Text(k), new Text(v));
				}
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
}
