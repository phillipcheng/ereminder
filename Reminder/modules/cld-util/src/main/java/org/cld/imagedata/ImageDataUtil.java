package org.cld.imagedata;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.imagedata.Data;
import org.xml.imagedata.Entry;

public class ImageDataUtil {
	private static Logger logger =  LogManager.getLogger(ImageDataUtil.class);
	
	public static List<String> getImageFilesFromIdxFile(String strUrl){
		List<String> imageUrls = new ArrayList<String>();
		try {
			JAXBContext jc = JAXBContext.newInstance("org.xml.imagedata");
			Unmarshaller u = jc.createUnmarshaller();
			URL url = new URL(strUrl);
			Data idata = (Data) u.unmarshal(url);
			List<Entry> el = idata.getEntry();
			for (Entry e:el){
				imageUrls.add(e.getImgURL());
			}
		}catch(Exception e){
			logger.error("", e);
		}
		return imageUrls;
	}
}
