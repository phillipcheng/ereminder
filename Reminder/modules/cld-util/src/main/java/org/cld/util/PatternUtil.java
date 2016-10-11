package org.cld.util;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PatternUtil {

	public static Logger logger = LogManager.getLogger(PatternUtil.class);
	
	public static void usePattern(PatternIO inPattern, List<String> pageList){
		String currentImageUrl = null;
		String lastImageUrl = null;
		if (pageList.size()>0){
			currentImageUrl = pageList.get(pageList.size()-1);
			if (pageList.size()>1){
				lastImageUrl = pageList.get(pageList.size()-2);
			}
		}else{
			return;
		}
		
		if (!inPattern.findPattern){
			//try 1 url pattern, the count0 means the index+1 for the first match url
			inPattern.pr = PatternResult.findPattern(currentImageUrl);
			if (inPattern.pr!=null){
				inPattern.findPattern=true;
				inPattern.count0=pageList.size();//since the 1st match url is the lastImageUrl
				inPattern.verified=false;//need verification
			}else{
				//try 2 url pattern
				if (lastImageUrl!=null &&
						!StringUtil.urlEqual(lastImageUrl, currentImageUrl)){
					inPattern.pr = PatternResult.findPattern(lastImageUrl, currentImageUrl);
					if (inPattern.pr!=null){
						inPattern.findPattern=true;
						inPattern.count0 = pageList.size()-1; //since the 1st match url is the lastImageUrl for 2 url pattern, so i need to minus 1
						inPattern.verified=false;//need verification
					}else{
						inPattern.findPattern=false;//this will trigger re-find	
						inPattern.verified=false;//
					}
				}
			}
		}else if (!inPattern.verified){
			String guessImageUrl = PatternResult.guessUrl(inPattern.pr, pageList.size()-inPattern.count0);
			if (!Objects.equals(guessImageUrl, currentImageUrl)){
				inPattern.verified=true;
				logger.debug("verified successfully." + inPattern.pr);
				//update pr and pagelist and break
				//remove the previously added page, since it can be guessed as well
				if (inPattern.pr.getPatternType()==PatternResult.pt_two_url){
					//need to remove 3, since we guess use 2, verify use 1
					if (pageList.size()>=3){
						pageList.subList(pageList.size()-3, pageList.size()).clear();
					}else{
						logger.error(String.format("pattern 2 url No.6 uses 3 pages, "
								+ "pageList size should be at least 3, but we get: %s",  pageList));
					}
				}else{
					//remove 2, since we guess 1, verify 1
					if (pageList.size()>=2){
						pageList.subList(pageList.size()-2, pageList.size()).clear();
					}else{
						logger.error(String.format("other 1 url pattern uses 2 pages, "
								+ "pageList size should be at least 2, but we get: %s",  pageList));
					}
				}
			}else{
				if (inPattern.pr.getPatternType()!=PatternResult.pt_two_url){
					logger.info(String.format("single url guess verification failed: \nimageUrl guessed is:%s\n, "
							+ "realUrl is %s", guessImageUrl, currentImageUrl));
					logger.info(String.format("try 2 url guess, url1:%s\n, url2:%s", lastImageUrl, currentImageUrl));
					if (!StringUtil.urlEqual(lastImageUrl, currentImageUrl)){
						inPattern.pr = PatternResult.findPattern(lastImageUrl, currentImageUrl);
						if (inPattern.pr!=null){
							inPattern.findPattern=true;
							inPattern.count0 = pageList.size()-1; //since the 1st match url is the lastImageUrl for 2 url pattern, so i need to minus 1
							inPattern.verified=false;//need verification
						}else{
							inPattern.findPattern=false;//this will trigger re-find	
						}
					}else{
						inPattern.findPattern=false;
					}
				}else{
					logger.info(String.format("2 url guess verification failed: \nimageUrl guessed is:%s\n, "
							+ "realUrl is %s", guessImageUrl, currentImageUrl));
					//retry 2 url matching with different url1 and url2
					inPattern.pr = PatternResult.findPattern(lastImageUrl, currentImageUrl);
					if (inPattern.pr!=null){
						inPattern.findPattern=true;
						inPattern.count0 = pageList.size()-1; //since the 1st match url is the lastImageUrl for 2 url pattern, so i need to minus 1
						inPattern.verified=false;//need verification
					}else{
						inPattern.findPattern=false;//this will trigger re-find	
						inPattern.verified=false;//
					}
				}
			}
		}
	}
}
