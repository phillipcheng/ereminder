package org.cld.stock;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.persistence.StockPersistMgr;
import org.cld.util.FileDataMapper;

public class HdfsReader {
	private static Logger logger =  LogManager.getLogger(HdfsReader.class);
	private FileSystem fs;
	private BufferedReader br;
	private FileDataMapper fdm;
	
	List<CandleQuote> cqCache = new ArrayList<CandleQuote>();
	
	public HdfsReader(FileSystem fs, BufferedReader br, FileDataMapper fdm){
		this.fs = fs;
		this.br = br;
		this.fdm = fdm;
	}
	
	public void close(){
		cqCache.clear();
		try{
			br.close();
			fs.close();
		}catch(Exception e){
			logger.error("error when close.", e);
		}
	}
	
	private List<CandleQuote> getFromCache(List<CandleQuote> cacheQuote, Date start, Date end){
		List<CandleQuote> cql = new ArrayList<CandleQuote>();
		for (CandleQuote cq: cacheQuote){
			if (!cq.getStartTime().before(start)){//[start
				if (cq.getStartTime().before(end)){//,end)
					cql.add(cq);
				}else{
					break;
				}
			}
		}
		return cql;
	}
	
	private void optiCache(List<CandleQuote> cacheQuote, Date start){
		int numberToRemove=0;
		for (int i=0; i<cacheQuote.size(); i++){
			CandleQuote cq = cacheQuote.get(i);
			if (!cq.getStartTime().before(start)){
				numberToRemove = i;
				break;
			}
		}
		logger.debug(String.format("number of items to remove from cache:%d", numberToRemove));
		for (int i=0; i<numberToRemove; i++){
			cacheQuote.remove(0);
		}
	}
	
	public List<CandleQuote> getData(Date startDt, Date endDt){
		List<CandleQuote> moreCq = null;
		if (cqCache.size()==0){
			moreCq = StockPersistMgr.getBTDDate(br, fdm, startDt, endDt);
			cqCache.addAll(moreCq);
			logger.debug(String.format("number of items to add to cache:%d", moreCq.size()));
		}else{
			if (endDt.after(cqCache.get(cqCache.size()-1).getStartTime())){
				moreCq = StockPersistMgr.getBTDDate(br, fdm, null, endDt);//from current mark to ed
				cqCache.addAll(moreCq);
				logger.debug(String.format("number of items to add to cache:%d", moreCq.size()));
			}
		}
		List<CandleQuote> myCq = getFromCache(cqCache, startDt, endDt);
		optiCache(cqCache, startDt);
		return myCq;
	}

	public FileSystem getFs() {
		return fs;
	}

	public void setFs(FileSystem fs) {
		this.fs = fs;
	}

	public BufferedReader getBr() {
		return br;
	}

	public void setBr(BufferedReader br) {
		this.br = br;
	}

}
