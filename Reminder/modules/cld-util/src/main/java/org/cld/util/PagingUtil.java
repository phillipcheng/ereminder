package org.cld.util;

public class PagingUtil {
	public static long getPageNum(long itemNum, int pageSize){
		if (itemNum % pageSize == 0){
			return itemNum/pageSize;
		}else{
			return itemNum/pageSize+1;
		}
	}
}
