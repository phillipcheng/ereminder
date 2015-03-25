package cy.common.util;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {
	public static Logger logger = LogManager.getLogger(FileUtil.class);
	
	public static String getFileAsString(String path, String encoding){
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(path));
			return new String(encoded, encoding);
		} catch (Exception e) {
			logger.error("",e);
			return null;
		}
	}
}
