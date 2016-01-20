package org.cld.stock.mapper.ext;

import java.io.File;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.common.CandleQuote;
import org.cld.util.FileDataMapper;
import org.cld.util.FsType;
import org.cld.util.SafeSimpleDateFormat;

public class NasdaqFileFQMinuteMapper extends FileDataMapper{
	Logger logger = LogManager.getLogger(NasdaqFileFQMinuteMapper.class);
	
	private NasdaqFileFQMinuteMapper(){
	}
	private static NasdaqFileFQMinuteMapper singleton = new NasdaqFileFQMinuteMapper();
	private static final SafeSimpleDateFormat sdf = new SafeSimpleDateFormat("MM/dd/yyyy HH:mm");

	public static NasdaqFileFQMinuteMapper getInstance(){
		return singleton;
	}
/*
 * Date,Time,Open,High,Low,Close,Volume
 * 12/08/2010,16:20,25.82,25.82,25.82,25.82,29114
*/
	@Override
	public Object getObject(String line) {
		String[] csv = line.split(",");
		if (csv.length==7){
			try{
				Date dt = sdf.parse(String.format("%s %s", csv[0], csv[1]));
				CandleQuote cq = new CandleQuote(null, dt, Float.parseFloat(csv[2]), 
					Float.parseFloat(csv[3]), Float.parseFloat(csv[5]), Float.parseFloat(csv[4]), Float.parseFloat(csv[6]));
				cq.setFqIdx(1);
				return cq;
			}catch(Exception e){
				logger.error("", e);
			}
		}
    	return null;
	}

	@Override
	public String getFileName(String stockId, FsType fsType) {
		String fn = stockId.replace("^", ".");
		if (fsType == FsType.hdfs){
			return String.format("%s/min/%s.txt", this.getRootFolder(), fn);
		}else if (fsType == FsType.local){
			return String.format("%s%smin%s%s.txt", this.getRootFolder(), File.separator, File.separator, fn);
		}else{
			logger.error(String.format("unsupported fsType:%s", fsType));
			return null;
		}
	}
	
	@Override
	public boolean oneFetch() {
		return false;
	}

}
