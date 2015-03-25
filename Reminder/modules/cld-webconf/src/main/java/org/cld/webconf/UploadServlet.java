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

@WebServlet("/Upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	Logger logger = LogManager.getLogger("cld.jsp");
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uid="cr";
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    InputStream filecontent = filePart.getInputStream();
	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(filecontent));
	    ZipEntry entry;
        while((entry = zis.getNextEntry()) != null) {
           logger.info("Extracting: " +entry);
           int count;
           int BUFFER = 2048;
           byte data[] = new byte[BUFFER];
           // write the files to the disk
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           while ((count = zis.read(data, 0, BUFFER)) != -1) {
              baos.write(data, 0, count);
           }
           baos.flush();
           String xmlconf = baos.toString("UTF-8");
           String filename = entry.getName();
           String confid = filename.substring(0, filename.lastIndexOf("."));
           ConfServlet.getCConf().getDsm().saveXmlConf(confid, uid, xmlconf);
           baos.close();
        }
        zis.close();
	}

	private static String getFilename(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}

}
