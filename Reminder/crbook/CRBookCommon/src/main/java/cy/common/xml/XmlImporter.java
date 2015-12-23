package cy.common.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cy.common.persist.BatchPersistManager;
import cy.common.persist.LocalPersistManager;
import cy.common.persist.RemotePersistManager;
import static cy.common.xml.XmlCSVHeader.*;


public class XmlImporter {
	public static Logger logger = LogManager.getLogger(XmlImporter.class);
	
	public static final String UTF8Encoding="UTF-8";
	public static final int NUM_PAGE_ATTR=3;
	public static final int NUM_BOOK_ATTR=7;
	public static final int NUM_VOL_ATTR=7;

	public static int BATCH_SIZE=1000;
	
	private static final String TAG = "XmlImporter";
	
	
	public static void importEntitiesFromCSV(File f, BatchPersistManager pManager){
		InputStream is = null;
		try {
			is = new FileInputStream(f);
			importEntitiesFromCSV(is, pManager);
		} catch (FileNotFoundException e) {
			logger.error("", e);
		}		
	}
	
	private static void importEntitiesToDB(List<String[]> booklist, List<String[]> pagelist,
			List<String[]> vollist, BatchPersistManager pManager){
		String[][] bookarray = new String[booklist.size()][NUM_BOOK_ATTR];
        String[][] pagearray = new String[pagelist.size()][NUM_PAGE_ATTR];//no utime
        String[][] volarray = new String[vollist.size()][NUM_VOL_ATTR];
        booklist.toArray(bookarray);
        pagelist.toArray(pagearray);
        vollist.toArray(volarray);
        
        pManager.batchInsertBooks(bookarray);
        pManager.batchInsertPages(pagearray);
        pManager.batchInsertVols(volarray);

		logger.warn(booklist.size() + " books, " + pagelist.size() + " pages, " + 
				vollist.size() + " vols, " + "imported.");
	}
	
	public static void importEntitiesFromCSV(InputStream is, BatchPersistManager pManager){
		BufferedReader fr = null;
		String line = null;
		try{
			fr = new BufferedReader(new InputStreamReader(is, UTF8Encoding));
			List<String[]> booklist = new ArrayList<String[]>();
			List<String[]> pagelist = new ArrayList<String[]>();
			List<String[]> vollist = new ArrayList<String[]>();
			//must be starting with a book line
			line = fr.readLine();
			int totalln=0;
            int linenum=0;
            while (line!=null) {
            	String[] tokens = line.split(TYPE_REG_SEPERATOR);
            	int i=0;
        		String[] val =null;
        		String type = null;
        		boolean skip=false;
            	for(String t: tokens) {
            		if (i==0){
            			type = t;
            			if (TYPE_BOOK.equals(type)){
            				val = new String[NUM_BOOK_ATTR];//
            			}else if (TYPE_PAGE.equals(type)){
            				val = new String[NUM_PAGE_ATTR];//
            			}else if (TYPE_VOL.equals(type)){
            				val = new String[NUM_VOL_ATTR];
            			}else{
            				logger.error("unsupported type:" + type);
            			}
            		}else{
            			if (i>val.length){
            				skip=true;
            				logger.error(line + " has my sepcial charactor. illegal.");
            				break;
            			}else{
            				val[i-1]=t;
            			}
            			
            		}
            		i++;
                }
            	if (!skip){
	        		if (TYPE_BOOK.equals(type)){
	    				booklist.add(val);
	    			}else if (TYPE_PAGE.equals(type)){
	    				pagelist.add(val);
	    			}else if (TYPE_VOL.equals(type)){
	    				vollist.add(val);
	    			}
            	}else{
            		skip=false;
            	}
        		totalln++;
        		linenum++;
                line = fr.readLine();
                if (linenum>BATCH_SIZE){
	        		importEntitiesToDB(booklist, pagelist, vollist, pManager);	        		
	        		linenum=0;
	        		booklist.clear();
	        		pagelist.clear();
                }
            }
            //for the last leg
    		importEntitiesToDB(booklist, pagelist, vollist, pManager);
		}catch(Exception e){
			logger.error(line, e);
		}finally{
			if (fr!=null){
				try {
					fr.close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
	}
}
