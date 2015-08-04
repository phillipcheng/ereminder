package org.cld.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadUtil {
	
	private static Logger logger =  LogManager.getLogger(DownloadUtil.class);
	
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
	public static void downloadFile(String url, boolean useProxy, String proxyIp, int port, 
			String directory, String fileName){
		InputStream is = null;
		FileOutputStream fos = null;
		try{
			int tryMax=3;
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
	
	public static void downloadFileToHdfs(String url, boolean useProxy, String proxyIp, int port, 
			String filePath, String fsDefaultName){
		InputStream is = null;
		FSDataOutputStream fos = null;
		FileSystem fs = null;
		
		try{
			Configuration conf = new Configuration();
			conf.set("fs.defaultFS", fsDefaultName);
			fs = FileSystem.get(conf);//fs can't be closed
			is = getInputStream(url, useProxy, proxyIp, port);
			Path fileNamePath = new Path(filePath);
			fos = fs.create(fileNamePath);
			fos.write(IOUtils.toByteArray(is));
		}catch(Exception e){
			logger.error("",e);
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
}
