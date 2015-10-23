package org.cld.stock.strategy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.hadoop.CrawlTaskMapper;
import org.cld.datastore.DBConnConf;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.strategy.SelectStrategy.byDayType;
import org.cld.stock.trade.StockOrder;
import org.cld.stock.trade.StockOrder.*;
import org.cld.taskmgr.entity.Task;
import org.cld.util.DateTimeUtil;
import org.cld.stock.trade.TradeSimulator;

public class StrategyValidationTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(StrategyValidationTask.class);
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat detailSdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	private SelectStrategy scs;
	private SellStrategy sls;
	private Date startDate;
	private String marketBaseId;
	private DBConnConf dbconf;
	
	public StrategyValidationTask(){
	}
	
	public StrategyValidationTask(SelectStrategy scs, SellStrategy sls, Date startDate, String marketBaseId, DBConnConf dbconf){
		this.scs = scs;
		this.sls = sls;
		this.startDate = startDate;
		this.marketBaseId = marketBaseId;
		this.setDbconf(dbconf);
		genId();
	}
	
	@Override
	public String genId(){
		String id = String.format("%s_%s", StrategyValidationTask.class.getSimpleName(), sdf.format(startDate));
		this.setId(id);
		return this.getId();
	}

	@Override
	public boolean hasOutput(){
		return true;
	}
	
	@Override
	public boolean hasMultipleOutput(){
		return true;
	}
	
	@Override
	public String getOutputDir(Map<String, Object> paramMap){
		return String.format("%s/reduce/", scs.getOutputDir());
	}
	
	@Override
	public void runMyselfAndOutput(Map<String, Object> params, 
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		try{
			Date sd = startDate;
			StockConfig sc = StockUtil.getStockConfig(marketBaseId);
			//valid one round
			List<String> stockidList = scs.select(dbconf, sd, sc);//using data before and including sd
			Map<String, List<StockOrder>> map = new HashMap<String, List<StockOrder>>();
			int stockNum = stockidList.size()>scs.getLimit() ? scs.getLimit():stockidList.size();
			Date submitDt = sd;
			if (scs.getDayType()==byDayType.byCalendarDay){
				submitDt = StockUtil.getNextOpenDay(DateTimeUtil.yesterday(sd), sc.getHolidays());
			}
			for (int i=0; i<stockNum; i++){
				String stockid = stockidList.get(i);
				List<StockOrder> sol = new ArrayList<StockOrder>();
				
				StockOrder marketBuyOrder = new StockOrder();
				marketBuyOrder.setOrderId(stockid);
				marketBuyOrder.setAction(ActionType.buy);
				marketBuyOrder.setOrderType(OrderType.market);
				marketBuyOrder.setSubmitTime(submitDt);
				marketBuyOrder.setDuration(sls.getHoldDuration());//TODO
				sol.add(marketBuyOrder);
				
				if (sls.getLimitPercentage()!=0){
					StockOrder limitSellOrder = new StockOrder();
					limitSellOrder.setStockid(stockid);
					limitSellOrder.setAction(ActionType.sell);
					limitSellOrder.setOrderType(OrderType.limit);
					limitSellOrder.setLimitPercentage(sls.getLimitPercentage());
					limitSellOrder.setPairOrderId(marketBuyOrder.getOrderId());
					sol.add(limitSellOrder);
				}
				
				if (sls.getStopTrailingPercentage()!=0){
					StockOrder limitTrailSellOrder = new StockOrder();
					limitTrailSellOrder.setStockid(stockid);
					limitTrailSellOrder.setAction(ActionType.sell);
					limitTrailSellOrder.setOrderType(OrderType.stoptrailingpercentage);
					limitTrailSellOrder.setIncrementPercent(sls.getStopTrailingPercentage());
					sol.add(limitTrailSellOrder);
				}
				
				StockOrder forceCleanSellOrder = new StockOrder();
				forceCleanSellOrder.setStockid(stockid);
				forceCleanSellOrder.setAction(ActionType.sell);
				forceCleanSellOrder.setOrderType(OrderType.forceclean);
				sol.add(forceCleanSellOrder);
				
				map.put(stockid, sol);
			}
			TradeSimulator.submitDailyOrder(map, dbconf, sc);
			for (String stockid:map.keySet()){
				List<StockOrder> solist = map.get(stockid);
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
					output = String.format("%s, %s, %s, %s, %s, %s, %s, %s", sdf.format(sd), stockid, 
							buyTime, buyPrice, sellTime, sellOrderType, sellPrice, percent);
				}else{
					output = String.format("%s, %s, %s, %s, %s, %s, %s, %s", sdf.format(sd), stockid, "-", "-", "-", "-", "-", "-");
				}
				context.write(new Text(sdf.format(sd)), new Text(output));
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	private static String submitTasks(String taskName, String propfile, List<Task> tl, CrawlConf cconf){
		int mbMem = 512;
		String optValue = "-Xmx" + mbMem + "M";
		Map<String, String> hadoopJobParams = new HashMap<String, String>();
		hadoopJobParams.put("mapreduce.map.speculative", "false");
		hadoopJobParams.put("mapreduce.map.memory.mb", mbMem + "");
		hadoopJobParams.put("mapreduce.map.java.opts", optValue);
		hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "1");
		return CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, false, 
				CrawlTaskMapper.class.getName(), StrategyResultReducer.class.getName(), hadoopJobParams);
	}
	
	public static String[] launch(String propfile, CrawlConf cconf, String marketBaseId, SelectStrategy scs, SellStrategy sls, 
			Date startDate, Date endDate, DBConnConf dbconf) {
		StockConfig sc = StockUtil.getStockConfig(marketBaseId);
		Date sd = startDate;
		List<Task> tl = new ArrayList<Task>();
		if (scs.getDayType()==byDayType.byTradingDay){
			if (!StockUtil.isOpenDay(startDate, sc.getHolidays())){
				sd = StockUtil.getNextOpenDay(startDate, sc.getHolidays());
			}
		}else if (scs.getDayType()==byDayType.byCalendarDay){
			sd = startDate;
		}else{
			logger.error("unsupported byDayType: " + scs.getDayType());
		}
		
		while(sd.before(endDate)){
			Task t = new StrategyValidationTask(scs, sls, sd, marketBaseId, dbconf);
			tl.add(t);
			if (scs.getDayType()==byDayType.byTradingDay){
				sd = StockUtil.getNextOpenDay(sd, sc.getHolidays());
			}else if (scs.getDayType()==byDayType.byCalendarDay){
				sd = DateTimeUtil.tomorrow(sd);
			}
		}
		String taskName = String.format("%s_%s", StrategyValidationTask.class.getSimpleName(), sdf.format(startDate));
		String jobId = submitTasks(taskName, propfile, tl, cconf);
		return new String[]{jobId};
	}
	
	public SelectStrategy getScs() {
		return scs;
	}
	public void setScs(SelectStrategy scs) {
		this.scs = scs;
	}
	public SellStrategy getSls() {
		return sls;
	}
	public void setSls(SellStrategy sls) {
		this.sls = sls;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getMarketBaseId() {
		return marketBaseId;
	}
	public void setMarketBaseId(String marketBaseId) {
		this.marketBaseId = marketBaseId;
	}

	public DBConnConf getDbconf() {
		return dbconf;
	}

	public void setDbconf(DBConnConf dbconf) {
		this.dbconf = dbconf;
	}
}
