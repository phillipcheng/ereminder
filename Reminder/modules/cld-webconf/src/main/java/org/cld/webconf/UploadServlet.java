package org.cld.webconf;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@WebServlet("/Upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LogManager.getLogger("cld.jsp");
	
	private static final String XML_TYPE = "text/xml";
	private static final String ZIP_TYPE = "";
	
	private static final String CONTENT_DISPOSITION="content-disposition";
	private static final String CONTENT_DISPOSITION_FILENAME = "filename";
	
	private String getFilename(Part part) {
        for (String cd : part.getHeader(CONTENT_DISPOSITION).split(";")) {
            if (cd.trim().startsWith(CONTENT_DISPOSITION_FILENAME)) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
	   
	private String readInputStream(InputStream input){
		int count;
        int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
	        // write the files to the disk
	        while ((count = input.read(data, 0, BUFFER)) != -1) {
	           baos.write(data, 0, count);
	        }
	        baos.flush();
	        return baos.toString("UTF-8");
		}catch(Exception e){
			logger.error("", e);
		}finally{
			try {
				baos.close();
			}catch(Exception e1){
				logger.error("", e1);
			}
		}
		return null;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SessionFactory factory = new Configuration().configure().buildSessionFactory();
		WebConfPersistMgr wcpm = new WebConfPersistMgr(factory);
		String uid="cr";
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    logger.info(String.format("name:%s, content-type:%s, size:%d, headers:%s", 
	    		filePart.getName(), filePart.getContentType(), filePart.getSize(), filePart.getHeaderNames()));
	    String contentType = filePart.getContentType();
	    if (XML_TYPE.equals(contentType)){
	    	String filename = getFilename(filePart);
	    	InputStream xis = new BufferedInputStream(filePart.getInputStream());
	    	String confid = filename.substring(0, filename.lastIndexOf("."));
	    	String xmlconf = readInputStream(xis);
	        wcpm.saveXmlConf(confid, uid, xmlconf);
	        logger.info(String.format("file saved to: confid:%s, uid:%s, xmlconf:%s", confid, uid, xmlconf));
	        xis.close();
	    }else{
		    InputStream filecontent = filePart.getInputStream();
		    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(filecontent));
		    ZipEntry entry;
	        while((entry = zis.getNextEntry()) != null) {
	           logger.info("Extracting: " +entry);
	           String filename = entry.getName();
	           String confid = filename.substring(0, filename.lastIndexOf("."));
	           String xmlconf = readInputStream(zis);
	           wcpm.saveXmlConf(confid, uid, xmlconf);
		       logger.info(String.format("file saved to: confid:%s, uid:%s, xmlconf:%s", confid, uid, xmlconf));
	        }
	        zis.close();
	    }
	    String originUrl = (String) request.getParameter("origin");
	    response.sendRedirect(originUrl);
	}
}
