package cy.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import cy.common.entity.Reading;
import cy.common.persist.RemotePersistManager;

public class WSUtil {
	public static final String EMPTY_PARAMETER="EmptyParameter";
	
	public static String convertToEmptyParam(String param){
		if ("".equals(param)||param==null){
			return EMPTY_PARAMETER;
		}else{
			return param;
		}
	}
	
	public static String convertFromEmptyParam(String param){
		if (EMPTY_PARAMETER.equals(param)){
			return "";
		}else{
			return param;
		}
	}
	
	public static void fixUpReadingDate(Reading r){
		String strUtime = r.getStrUtime();
		Date d;
		if (strUtime!=null){
			try {
				d = RemotePersistManager.SDF_SERVER_DTZ.parse(strUtime);
			}catch(Exception e){
				d = new Date();
			}
		}else{
			d = new Date();
		}
		r.setUtime(d);
	}
	
	public static String getStringFromInputStream(InputStream in, String encoding) throws Exception{	
		BufferedReader reader = null;
		if (encoding==null){
			reader = new BufferedReader(new InputStreamReader(in));
		}else{
			reader = new BufferedReader(new InputStreamReader(in, encoding));
		}
        String result, line = reader.readLine();
        result = line;
        while((line = reader.readLine()) != null) {
            result += line;
        }
        return result;
	}
	
	public static String getStringFromInputStream(InputStream in) throws Exception{		
		return getStringFromInputStream(in, null);
	}
}
