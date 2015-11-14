package org.cld.stock.strategy.prepare;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.stock.CandleQuote;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.select.CloseDropAvgSS;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.entity.Task;


public class GenCloseDropAvgForDayTask extends Task implements Serializable{
	private static final long serialVersionUID = 1L;
	private static Logger logger =  LogManager.getLogger(GenCloseDropAvgForDayTask.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String stockId;
	private String whichDay;
	private String baseMarketId;
	
	public GenCloseDropAvgForDayTask(){	
	}
	
	public GenCloseDropAvgForDayTask(String baseMarketId, String stockId, String whichDay){
		this.stockId = stockId;
		this.whichDay = whichDay;
		this.setBaseMarketId(baseMarketId);
		genId();
	}
	
	@Override
	public boolean hasOutput(){
		return true;
	}
	
	public String getOutputDir(Map<String, Object> paramMap){
		return String.format("/reminder/sresult/prepare/%s/%s_%s", GenCloseDropAvgForDayTask.class.getSimpleName(), baseMarketId, whichDay);
	}
	
	@Override
	public String genId(){
		String inputId = String.format("%s%s%s", GenCloseDropAvgForDayTask.class.getSimpleName(), stockId, whichDay);
		this.setId(inputId);
		return this.getId();
	}
	
	@Override
	public void runMyselfAndOutput(Map<String, Object> params,
			MapContext<Object, Text, Text, Text> context, MultipleOutputs<Text, Text> mos) throws InterruptedException{
		try{
			CrawlConf cconf = (CrawlConf) params.get(TaskMgr.TASK_RUN_PARAM_CCONF);
			StockConfig sc = StockUtil.getStockConfig(baseMarketId);
			List<Object> lo = StockPersistMgr.getDataByStockDateLimit(cconf.getSmalldbconf(), sc.getFQDailyQuoteTableMapper(), 
					stockId, sdf.parse(whichDay), CloseDropAvgSS.LOOKUP_DAYS);
			if (lo.size()>0){
				CandleQuote startCq =(CandleQuote) lo.get(0);
				CandleQuote thisCq = startCq;
				CandleQuote prevCq = null;
				float high=0;
				int days=0;//include the submit day
				if (startCq.getStartTime().equals(sdf.parse(whichDay))){
					if (SelectStrategy.checkValid(thisCq)){
						if (lo.size()>1){
							for (int j=1; j<lo.size(); j++){
								prevCq = (CandleQuote)lo.get(j);
								if (thisCq.getClose()>=prevCq.getClose()){
									break;
								}
								days++;
								high = prevCq.getClose();
								thisCq = prevCq;
							}
							String ok = String.format("%s,%s", stockId, whichDay);
							String ov;
							if (high>0){
								ov = String.format("%.3f,%.3f,%d", high, startCq.getClose(), days);
							}else{
								ov = String.format("%.3f,%.3f,%d", startCq.getClose(), startCq.getClose(), 0);
							}
							context.write(new Text(ok), new Text(ov));
						}
					}
				}else{
					logger.error(String.format("data for %s on %s not there.", stockId, whichDay));
				}
			}else{
				logger.error(String.format("no data for %s for the past %d calendar days.", stockId, CloseDropAvgSS.LOOKUP_DAYS));
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getWhichDay() {
		return whichDay;
	}

	public void setWhichDay(String whichDay) {
		this.whichDay = whichDay;
	}

	public String getBaseMarketId() {
		return baseMarketId;
	}

	public void setBaseMarketId(String baseMarketId) {
		this.baseMarketId = baseMarketId;
	}
}
