package org.cld.util.reload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.StringUtil;
import org.cld.util.TimeUtil;

public class ClassReloadFactory {
		
	public static String CLASS_SUFFIX = "class";
	public static String[] fileSuffix = new String[]{CLASS_SUFFIX};
	
	private static Logger logger = LogManager.getLogger(ClassReloadFactory.class);
	
	private Map<String, TimestampedItem<Class<?>>> itemMap = new TreeMap<String, TimestampedItem<Class<?>>>();
	private Map<String, TimestampedItem<Class<?>>> updatedMap = new TreeMap<String, TimestampedItem<Class<?>>>();
	
	private String pluginDir[] = null;
	private String pluginJar[] = null;
	
	public ClassReloadFactory(String[] dir, String[] jar){
		if (dir == null){
			pluginDir = new String[]{};
		}else{
			pluginDir = StringUtil.getNotNullStringArray(dir);
		}
		if (jar == null){
			pluginJar = new String[]{};
		}else{
			pluginJar = StringUtil.getNotNullStringArray(jar);
		}
	}
	
	public TimestampedItem<Class<?>> getReloadable(String name){
		//fetch from updatedMap 1st, then all itemMap
		TimestampedItem<Class<?>> ti = updatedMap.get(name);
		if (ti != null){
			ti.setUpdated(true);
			return ti;
		}else{
			ti = itemMap.get(name);
			if (ti!=null){
				ti.setUpdated(false);
				return ti;
			}else{
				logger.error("class not found in reloading resources:" + name);
				return null;
			}
		}
	}
	
	/**
	 * 
	 * @param pluginDir: the dir to look for hot deployable classes
	 */
	public synchronized void reload(){
		updatedMap.clear();
		URL[] urls = new URL[pluginDir.length + pluginJar.length];
		int i = 0;
		//
		for (i=0; i<pluginDir.length; i++){
			File topfolder = new File(pluginDir[i]);
			URL url = null;
			try {
				url = topfolder.toURI().toURL();
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
			urls[i]=url;
		}
		//
		for (i=0; i<pluginJar.length; i++){
			File topfolder = new File(pluginJar[i]);
			URL url = null;
			try {
				url = topfolder.toURI().toURL();
			} catch (MalformedURLException e) {
				logger.error("", e);
			}
			urls[pluginDir.length+i]=url;
		}
		
		//TODO, parent last classloader is needed later, if plugin code uses lib
		ClassLoader cl = new URLClassLoader(urls);
		logger.info("classloader is:" + cl);
		
		//
		for (i=0; i<pluginDir.length; i++){
			Iterator<File> files = FileUtils.iterateFiles(new File(pluginDir[i]), fileSuffix, true);
			while (files.hasNext()){
				File f=files.next();
				String name = f.getPath();
				name = name.substring(pluginDir[i].length()+1, name.length()- CLASS_SUFFIX.length() -1);//1 for .
				name = name.replace(File.separatorChar, '.');
				logger.debug("class name:" + name);
				try {
					Class<?> item = cl.loadClass(name);
					logger.info(item + " is loaded from:" + item.getClass().getClassLoader());
					
					TimestampedItem<Class<?>> ti = new TimestampedItem<Class<?>>(f.lastModified(), item);
					if (addItem(ti)){
						updatedMap.put(name, ti);
					}
					
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		}
		
		//
		for (i=0; i<pluginJar.length; i++){
			JarInputStream jarFile = null;
			try {
				jarFile = new JarInputStream(new FileInputStream(pluginJar[i]));
				JarEntry jarEntry = null;
				do {
					try{
		    			jarEntry = jarFile.getNextJarEntry();
		    		}catch(IOException ioe){
		    			logger.error("Unable to get next jar entry from jar file '"+ pluginJar[i] +"'", ioe);
		    		}
		    		if (jarEntry != null){
		    			logger.debug("jarEntry:" + jarEntry.getName());
		    			String name = jarEntry.getName();
			    		if (name.endsWith(CLASS_SUFFIX)){
							name = name.substring(0, name.length()- CLASS_SUFFIX.length() -1);//1 for .
							name = name.replace('/', '.');
							logger.debug("class name:" + name);
							try {
								Class<?> item = cl.loadClass(name);
								logger.info(item + " is loaded from:" + item.getClass().getClassLoader());
								
								TimestampedItem<Class<?>> ti = new TimestampedItem<Class<?>>(jarEntry.getTime(), item);
								logger.debug("updated time:" + TimeUtil.sddf.format(new Date(jarEntry.getTime())) + " for:" + jarEntry.getName());
								if (addItem(ti)){
									updatedMap.put(name, ti);
								}
								
							} catch (Exception e) {
								logger.error("", e);
							}
			    		}
		    		}
				}while (jarEntry!=null);
			}catch(FileNotFoundException e){
				logger.error("FileNotFoundException:" + pluginJar[i],e);
			} catch (IOException e1) {
				logger.error("IOException:" + pluginJar[i], e1);
			}finally{
				closeJarFile(jarFile);
			}
		}
	}
	
	private void closeJarFile(final JarInputStream jarFile)	{
	    if(jarFile != null){ 
	    	try {
	    		jarFile.close(); 
	    	}catch(IOException ioe){
	    		logger.error("", ioe);
	    	}
	    }
	}
	
	/**
	 * 
	 * @param timedItem
	 * @return true when this updated item is added
	 */
	private synchronized boolean addItem(TimestampedItem<Class<?>> timedItem){
		String itemName = timedItem.getContent().getName();
		if (itemMap.containsKey(itemName)){
			TimestampedItem<Class<?>> olditem = itemMap.get(itemName);
			if (timedItem.getTimestamp() > (olditem.getTimestamp())){
				itemMap.put(itemName, timedItem);
				return true;
			}else{
				return false; //add failed because existing a newer version
			}
		}else{
			itemMap.put(itemName, timedItem);
			return true;
		}
	}
	
	
	
	public synchronized void cleanup(){
		itemMap.clear();
	}
	
	public String toString(){
		return "updatedMap:" + updatedMap + "\n" +
				"itemMap:" + itemMap + "\n";
	}

}
