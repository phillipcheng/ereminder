package org.cld.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtil {
	/**
	 * 
	 * @param totalList
	 * @param batchSize
	 * @return return list RandomAccessSubList is not Serializable
	 */
	public static List<List<?>> generateBatches(List<?> totalList, int batchSize){
		List<List<?>> resList = new ArrayList<List<?>>();
		int total = totalList.size();
		int turns = total/batchSize;
		
		for (int i=0; i<turns; i++){
			List<?> subList = totalList.subList(i*batchSize, (i+1)*batchSize);
			resList.add(subList);
		}
		List<?> subList = totalList.subList(turns*batchSize, total);
		resList.add(subList);
		return resList;
	}
	
	public static Long[] convertToLong(String[] in){
		Long[] ret = new Long[in.length];
		for (int i=0; i<in.length; i++){
			ret[i] = Long.parseLong(in[i]);
		}
		return ret;
	}
	
	public static <T> T[] concatAll(T[] first, T[]... rest) {
	  int totalLength = first.length;
	  for (T[] array : rest) {
	    totalLength += array.length;
	  }
	  T[] result = Arrays.copyOf(first, totalLength);
	  int offset = first.length;
	  for (T[] array : rest) {
	    System.arraycopy(array, 0, result, offset, array.length);
	    offset += array.length;
	  }
	  return result;
	}
}
