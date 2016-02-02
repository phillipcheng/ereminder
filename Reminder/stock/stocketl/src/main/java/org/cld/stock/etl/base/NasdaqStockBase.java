package org.cld.stock.etl.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cld.stock.common.StockConfig;
import org.cld.stock.common.StockUtil;
import org.cld.stock.config.NasdaqStockConfig;
import org.cld.stock.etl.StockBase;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskResult;
import org.cld.taskmgr.entity.Task;
import org.cld.util.entity.CrawledItem;

public class NasdaqStockBase extends StockBase{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public NasdaqStockBase(String propFile, String marketId, Date sd, Date ed){
		super(propFile, StockUtil.NASDAQ_STOCK_BASE, marketId, sd, ed, StockUtil.NASDAQ_STOCK_BASE);
	}

	public static final String[] tryIds = new String[]{"FIT","AAPL","BABA", "IBM", "HPQ", "VIPS", "JD", "Z", "ZNGA"};
	
	@Override
	public boolean fqReady(Date today) {
		try{
			String checkFqCmdFile = "nasdaq-quote-fq-check.xml";
			List<Task> tl = cconf.setUpSite(checkFqCmdFile, null);
			int readyCount=0;
			if (tl.size()>0){
				Task t = tl.get(0);
				t.initParsedTaskDef();
				for (String stockid: tryIds){
					Map<String, Object> crawlTaskParams = new HashMap<String, Object>();
					crawlTaskParams.put(TaskMgr.TASK_RUN_PARAM_CCONF, cconf);
					crawlTaskParams.put("stockid", stockid);
					TaskResult tr = t.runMyself(crawlTaskParams, false, null, null);
					if (tr!=null && tr.getCIs()!=null && tr.getCIs().size()>0){
						CrawledItem ci = tr.getCIs().get(0);
						//return list of csv tuple: key, value
						String[][] csv = ci.getCsvValue();
						if (csv.length>=1){
							String[] kv = csv[0];
							String v = kv[1];
							String todayStr = sdf.format(today);
							if (todayStr.equals(v)){
								readyCount++;
							}else{
								logger.info(String.format("not ready yet. expect %s, get %s", todayStr, v));
							}
						}else{
							logger.error(String.format("csv not returned for stock %s", stockid));
						}
					}else{
						logger.error(String.format("ci list size<=0 for stock %s", stockid));
					}
				}
				if (readyCount>(tryIds.length*0.9)){
					logger.info(String.format("more than 90 percent is ready. readyCount %d, total %s", readyCount, tryIds.length));
					return true;
				}
			}else{
				logger.error(String.format("task list size<=0 for %s", checkFqCmdFile));
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return false;
	}
}
