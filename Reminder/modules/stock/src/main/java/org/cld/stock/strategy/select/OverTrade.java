package org.cld.stock.strategy.select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlUtil;
import org.cld.datacrawl.hadoop.CrawlTaskMapper;
import org.cld.hadooputil.DefaultCopyTextReducer;
import org.cld.stock.CandleQuote;
import org.cld.stock.CqIndicators;
import org.cld.stock.DivSplit;
import org.cld.stock.ETLUtil;
import org.cld.stock.PriceSeg;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.IntervalUnit;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.StrategyConst;
import org.cld.stock.strategy.prepare.GenCloseDropAvgForDayTask;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DataMapper;
import org.cld.util.DateTimeUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OverTrade extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(OverTrade.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final String p_threshold="scs.param.threshold";
	public static final String p_submitprice="scs.param.submitprice";
	public static final String p_submittime="scs.param.submittime";
	public static final String p_lookupdn="scs.param.lookupdn";
	public static final String p_aggregation="scs.param.aggregation";
	public static final String p_direction="scs.param.direction";
	
	public static final String v_direction_up="up";
	public static final String v_direction_down="down";
	public static final String v_aggregation_avg="avg";
	public static final String v_aggregation_sum="sum";
	public static final String v_submittime_open="open";
	public static final String v_submittime_close="close";
	
	float pthreshold=5f;
	float psubmitpriceUpRatio=0f;
	int plookupdn=0;//unlimited
	String paggregation=v_aggregation_avg;
	String pdirection=v_direction_down;
	String psubmittime=v_submittime_open;
	
	private List<CandleQuote> cqlist = new ArrayList<CandleQuote>();
	Set<Date> lxdds = new HashSet<Date>();
	
	public OverTrade(){
	}
	
	@Override
	public void init(){
		super.init();
		super.setParam(SelectStrategy.KEY_LU_UNIT, IntervalUnit.day);
		if (this.getParams().containsKey(p_threshold)){
			pthreshold = Float.parseFloat(getParams().get(p_threshold).toString());
		}
		if (this.getParams().containsKey(p_submitprice)){
			psubmitpriceUpRatio = Float.parseFloat(getParams().get(p_submitprice).toString());
		}
		if (this.getParams().containsKey(p_lookupdn)){
			plookupdn = Integer.parseInt(getParams().get(p_lookupdn).toString());
		}
		if (this.getParams().containsKey(p_aggregation)){
			paggregation = getParams().get(p_aggregation).toString();
		}
		if (this.getParams().containsKey(p_direction)){
			pdirection = getParams().get(p_direction).toString();
		}
		if (this.getParams().containsKey(p_submittime)){
			psubmittime = getParams().get(p_submittime).toString();
		}
	}
	
	@Override
	public void initData(Map<DataMapper, List<? extends Object>> resultMap){
		List<CqIndicators> cqilist = (List<CqIndicators>) resultMap.get(this.quoteMapper());
		for (CqIndicators cqi:cqilist){
			cqlist.add(cqi.getCq());
		}
		List<DivSplit> lxd = (List<DivSplit>) resultMap.get(sc.getExDivSplitHistoryTableMapper());
		for (DivSplit ds:lxd){
			lxdds.add(ds.getDt());
		}
	}
	
	@Override
	public int maxLookupNum() {
		if (plookupdn==0){
			return SelectStrategy.MAX_LOOKUP;
		}else{
			return plookupdn;
		}
	}
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{super.quoteMapper(), sc.getExDivSplitHistoryTableMapper()};
	}
	
	//use the open of submit day and submit at the open, table results are per stock
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<? extends Object>> tableResults) {
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<CandleQuote> lo = cqlist;
		for (int i=lo.size()-1; i>=0; i--){//go backward
			CandleQuote submitCq = (CandleQuote) lo.get(i);
			if (lxdds.contains(submitCq.getStartTime())){//skip ex days
				continue;
			}
			CandleQuote thisCq = submitCq;
			CandleQuote prevCq = null;
			CandleQuote oneDayBeforeSumbitCq = null;
			if (i-1>=0){
				oneDayBeforeSumbitCq = (CandleQuote) lo.get(i-1);
				float limit=0;
				int days=0;//include the submit day
				if (checkValid(oneDayBeforeSumbitCq)){
					int ndays = 0;
					if (plookupdn>0){
						ndays = plookupdn;
					}
					boolean isDay = true;//using the open-close day portion, else use the close-open night portion
					if (psubmittime.equals(v_submittime_open)){
						isDay = false;
					}else{
						isDay = true;
					}
					int j=i-1;
					prevCq = (CandleQuote)lo.get(j);
					List<PriceSeg> psl = new ArrayList<PriceSeg>();
					while (j>0){
						PriceSeg ps = null;
						if (isDay && (pdirection.equals(v_direction_down) && thisCq.getClose()<thisCq.getOpen() || 
								pdirection.equals(v_direction_up) && thisCq.getClose()>thisCq.getOpen())){
							limit = thisCq.getOpen();
							ps = new PriceSeg(PriceSeg.TAG_OPEN, thisCq.getStartTime(), thisCq.getOpen(), 
									PriceSeg.TAG_CLOSE, thisCq.getStartTime(), thisCq.getClose());
						}else if (!isDay && (pdirection.equals(v_direction_down) && thisCq.getOpen()<prevCq.getClose() ||
								pdirection.equals(v_direction_up) && thisCq.getOpen()>prevCq.getClose())){
							limit = prevCq.getClose();
							ps = new PriceSeg(PriceSeg.TAG_CLOSE, prevCq.getStartTime(), prevCq.getClose(), 
									PriceSeg.TAG_OPEN, thisCq.getStartTime(), thisCq.getOpen());
						}else{
							break;
						}
						if (ps!=null) psl.add(ps);
						if (ndays!=0 && days>ndays){
							break;
						}
						isDay = !isDay;
						days++;
						if (isDay){
							thisCq = prevCq;
							j--;
							prevCq = (CandleQuote)lo.get(j);
						}
					}
					logger.debug(String.format("psl for day %s:%s", sdf.format(submitCq.getStartTime()), psl));
					if (limit*days>0){
						float value =0f;
						if (psubmittime.equals(v_submittime_open)){
							value = limit - submitCq.getOpen();
						}else{
							value = limit - submitCq.getClose();
						}
						value = value / limit;
						if (paggregation.equals(v_aggregation_avg)){
							value= value/days;
						}
						value = Math.abs(value);
						if (value>pthreshold*0.01){
							float price = 0f;
							if (psubmittime.equals(v_submittime_open)){
								price = submitCq.getOpen()*(1+psubmitpriceUpRatio*0.01f);
								scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitCq.getStartTime()), value, price));
							}else{
								price = submitCq.getClose()*(1+psubmitpriceUpRatio*0.01f);
								scrl.add(new SelectCandidateResult(sc.getNormalTradeEndTime(submitCq.getStartTime()), value, price));
							}
							logger.info(String.format("%s has consecutive %s %s %.3f for %d nd til %s", submitCq.getStockid(), pdirection, paggregation, 
									value, days, sdf.format(submitCq.getStartTime())));
						}
					}
				}else{
					logger.debug(String.format("not valid stock %s", oneDayBeforeSumbitCq.toString()));
				}
			}
		}
		return scrl;
	}
	
	@Override
	public String[] prepareData(String baseMarketId, String marketId, CrawlConf cconf, String propfile, Date start, Date end){
		StockConfig sc = StockUtil.getStockConfig(baseMarketId);
		List<String> allIds = Arrays.asList(ETLUtil.getStockIdByMarketId(sc, marketId, cconf, ""));
		List<Task> tl = new ArrayList<Task>();
		Date day = StockUtil.getNextOpenDay(start, sc.getHolidays());
		List<String> jobidlist = new ArrayList<String>();
		while (day.before(end)){
			for (String stockid: allIds){
				Task t = new GenCloseDropAvgForDayTask(baseMarketId, stockid, sdf.format(day));
				tl.add(t);
			}
			int mbMem = 512;
			String optValue = "-Xmx" + mbMem + "M";
			Map<String, String> hadoopJobParams = new HashMap<String, String>();
			hadoopJobParams.put("mapreduce.map.speculative", "false");//since we do not allow same map multiple instance
			hadoopJobParams.put("mapreduce.map.memory.mb", mbMem+"");
			hadoopJobParams.put("mapreduce.map.java.opts", optValue);
			hadoopJobParams.put(NLineInputFormat.LINES_PER_MAP, "200");
			String taskName = String.format("%s%s", GenCloseDropAvgForDayTask.class.getSimpleName(), sdf.format(day));
			String jobId = CrawlUtil.hadoopExecuteCrawlTasksWithReducer(propfile, cconf, tl, taskName, false, 
					CrawlTaskMapper.class, DefaultCopyTextReducer.class, hadoopJobParams);
			jobidlist.add(jobId);
			tl.clear();
			day = StockUtil.getNextOpenDay(day, sc.getHolidays());
		}
		String[] jobidarray = new String[jobidlist.size()];
		return jobidlist.toArray(jobidarray);
	}
	
	//preparedData is adjusted, 
	private List<SelectCandidateResult> select(Map<String, String> preparedData, Map<String, Float> newQuotes, 
			Date submitDay, int n){
		TreeMap<Float, List<SelectCandidateResult>> map = new TreeMap<Float, List<SelectCandidateResult>>();
		float threashold = 0f;//TODO
		float openUpRatio = 0f;//TODO
		boolean orderDir = getOrderDirection().equals(StrategyConst.V_ASC);
		for (String stockId:newQuotes.keySet()){
			float newQuote = newQuotes.get(stockId);
			String preparedValues = preparedData.get(stockId);
			if (preparedValues!=null){
				String[] vs = preparedValues.split(",");
				float adjHigh = Float.parseFloat(vs[0]);
				float adjLastClose = Float.parseFloat(vs[1]);
				int days = Integer.parseInt(vs[2]);
				float value=0;
				if (newQuote<adjLastClose){
					value = (adjHigh-newQuote)/(adjHigh*(days+1));
					if (value>threashold*0.01){
						float price = newQuote*(1+openUpRatio*0.01f);
						List<SelectCandidateResult> scrl = map.get(value);
						if (scrl==null){
							scrl = new ArrayList<SelectCandidateResult>();
							map.put(value, scrl);
						}
						scrl.add(new SelectCandidateResult(stockId, submitDay, value, price));
					}
				}
			}
		}
		
		Iterator<Float> kl = null;
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		if (orderDir){
			kl = map.keySet().iterator();
		}else{
			kl = map.descendingKeySet().iterator();
		}
		int cnt=0;
		while(cnt<n){
			if (kl.hasNext()){
				Float f = kl.next();
				List<SelectCandidateResult> l = map.get(f);
				int i=0;
				for (i=0; i<l.size();i++){
					SelectCandidateResult scr = l.get(i);
					logger.info(String.format("number %d:%s", i, scr));
					scrl.add(scr);
				}
				cnt+=(i+1);
			}else{
				break;
			}
		}
		return scrl;
	}
	
	@Override
	public List<SelectCandidateResult> selectByCurrent(CrawlConf cconf, String baseMarketId, String marketId, 
			Date submitDay, int n, Map<String, Float> newQuotes){
		StockConfig sc = StockUtil.getStockConfig(baseMarketId);
		Date lastDay = StockUtil.getLastOpenDay(DateTimeUtil.yesterday(submitDay), sc.getHolidays());
		//stockId, whichDay, adjhigh, lastadjclose, days
		String inputFile = String.format("/reminder/sresult/prepare/%s/%s_%s/part-r-00000", GenCloseDropAvgForDayTask.class.getSimpleName(), 
				baseMarketId, sdf.format(lastDay));
		Configuration conf = HadoopTaskLauncher.getHadoopConf(cconf.getNodeConf());
		try {
			FileSystem fs = FileSystem.get(conf);
			Path fileNamePath = new Path(inputFile);
			FSDataInputStream fin = fs.open(fileNamePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
			String line = null;
			Map<String, String> preparedData = new HashMap<String, String>();
			while ((line = br.readLine()) != null) {
				String[] vs = line.split(",");
				if (vs.length==5){
					preparedData.put(vs[0], vs[2]+","+vs[3]+","+vs[4]);
				}
			}
			br.close();
			Set<String> xstockids = new HashSet<String>();
			if (newQuotes!=null){
				//new quotes are passed in
				//div, split are fetched from future table
				List<Object> xl = StockPersistMgr.getDataByDate(cconf.getSmalldbconf(), sc.getDividendTableMapper(), submitDay, "EffDate");
				xl.addAll(StockPersistMgr.getDataByDate(cconf.getSmalldbconf(), sc.getSplitTableMapper(), submitDay, "exdt"));
				for (Object o:xl){
					DivSplit ds = (DivSplit)o;
					if (newQuotes.containsKey(ds.getStockid())){
						newQuotes.remove(ds.getStockid());
					}
				}
			}else{
				//new quotes are null for validate
				List<Object> cql = StockPersistMgr.getDataByDate(cconf.getSmalldbconf(), sc.getFQDailyQuoteTableMapper(), submitDay);
				newQuotes = new HashMap<String, Float>();
				for (Object o:cql){
					CandleQuote cq = (CandleQuote)o;
					if (!xstockids.contains(cq.getStockid())){
						newQuotes.put(cq.getStockid(), cq.getOpen());
					}
				}
				//x-div-split data are fetched from history table
				List<Object> xl = StockPersistMgr.getDataByDate(cconf.getSmalldbconf(), sc.getExDivSplitHistoryTableMapper(), submitDay);
				for (Object o:xl){
					DivSplit ds = (DivSplit)o;
					if (newQuotes.containsKey(ds.getStockid())){
						newQuotes.remove(ds.getStockid());
					}
				}
			}
			return select(preparedData, newQuotes, submitDay, n);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
}
