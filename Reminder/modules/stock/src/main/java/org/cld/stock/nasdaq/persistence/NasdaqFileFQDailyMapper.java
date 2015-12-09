package org.cld.stock.nasdaq.persistence;

import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.CandleQuote;
import org.cld.util.FileDataMapper;

public class NasdaqFileFQDailyMapper extends FileDataMapper{
	Logger logger = LogManager.getLogger(NasdaqFileFQDailyMapper.class);
	
	private NasdaqFileFQDailyMapper(){
		
	}
	private static NasdaqFileFQDailyMapper singleton = new NasdaqFileFQDailyMapper();
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	public static NasdaqFileFQDailyMapper getInstance(){
		return singleton;
	}
/*
 * date,     open, high, low, close
 * 02/04/2013,31.09,31.21,30.45,30.71,4931239
*/
	@Override
	public Object getObject(String line) {
		String[] csv = line.split(",");
		if (csv.length==6){
			try{
				CandleQuote cq = new CandleQuote(null, sdf.parse(csv[0]), Float.parseFloat(csv[1]), 
					Float.parseFloat(csv[2]), Float.parseFloat(csv[4]), Float.parseFloat(csv[3]), Float.parseFloat(csv[5]));
				cq.setFqIdx(1);
				return cq;
			}catch(Exception e){
				logger.error("", e);
			}
		}
    	return null;
	}

	@Override
	public String getFileName(String stockId) {
		String fn = stockId.replace("^", ".");
		return String.format("/reminder/nasdaq/daily/%s.txt", fn);
	}
	
	@Override
	public boolean oneFetch() {
		return true;
	}
}
