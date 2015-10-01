package org.cld.crbook.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;

import cy.common.entity.Book;
import cy.common.entity.Page;
import cy.common.entity.Volume;
import cy.common.xml.XmlWorker;
import static cy.common.xml.XmlCSVHeader.*;

public class XmlCSV {

	private static Logger logger =  LogManager.getLogger(XmlCSV.class);
	
	public static final String UTF8Encoding="UTF-8";

	public static final int OR_NOTHING = 1;
	public static final int OR_BOOK = 2;
	public static final int OR_BOOK_PAGE = 3;
	
	public static void genAllCSV(String path, String outputFile, String dupCheckUrl){
		BufferedWriter fw = null;
		try{
			Collection<File> cf = FileUtils.listFiles(new File(path), new String[]{vol_suffix}, true);
			fw = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(outputFile), UTF8Encoding));
			for (File f : cf){
				Volume v = new Volume();
				XmlWorker.readVolumeXml(v, f);
				//find the books belong to this volume
				String fpath = f.getPath();
				//remove the vol_suffix to get the file name for the folder(volume)
				String vfolder = fpath.substring(0, fpath.lastIndexOf(vol_suffix));
				genBookPageCSV(vfolder, book_suffix, fw, false, v, dupCheckUrl);				
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void genVolCSV(String path, String fileSuffix, String outputFile){
		BufferedWriter fw = null;
		try{
			Collection<File> cf = FileUtils.listFiles(new File(path), new String[]{fileSuffix}, true);
			fw = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(outputFile), UTF8Encoding));
			for (File f : cf){
				Volume v = new Volume();
				XmlWorker.readVolumeXml(v, f);
				
				String str = v.toDBString(TYPE_SEPERATOR);
				fw.write(TYPE_VOL + TYPE_SEPERATOR + str);
				fw.newLine();
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void genBookPageCSV(String path, String fileSuffix, String outputFile, String dupCheckUrl){
		BufferedWriter fw = null;
		try{
			
			fw = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(outputFile), UTF8Encoding));
			genBookPageCSV(path, fileSuffix, fw, true, null, dupCheckUrl);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 * @param path: input file directory
	 * @param fileSuffix: input file suffix
	 * @param fw: output writer
	 * @param isRecursive: whether recursive down
	 * @param v: the volume to write out as well
	 * @param dupCheck: pointing to the database to dedup against
	 */
	private static void genBookPageCSV(String path, String fileSuffix, BufferedWriter fw, 
			boolean isRecursive, Volume v, String dupCheckUrl){
		
		try{
			
			Collection<File> cf = null;
			if (isRecursive){
				cf = FileUtils.listFiles(new File(path), new String[]{fileSuffix}, true);
			}else{
				FilenameFilter bookFilter = new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith(book_suffix)){
							return true;
						}
						return false;
					}					
				};
				File vf = new File(path);
				if (vf.isDirectory()){
					cf = new ArrayList<File>();
					String[] fs = vf.list(bookFilter);
					for (String f:fs){
						File file = new File(path, f);
						cf.add(file);
					}
				}else{
					logger.error(path + " should be a directory.");
				}
			}
			
			int bookCount=cf.size();
			for (File f : cf){
				Book b = new Book();
				List<Page> pl = new ArrayList<Page>();
				XmlWorker.readBookXml(b, pl, f);
					
				//find common prefix
				String[] backUrls = new String[pl.size()];
				for (int i=0; i<pl.size(); i++){
					Page p = pl.get(i);
					backUrls[i] = p.getBackgroundUri();
				}
				String bUrl = StringUtils.getCommonPrefix(backUrls);
				
				//find common suffix
				String[] reverseUrls = new String[pl.size()];
				for (int i=0; i<backUrls.length; i++){
					String url = backUrls[i];
					if (bUrl!=null){
						//remove the common prefix
						url = url.substring(bUrl.length());
					}
					reverseUrls[i] = new StringBuffer(url).reverse().toString();
				}
				String cStr = StringUtils.getCommonPrefix(reverseUrls);
				String sUrl = new StringBuffer(cStr).reverse().toString();
				
				if (b.getPageBgUrlPattern()==null){
					//if no existing pattern, create a page list pattern
					PatternResult pr = new PatternResult();
					pr.setPatternType(PatternResult.pt_list);
					pr.setPatternPrefix(bUrl);
					pr.setPatternSuffix(sUrl);
					b.setPageBgUrlPattern(pr);
					String[] ppUrls = new String[pl.size()];
					int i=0;
					for (Page p: pl){
						String url = p.getBackgroundUri();
						url = url.substring(bUrl.length());
						if (url.length()>sUrl.length()){
							url = url.substring(0, url.length()-sUrl.length());
						}
						ppUrls[i]=url;
						i++;
					}
					pr.setPPUrls(ppUrls);
					b.dataToJSON();						
					String str = b.toDBString(TYPE_SEPERATOR);
					fw.write(TYPE_BOOK + TYPE_SEPERATOR + str);
					fw.newLine();
				}else{
					//find common prefix
					b.setbUrl(bUrl);
					b.setsUrl(sUrl);
					
					b.dataToJSON();						
					String str = b.toDBString(TYPE_SEPERATOR);
					fw.write(TYPE_BOOK + TYPE_SEPERATOR + str);
					fw.newLine();
					
					for (Page p: pl){
						String url = p.getBackgroundUri();
						url = url.substring(bUrl.length());
						if (url.length()>sUrl.length()){
							url = url.substring(0, url.length()-sUrl.length());
						}
						p.setBackgroundUri(url);
						p.dataToJSON();
						str = p.toDBString(TYPE_SEPERATOR);
						fw.write(TYPE_PAGE + TYPE_SEPERATOR + str);
						fw.newLine();
					}
				}
			}
			if (v!=null){
				v.setBookNum(bookCount);
				String str = v.toDBString(TYPE_SEPERATOR);
				fw.write(TYPE_VOL + TYPE_SEPERATOR + str);
				fw.newLine();
			}
		}catch(Exception e){
			logger.error("", e);
		}
	}	
	
	/**
	 * 
	 */
	class BookAndPages{
		Book b;
		List<Page> pl;
	}
	
	/**
	 * 
	 * @param b
	 * @param pl: the pl is the newly found pagelist should be appended to book's origin pagelist
	 * @return nothing returned, but the input book and pagelist might be changed, check the params to decide
	 */
	public static void optimizeBook(Book b, List<Page> pl){	
		//find common prefix
		String[] backUrls = new String[pl.size()];
		for (int i=0; i<pl.size(); i++){
			Page p = pl.get(i);
			backUrls[i] = p.getBackgroundUri();
		}
		
		if(b.getPageBgUrlPattern()!=null && b.getPageBgUrlPattern().getPatternType() == PatternResult.pt_list){
			//already has a list of pages with no pattern
			//restore the existing pagelist to the beginning of the pagelist
			if (b.getPageBgUrlPattern().getPPUrls()!=null){
				int existBgUrlLength = b.getPageBgUrlPattern().getPPUrls().length;
				String[] existBackUrls = new String[existBgUrlLength];
				for (int i=0; i<existBgUrlLength; i++){
					existBackUrls[i] = b.getPageBgUrlPattern().getPatternPrefix() +
							b.getPageBgUrlPattern().getPPUrls()[i] +
							b.getPageBgUrlPattern().getPatternSuffix();
				}
				backUrls = (String[]) ArrayUtils.addAll(existBackUrls, backUrls);
			}
		}
		
		String bUrl = StringUtils.getCommonPrefix(backUrls);
		
		//find common suffix
		String[] reverseUrls = new String[backUrls.length];
		for (int i=0; i<backUrls.length; i++){
			String url = backUrls[i];
			if (bUrl!=null){
				//remove the common prefix
				url = url.substring(bUrl.length());
			}
			reverseUrls[i] = new StringBuffer(url).reverse().toString();
		}
		String cStr = StringUtils.getCommonPrefix(reverseUrls);
		String sUrl = new StringBuffer(cStr).reverse().toString();
		
		if (b.getPageBgUrlPattern()==null || //1st time, create a page list pattern
				b.getPageBgUrlPattern().getPatternType()==PatternResult.pt_list){
			PatternResult pr = new PatternResult();
			pr.setPatternType(PatternResult.pt_list);
			pr.setPatternPrefix(bUrl);
			pr.setPatternSuffix(sUrl);
			b.setPageBgUrlPattern(pr);
			String[] ppUrls = new String[backUrls.length];
			int i=0;
			for (String url: backUrls){
				url = url.substring(bUrl.length());
				if (url.length()>sUrl.length()){
					url = url.substring(0, url.length()-sUrl.length());
				}
				ppUrls[i]=url;
				i++;
			}
			pr.setPPUrls(ppUrls);
			b.dataToJSON();
			pl.clear();//clear the pagelist
		}else{//multiple patterns matched, still need partial page list
			//find common prefix
			b.setbUrl(bUrl);
			b.setsUrl(sUrl);			
			b.dataToJSON();
			for (Page p: pl){
				String url = p.getBackgroundUri();
				url = url.substring(bUrl.length());
				if (url.length()>sUrl.length()){
					url = url.substring(0, url.length()-sUrl.length());
				}
				p.setBackgroundUri(url);
				p.dataToJSON();
			}
		}
	}	
}
