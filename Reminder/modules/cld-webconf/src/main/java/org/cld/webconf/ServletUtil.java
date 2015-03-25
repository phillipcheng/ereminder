package org.cld.webconf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServletUtil {

	private static Logger logger = LogManager.getLogger("cld.jsp");
	
	public static void writeFileContent(String file, PrintWriter out, Map<String, String> rpMap){
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
			while ((line = br.readLine()) != null) {
				if (rpMap!=null){
					for (Entry<String, String> ent : rpMap.entrySet()){
						if (line.contains(ent.getKey())){
							line = line.replace(ent.getKey(), ent.getValue());
						}
				    }
				}
				out.println(line);
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			if (br!=null){
				try {
					br.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}
	
	public static String genViewTaskUrl(Set<String> taskids){
		String requestUrl = ConfServlet.TestResultPage + "?";
		int testCount=0;
		for(String taskid:taskids){
			if (testCount>0){
				requestUrl+="&";
			}
			requestUrl += ConfServlet.REQ_PARAM_TEST_TASKIDS + "=" + taskid;
		}
		return requestUrl;
	}
	
	public static String genViewCatUrl(String storeId, String pcatId){
		String requestUrl = ConfServlet.CatResultPage + "?";
		requestUrl += ConfServlet.REQ_PARAM_SITECONF_ID + "=" + storeId;
		if (pcatId!=null){
			requestUrl += "&" + ConfServlet.REQ_PARAM_PCAT_ID + "=" + pcatId;
		}
		return requestUrl;
	}
	
	public static String genViewSiteConfUrl(){
		String requestUrl = ConfServlet.SiteConfListPage;
		return requestUrl;
	}
}
