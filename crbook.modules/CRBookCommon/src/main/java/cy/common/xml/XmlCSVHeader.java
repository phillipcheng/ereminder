package cy.common.xml;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XmlCSVHeader {

	private static Logger logger =  LogManager.getLogger(XmlCSVHeader.class);
	
	public static final String TYPE_BOOK="B";
	public static final String TYPE_PAGE="P";
	public static final String TYPE_VOL="V";
	public static final String TYPE_SEPERATOR="|";
	public static final String TYPE_REG_SEPERATOR="\\|";//for regular expression this is a keyword
	public static final String book_suffix="crbook";
	public static final String vol_suffix="crvol";
}
