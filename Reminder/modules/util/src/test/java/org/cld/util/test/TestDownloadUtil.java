package org.cld.util.test;

import org.apache.commons.io.FilenameUtils;
import org.cld.util.DownloadUtil;
import org.junit.Test;

public class TestDownloadUtil {
	
	private String fileUrl = "http://www.hkexnews.hk/listedco/listconews/SEHK/2015/0109/LTN20150109206_C.pdf";
	
	public void testDownloadToFS(){
		
		DownloadUtil.downloadFile(fileUrl, false, null, 0, "0001", FilenameUtils.getName(fileUrl));
	}
	
	public void testDownloadToHdfs(){
		
		DownloadUtil.downloadFileToHdfs(fileUrl, false, null, 0, "hdfs://localhost:19000/tmp/" + FilenameUtils.getName(fileUrl), "hdfs://localhost:19000");
	}
	

	public void testDownloadFromProxy(){
		
		DownloadUtil.downloadFile(fileUrl, true, "16.85.88.10", 8080, "0001", "Proxy"+FilenameUtils.getName(fileUrl));
	}
}
