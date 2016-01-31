package org.cld.util.test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.imagedata.ImageDataUtil;
import org.cld.util.DownloadUtil;
import org.cld.util.StringUtil;
import org.junit.Test;

public class TestFileDownload {
	private static Logger logger =  LogManager.getLogger(TestFileDownload.class);
	

	@Test
	public void testSanGuoYanYi() throws Exception {
		ExecutorService exeService = Executors.newFixedThreadPool(20);
		String webRoot = "http://www.7zzy.com";
		String urlRoot = "http://www.7zzy.com/d/file/dianzikeben/xml/";
		String localRoot = "C:\\mydoc\\picbook";
		String bookSeries = "SanGuoYanYi";
		for (int i=3; i<=60; i++){
			String bookIdx = StringUtil.getStringFromNum(i, 2);
			List<String> relImageUrls = ImageDataUtil.getImageFilesFromIdxFile(String.format("%s%s%s%s", urlRoot, bookSeries, bookIdx, ".xml"));
			String directory = String.format("%s%s%s%s", localRoot, File.separator, bookSeries, bookIdx);
			List<String> absImageUrls = new ArrayList<String>();
			for (String relImageUrl:relImageUrls){
				String imageUrl = String.format("%s%s", webRoot, relImageUrl);
				absImageUrls.add(imageUrl);
			}
			DownloadUtil du = new DownloadUtil(directory, absImageUrls);
			exeService.submit(du);
		}
		exeService.shutdown();
		exeService.awaitTermination(4, TimeUnit.HOURS);
	}
}
