package org.cld.util;

import org.apache.commons.lang.ObjectUtils;

public abstract class FileDataMapper implements DataMapper{
	public abstract String getFileName(String stockId);
	public abstract Object getObject(String line);//csv line
	
	@Override
	public boolean equals(Object obj){
		if (obj!=null && obj instanceof FileDataMapper){
			FileDataMapper fdmapper=(FileDataMapper)obj;
			return ObjectUtils.equals(this.getFileName(""), fdmapper.getFileName(""));
		}else{
			return false;
		}
	}
	
	public boolean isCqMapper(){
		return true;
	}
	
	@Override
	public int hashCode(){
		return getFileName("").hashCode();
	}
}
