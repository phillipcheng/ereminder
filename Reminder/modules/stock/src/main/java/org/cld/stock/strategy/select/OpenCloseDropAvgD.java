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
import org.cld.stock.DivSplit;
import org.cld.stock.ETLUtil;
import org.cld.stock.StockConfig;
import org.cld.stock.StockUtil;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.stock.strategy.SelectCandidateResult;
import org.cld.stock.strategy.SelectStrategy;
import org.cld.stock.strategy.prepare.GenCloseDropAvgForDayTask;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.hadoop.HadoopTaskLauncher;
import org.cld.util.DataMapper;
import org.cld.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class OpenCloseDropAvgD extends SelectStrategy {
	public static Logger logger = LogManager.getLogger(OpenCloseDropAvgD.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private StockConfig sc;
	
	public OpenCloseDropAvgD(){
	}
	
	//init after json deserilized
	public void init(){
		super.init();
		sc = StockUtil.getStockConfig(this.getBaseMarketId());
	}
	
	//preparedData is adjusted, 
	public List<SelectCandidateResult> select(Map<String, String> preparedData, Map<String, Float> newQuotes, 
			Date submitDay, int n){
		TreeMap<Float, List<SelectCandidateResult>> map = new TreeMap<Float, List<SelectCandidateResult>>();
		float threashold = (float)getParams()[0];//drop percentage 5 mean 5%
		float openUpRatio = (float)getParams()[1];//the limit price ratio submit to open price -1 mean -1%
		boolean orderDir = getOrderDirection().equals(SelectStrategy.ASC);
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
	
	@JsonIgnore
	@Override
	public DataMapper[] getDataMappers() {
		return new DataMapper[]{sc.getBTFQDailyQuoteMapper(), sc.getExDivSplitHistoryTableMapper()};
	}
	
	//use the open of submit day and submit at the open, table results are per stock
	@Override
	public List<SelectCandidateResult> selectByHistory(Map<DataMapper, List<Object>> tableResults) {
		Object[] params = this.getParams();
		float threashold = ((Double)params[0]).floatValue();//drop percentage 5 mean 5%
		float openUpRatio = ((Double)params[1]).floatValue();//the limit price ratio submit to open price -1 mean -1%
		List<SelectCandidateResult> scrl = new ArrayList<SelectCandidateResult>();
		List<Object> lxd = tableResults.get(sc.getExDivSplitHistoryTableMapper());
		Set<Date> lxdds = new HashSet<Date>();
		for (Object o:lxd){
			DivSplit ds = (DivSplit)o;
			lxdds.add(ds.getDt());
		}
		List<Object> lo = tableResults.get(sc.getBTFQDailyQuoteMapper());
		for (int i=lo.size()-1; i>=0; i--){
			CandleQuote submitCq = (CandleQuote) lo.get(i);
			if (lxdds.contains(submitCq.getStartTime())){//skip ex days
				continue;
			}
			CandleQuote thisCq = submitCq;
			CandleQuote prevCq = null;
			CandleQuote oneDayBeforeSumbitCq = null;
			if (i-1>=0){
				oneDayBeforeSumbitCq = (CandleQuote) lo.get(i-1);
				float high=0;
				int days=0;//include the submit day
				if (checkValid(oneDayBeforeSumbitCq)){
					for (int j=i-1; j>0; j--){
						prevCq = (CandleQuote)lo.get(j);
						if (j==i-1){
							//for the day before submit day, i need to make sure the open of submit day is lower then the close of prev day
							if (thisCq.getOpen()>=prevCq.getClose()){
								break;
							}
						}else{
							if (thisCq.getClose()>=prevCq.getClose()){
								break;
							}
						}
						days++;
						high = prevCq.getClose();
						thisCq = prevCq;
					}
					if (high>0){
						if (DateTimeUtil.DateDiff(oneDayBeforeSumbitCq.getStartTime().getTime(),thisCq.getStartTime().getTime())<=LOOKUP_DAYS){
							float value = (high-submitCq.getOpen())/(high*days);
							if (value>threashold*0.01){
								float price = submitCq.getOpen()*(1+openUpRatio*0.01f);
								scrl.add(new SelectCandidateResult(sc.getNormalTradeStartTime(submitCq.getStartTime()), value, price));
								logger.info(String.format("%s has consecutive drop %.3f for %d days til %s", submitCq.getStockid(), value, days, 
										sdf.format(submitCq.getStartTime())));
							}
						}else{
							logger.info(String.format("%s has used data %d days before. ignored.", submitCq.getStockid(), LOOKUP_DAYS));
						}
					}
				}
			}
		}
		return scrl;
	}
}
