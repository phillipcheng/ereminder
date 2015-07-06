package org.cld.util.test;

import org.apache.commons.io.FilenameUtils;
import org.cld.util.DownloadUtil;
import org.junit.Test;

public class TestDownloadUtil {
	
	private String fileUrl = "http://www.hkexnews.hk/listedco/listconews/SEHK/2015/0109/LTN20150109206_C.pdf";
	private String fileUrl2 = "http://money.finance.sina.com.cn/corp/go.php/vDOWN_BalanceSheet/displaytype/4/stockid/000004/ctrl/all.phtml";
	private String fileUrl3 = "http://money.finance.sina.com.cn/corp/go.php/vDOWN_BalanceSheet/displaytype/4/stockid/000007/ctrl/all.phtml";
	
	public void testDownloadToFS(){
		
		DownloadUtil.downloadFile(fileUrl, false, null, 0, "0001", FilenameUtils.getName(fileUrl));
	}
	
	@Test
	public void testDownloadToFS2(){
		
		DownloadUtil.downloadFile(fileUrl2, false, null, 0, "000004", "000004");
	}
	
	public void testDownloadToHdfs(){
		
		DownloadUtil.downloadFileToHdfs(fileUrl, false, null, 0, 
				"hdfs://localhost:19000/tmp/" + FilenameUtils.getName(fileUrl), "hdfs://localhost:19000");
	}
	
	@Test
	public void testDownloadToHdfs2(){
		
		DownloadUtil.downloadFileToHdfs(fileUrl3, false, null, 0, 
				"hdfs://localhost:19000/reminder/" + "000007", "hdfs://localhost:19000");
	}
	

	public void testDownloadFromProxy(){
		
		DownloadUtil.downloadFile(fileUrl, true, "16.85.88.10", 8080, "0001", "Proxy"+FilenameUtils.getName(fileUrl));
	}
}
