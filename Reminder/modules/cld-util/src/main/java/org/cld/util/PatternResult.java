package org.cld.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PatternResult {

	public static Logger logger = LogManager.getLogger(PatternResult.class);
	
	//zzz is the image suffix like jpg, png, etc
	//x is the number index which will be incremented to guess
	//pxp = (x)
	//s is the separator like '.'
	//yyy is the padding
	
	//001002.jpg or 0102.jpg, and the next should be 1 more then previous one, i donot know how to use regexp to express this, test in the code
	public static String pattern_0x0y_zzz = "^(.*)(0[0-9]{1})(0[0-9]{1})\\.([a-zA-Z]{3,4})$|^(.*)(0[0-9]{2})(0[0-9]{2})\\.([a-zA-Z]{3,4})$";
	public static Pattern p_0x0y_zzz = Pattern.compile(pattern_0x0y_zzz);	
	
	//01.jpg, 001.jpg, 0001.jpg, 00001.jpg let this have higher priority then the previous one when both matched 0001
	public static String pattern_0x_s_yyy_zzz = "^(.*)(0[0-9]{1,4})([_\\.%]{0,1})([%0-9a-zA-Z]*)\\.([a-zA-Z]{3,4})$";
	public static Pattern p_0x_s_yyy_zzz = Pattern.compile(pattern_0x_s_yyy_zzz);
	
	//(001.jpg) or (1.jpg) before next
	public static String pattern_pxp_s_yyy_zzz="^(.*)%28([0]*[0-9]+)%29([_\\.%]{0,1})([%0-9a-zA-Z]*)\\.([a-zA-Z]{3,4})$";
	public static Pattern p_pxp_s_yyy_zzz = Pattern.compile(pattern_pxp_s_yyy_zzz);
	
	//1.jpg
	public static String pattern_x_s_yyy_zzz = "^(.*)([0-9]{1,4})([_\\.%]{0,1})([%0-9a-zA-Z]*)\\.([a-zA-Z]{3,4})$";
	public static Pattern p_x_s_yyy_zzz = Pattern.compile(pattern_x_s_yyy_zzz);
	
	//71054039/02_152.jpg
	
	public static int pt_x_s_yyy_zzz=1;
	public static int pt_0x_s_yyy_zzz=2;
	public static int pt_pxp_s_yyy_zzz=3;
	public static int pt_0x0y_zzz=4;
	public static int pt_list=5;//page url in the list
	public static int pt_two_url=6;//pattern is guess via 2 url
	
	// [patterPrefix][delta*step+startImageCount][sep][postFix].[patternSuffix]
	private static final int default_patternType=pt_x_s_yyy_zzz;
	private static final int default_digitNum=0;
	private static final int default_startImageCount=1;
	private static final String default_patternSuffix="jpg";
	private static final String default_patternPrefix="";
	private static final int default_step=1;
	
	private static final String default_postFix="";
	private static final String default_sep="";
	
	private int patternType=default_patternType;
	
	private int digitNum=default_digitNum;
	
	private int startImageCount=default_startImageCount;
	
	private String patternSuffix=default_patternSuffix;
	
	private String patternPrefix=default_patternPrefix;
	
	private int step=default_step; //increase by this, 1, -1, 2 etc
	
	private String postFix=default_postFix;
	
	private String sep=default_sep;
	

	private String[] ppUrls;
	
	public String[] getPPUrls(){
		return ppUrls;
	}
	public void setPPUrls(String[] ppUrls){
		this.ppUrls = ppUrls;
	}
	
	private static final String JSKEY_PATTERN_TYPE="pt";
	private static final String JSKEY_DIGIT_NUM="dn";
	private static final String JSKEY_START_IMAGE="sm";
	private static final String JSKEY_SUFFIX="suf";
	private static final String JSKEY_PREFIX="prf";
	private static final String JSKEY_POSTFIX="pf";
	private static final String JSKEY_SEP="sep";
	private static final String JSKEY_INCMODE="im";//step
	private static final String JSKEY_PAGELIST="pl";
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(patternType);
		sb.append(",");
		sb.append(digitNum);
		sb.append(",");
		sb.append(startImageCount);
		sb.append(",");
		sb.append(patternSuffix);
		sb.append(",");
		sb.append(patternPrefix);		
		sb.append(",");
		sb.append(postFix);
		sb.append(",");
		sb.append(sep);
		sb.append(",");
		sb.append(step);
		return sb.toString();
	}
	//from string data to json-object
    public void fromJSON(String input){
		try{
			JSONObject data = new JSONObject(input);
			if (data.has(JSKEY_PATTERN_TYPE))
				patternType = data.getInt(JSKEY_PATTERN_TYPE);
			if (data.has(JSKEY_DIGIT_NUM))
				digitNum = data.getInt(JSKEY_DIGIT_NUM);
			if (data.has(JSKEY_START_IMAGE))
				startImageCount = data.getInt(JSKEY_START_IMAGE);
			if (data.has(JSKEY_SUFFIX))
				patternSuffix = data.getString(JSKEY_SUFFIX);
			if (data.has(JSKEY_PREFIX))
				patternPrefix = data.getString(JSKEY_PREFIX);
			if (data.has(JSKEY_POSTFIX))
				postFix = data.getString(JSKEY_POSTFIX);
			if (data.has(JSKEY_SEP))
				sep = data.getString(JSKEY_SEP);	
			if (data.has(JSKEY_INCMODE))
				step = data.getInt(JSKEY_INCMODE);
			if (data.has(JSKEY_PAGELIST)){
				JSONArray jarray = data.optJSONArray(JSKEY_PAGELIST);
				int count = jarray.length();
				ppUrls = new String[count];
				for (int i=0; i<count; i++){
					ppUrls[i]=jarray.getString(i);
				}	        
			}
		}
		catch(JSONException e){
			e.printStackTrace();
		}
	}
    
    public String toJSON(){
		JSONObject obj = new JSONObject();
		try {
			if (patternType!=default_patternType)
				obj.put(JSKEY_PATTERN_TYPE, patternType);
			if (digitNum!=default_digitNum)
				obj.put(JSKEY_DIGIT_NUM, digitNum);
			if (startImageCount!=default_startImageCount)
				obj.put(JSKEY_START_IMAGE, startImageCount);
			if (!default_patternSuffix.equals(patternSuffix))
				obj.put(JSKEY_SUFFIX, patternSuffix);
			if (!default_patternPrefix.equals(patternPrefix))
				obj.put(JSKEY_PREFIX, patternPrefix);			
			if (!default_postFix.equals(postFix))
				obj.put(JSKEY_POSTFIX, postFix);
			if (!default_sep.equals(sep))
				obj.put(JSKEY_SEP, sep);
			if (default_step!=step)
				obj.put(JSKEY_INCMODE, step);
			if (ppUrls!=null && ppUrls.length>0){
				JSONArray jarray=new JSONArray();
				for (int i=0; i<ppUrls.length; i++){
					jarray.put(ppUrls[i]);
				}
				obj.put(JSKEY_PAGELIST, jarray);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj.toString();
	}
    
	//return null means pattern not found
	public static PatternResult findPattern(String imageUrl){
		
		PatternResult pr = new PatternResult();
		Matcher m;
		boolean bF;
		
		m = p_0x0y_zzz.matcher(imageUrl);
		bF = m.find();
		if (bF){
			pr.patternType=pt_0x0y_zzz;
			if (m.group(1)!=null){//0102.jpg
				pr.patternPrefix = m.group(1);
				pr.digitNum=m.group(2).length();
				int first=Integer.parseInt(m.group(2));
				int second = Integer.parseInt(m.group(3));
				pr.startImageCount=Integer.parseInt(m.group(2));
				pr.patternSuffix=m.group(4);
				if (second == first+1 && first!=0){
					//let 0001 passed, not matched
					return pr;	
				}
			}else{//001002.jpg
				pr.patternPrefix = m.group(5);
				pr.digitNum=m.group(6).length();
				pr.startImageCount=Integer.parseInt(m.group(6));
				pr.patternSuffix=m.group(8);
				int first=Integer.parseInt(m.group(6));
				int second = Integer.parseInt(m.group(7));
				if (second == first+1){
					return pr;	
				}
			}
		}
		
		m = p_0x_s_yyy_zzz.matcher(imageUrl);
		bF = m.find();
		if (bF){
			pr.patternType=pt_0x_s_yyy_zzz;
			pr.patternPrefix = m.group(1);
			pr.digitNum=m.group(2).length();
			pr.startImageCount=Integer.parseInt(m.group(2));
			pr.sep = m.group(3);
			pr.postFix=m.group(4);
			pr.patternSuffix=m.group(5);
			return pr;
		}
		
		m = p_pxp_s_yyy_zzz.matcher(imageUrl);
		bF = m.find();
		if (bF){
			pr.patternType=pt_pxp_s_yyy_zzz;
			pr.patternPrefix = m.group(1);
			pr.digitNum=m.group(2).length();
			pr.startImageCount=Integer.parseInt(m.group(2));
			pr.sep = m.group(3);
			pr.postFix=m.group(4);
			pr.patternSuffix=m.group(5);
			return pr;
		}
	
		m = p_x_s_yyy_zzz.matcher(imageUrl);
		bF = m.find();
		if (bF){
			pr.patternType=pt_x_s_yyy_zzz;
			pr.patternPrefix = m.group(1);
			pr.digitNum=0;//means no appending digits, as is.
			pr.startImageCount=Integer.parseInt(m.group(2));
			pr.sep = m.group(3);
			pr.postFix=m.group(4);
			pr.patternSuffix=m.group(5);
			return pr;
		}
		return null;
	}
	
	public static PatternResult findPattern(String inImageUrl1, String inImageUrl2){
		String imageUrl1;
		String imageUrl2;
		try {
			//imageUrl1 = URLDecoder.decode(inImageUrl1,"UTF-8");
			//imageUrl2 = URLDecoder.decode(inImageUrl2,"UTF-8");
			imageUrl1 = inImageUrl1;
			imageUrl2 = inImageUrl2;
			String[] urls = new String[]{imageUrl1, imageUrl2};
			//imageUrl1 is the starting url
			String cPrefix = StringUtils.getCommonPrefix(urls);
			String cPostfix = StringUtil.getCommomSuffix(urls);
			
			String num1 = StringUtil.getStringBetweenLastPreLastPost(imageUrl1, cPrefix, cPostfix);
			String num2 = StringUtil.getStringBetweenLastPreLastPost(imageUrl2, cPrefix, cPostfix);
			int n1=0;
			int n2=0;
			try{
				n1 = Integer.parseInt(num1);
				n2 = Integer.parseInt(num2);
			}catch(NumberFormatException nfe){
				//not number
				return null;
			}
			int step = n2 - n1;
			if (Math.abs(step)<=2){
				//is a guess
				PatternResult pr = new PatternResult();
				pr.setPatternType(pt_two_url);
				pr.setStep(step);
				//from cPrefix, fetch all the tail digits, merge them with n1
				StringBuffer tailNum= new StringBuffer();
				int lastNoneDigit = cPrefix.length()-1;
				for (; lastNoneDigit>0; lastNoneDigit--){
					char ch = cPrefix.charAt(lastNoneDigit);
					if (ch>='0' && ch<='9'){
						tailNum.insert(0, ch);
					}else{
						break;
					}
				}
				logger.debug("tail number:" + tailNum);
				String totalNum = tailNum + num1;
				pr.setPatternPrefix(cPrefix.substring(0, lastNoneDigit+1));//
				pr.setDigitNum(totalNum.length());
				pr.setPatternSuffix(cPostfix);
				int sIC = Integer.parseInt(totalNum);
				pr.setStartImageCount(sIC);
				return pr;
			}
		} catch (Exception e) {
			logger.info("", e);
		}
		return null;
	}
	
	//delta is relative to 1st page, so if you want to get page n, you need to pass in n-1
	public static String guessUrl(PatternResult pr, int delta){
		String imageUrl=null;
		
		if (pr.patternType==pt_0x_s_yyy_zzz){
			//set pageUrl
			String pageNum = StringUtil.getStringFromNum(pr.getStep()*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_x_s_yyy_zzz){
			//set pageUrl
			int pageNum = pr.getStep()*delta + pr.startImageCount;
			imageUrl = pr.patternPrefix + pageNum + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_pxp_s_yyy_zzz){
			String pageNum = StringUtil.getStringFromNum(pr.getStep()*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + "%28" + pageNum + "%29" + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_0x0y_zzz){
			String pageNum1 = StringUtil.getStringFromNum(pr.getStep()*delta + pr.startImageCount, pr.digitNum);
			String pageNum2 = StringUtil.getStringFromNum(pr.getStep()*delta + pr.startImageCount +1, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum1 + pageNum2  + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_list){
			if (pr.ppUrls!=null){
				if (delta>=0 && delta<pr.ppUrls.length)
					imageUrl = pr.patternPrefix + pr.ppUrls[delta] + pr.patternSuffix;
			}else{
				//logger.error();
			}
		}else if (pr.patternType==pt_two_url){
			//
			String pageNum = StringUtil.getStringFromNum(pr.step*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum + pr.patternSuffix;
			
		}else{
			logger.error("unknown pattern:" + pr.patternType);
		}
		
		return imageUrl;
	}
	
	//delta is relative to 1st page, so if you want to get page n, you need to pass in n-1
	public static String tryUrl(PatternResult pr, int delta, int tryStep){
		String imageUrl=null;
		
		if (pr.patternType==pt_0x_s_yyy_zzz){
			//set pageUrl
			String pageNum = StringUtil.getStringFromNum(tryStep*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_x_s_yyy_zzz){
			//set pageUrl
			int pageNum = tryStep*delta + pr.startImageCount;
			imageUrl = pr.patternPrefix + pageNum + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_pxp_s_yyy_zzz){
			String pageNum = StringUtil.getStringFromNum(tryStep*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + "%28" + pageNum + "%29" + pr.sep + pr.postFix + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_0x0y_zzz){
			String pageNum1 = StringUtil.getStringFromNum(tryStep*delta + pr.startImageCount, pr.digitNum);
			String pageNum2 = StringUtil.getStringFromNum(tryStep*delta + pr.startImageCount +1, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum1 + pageNum2  + "." + pr.patternSuffix;
		}else if (pr.patternType==pt_two_url){
			//
			String pageNum = StringUtil.getStringFromNum(tryStep*delta + pr.startImageCount, pr.digitNum);
			imageUrl = pr.patternPrefix + pageNum + pr.patternSuffix;
			
		}
		
		return imageUrl;
	}
	
	public int getPatternType() {
		return patternType;
	}

	public void setPatternType(int patternType) {
		this.patternType = patternType;
	}

	public int getDigitNum() {
		return digitNum;
	}

	public void setDigitNum(int digitNum) {
		this.digitNum = digitNum;
	}

	public int getStartImageCount() {
		return startImageCount;
	}

	public void setStartImageCount(int startImageCount) {
		this.startImageCount = startImageCount;
	}

	public String getPatternSuffix() {
		return patternSuffix;
	}

	public void setPatternSuffix(String patternSuffix) {
		this.patternSuffix = patternSuffix;
	}

	public String getPatternPrefix() {
		return patternPrefix;
	}

	public void setPatternPrefix(String patternPrefix) {
		this.patternPrefix = patternPrefix;
	}

	public String getPostFix() {
		return postFix;
	}

	public void setPostFix(String postFix) {
		this.postFix = postFix;
	}

	public String getSep() {
		return sep;
	}
	public void setSep(String sep) {
		this.sep = sep;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
}
