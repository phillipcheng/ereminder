package org.cld.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadUtil implements Runnable {
	
	private static Logger logger =  LogManager.getLogger(DownloadUtil.class);
	
	public static int tryMax = 3;
	
	private static InputStream getInputStream(String url, boolean useProxy, String proxyIp, int port){
		InputStream is=null;
		try{
			URL website = new URL(url);
			if (!useProxy){
				is = website.openStream();
			}else{
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, port));
				is = website.openConnection(proxy).getInputStream();
			}
		}catch(Exception e){
			logger.warn("", e);
		}
		return is;
	}
	
	public static void downloadFile(String url, String directory, String fileName){
		downloadFile(url, false, null, 0, directory, fileName);
	}
	
	public static void downloadFile(String url, boolean useProxy, String proxyIp, int port, 
			String directory, String fileName){
		InputStream is = null;
		FileOutputStream fos = null;
		try{
			int tryNum=0;
			while (is==null && tryNum<tryMax){
				is = getInputStream(url, useProxy, proxyIp, port);
				tryNum++;
			}
			if (is!=null){
				new File(directory).mkdirs();
				ReadableByteChannel rbc = Channels.newChannel(is);
				fos = new FileOutputStream(directory + File.separator + fileName);
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			}else{
				logger.error(String.format("try max %d reached.", tryNum));
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try{
				if (is!=null){
					is.close();
				}
				if (fos!=null){
					fos.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}
	
	public static void outputToFile(String[] lines, String directory, String fileName){
		FileOutputStream fos = null;
		BufferedWriter bw = null;
		try{
			new File(directory).mkdirs();
			fos = new FileOutputStream(directory + File.separator + fileName);
			bw = new BufferedWriter(new OutputStreamWriter(fos));
			for (String line:lines){
				bw.write(line);
				bw.write("\n");
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try{
				if (bw!=null){
					bw.close();
				}
			}catch(Exception e){
				logger.error("", e);
			}
		}
	}

	public DownloadUtil(String rootDir, List<String> urls){
		this.rootDir = rootDir;
		this.urls = urls;
	}
	private List<String> urls;
	private String rootDir;
	@Override
	public void run() {
		for (String url:urls){
			String fileName = (new File(url)).getName();
			logger.info(String.format("download %s to %s from url:%s", fileName, rootDir, url));
			DownloadUtil.downloadFile(url, rootDir, fileName);
		}
	}
}
