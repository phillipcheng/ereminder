package org.cld.datacrawl.mgr.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.datastore.entity.SiteConf;

public class ImportExportSiteConf {
	private static Logger logger =  LogManager.getLogger(ImportExportSiteConf.class);
	CrawlClientNode ccnode;
	CrawlConf cconf;
	
	private ImportExportSiteConf(String clientProperties) {
		ccnode = CrawlTestUtil.getCCNode(clientProperties);
		cconf = ccnode.getCConf();
	}
	
	public ImportExportSiteConf(){
		this("client1-v2.properties");
	}
	
	
	public void exportSiteConf(String userId, String path) throws Exception{
		List<SiteConf> sclist = cconf.getDsm().getSiteConf(userId, true, -1);
		logger.info("siteconf found:" + sclist.size());
		for (SiteConf sc:sclist){
			String fileName = path + sc.getId() + ".xml";
			File f = new File(fileName);
			Writer w = new BufferedWriter(new FileWriter(f));
			SiteConf fullSC = cconf.getDsm().getFullSitConf(sc.getId());
			if (fullSC!=null){
				w.write(fullSC.getConfxml());
			}
			w.close();
		}
	}
	
	public void importSiteConf(String userId, String path) throws Exception{
		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".xml"))
					return true;
				else
					return false;
			}
			
		});
		for (File f: files){
			String name = f.getName();
			name = name.substring(0, name.length()-".xml".length());
			BufferedReader r = new BufferedReader(new FileReader(f));
			StringBuilder builder = new StringBuilder();
			String aux = "";

			while ((aux = r.readLine()) != null) {
			    builder.append(aux);
			}

			String text = builder.toString();
			cconf.getDsm().saveXmlConf(name, userId, text);
		}
	}

	public static void main(String[] args) throws Exception{
		if (args.length!=4){
			System.out.println("usage: ImportExportSiteConf properties import/export userId path");
			return;
		}
		String properties = args[0];
		String cmd = args[1];
		String userId = args[2];
		String path=args[3];
		ImportExportSiteConf iesf = new ImportExportSiteConf(properties);
		if ("import".equals(cmd)){
			iesf.importSiteConf(userId, path);
		}else if ("export".equals(cmd)){
			iesf.exportSiteConf(userId, path);
		}else{
			System.out.println("unknown command:" + cmd);
		}
	}
}
