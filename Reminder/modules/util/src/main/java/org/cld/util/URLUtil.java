package org.cld.util;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class URLUtil {

	private static Logger logger = LogManager.getLogger(URLUtil.class);
	/**
	 * extract the parameter values according to the keys from the url
	 * @param url
	 * @param keys
	 * @return: the name value pair list of the giving key array
	 */	
	public static List<NameValuePair> extractValue(String url, String[] keys){
		try {
			URIBuilder urib = new URIBuilder(url);			
			List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
			List<NameValuePair> nvp = urib.getQueryParams();
			for (int i=0; i<nvp.size(); i++){
				NameValuePair nv = nvp.get(i);
				if (ArrayUtils.contains(keys, nv.getName())){
					nvpList.add(nv);
				}
			}
			return nvpList;
		} catch (URISyntaxException e) {
			logger.error("", e);
			return null;
		}
		
	}
	
	/**
	 * only keep the parameter values according to the keys from the url / change the url
	 * @param url
	 * @param keys
	 * @param baseURL: the base URL be put at the front
	 * @return: the updated url
	 */
	public static String retainValue(String url, String[] keys, String baseURL){
		List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
		List<NameValuePair> nvp = URLEncodedUtils.parse(url, Charset.defaultCharset());
		for (int i=0; i<nvp.size(); i++){
			NameValuePair nv = nvp.get(i);
			if (ArrayUtils.contains(keys, nv.getName())){
				nvpList.add(nv);
			}
		}
		String params = URLEncodedUtils.format(nvpList, Charset.defaultCharset());
		return baseURL + params;
		
	}
	
}
