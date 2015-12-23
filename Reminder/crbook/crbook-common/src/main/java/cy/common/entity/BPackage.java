package cy.common.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.cld.util.CompareUtil;

public class BPackage {
	public static final String sdformat ="yyyy-MM-dd HH:mm:ss.SSS";
	public static final SimpleDateFormat sdf = new SimpleDateFormat(sdformat, Locale.US);
	
	
	public static final String default_time = "2011-01-22 02:22:22.000 -0800";
	
	private String name;
	private long size;
	private String ptime; //package update time
	private String itime; //install time
	
	public BPackage(String name, long size, String ptime, String itime){
		this.name = name;
		this.size = size;
		this.ptime = ptime;
		this.itime = itime;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getPtime() {
		return ptime;
	}
	public void setPtime(String ptime) {
		this.ptime = ptime;
	}
	public String getItime() {
		return itime;
	}
	public void setItime(String itime) {
		this.itime = itime;
	}
	public Date getPtimeDate(){
		try {
			return sdf.parse(this.ptime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(",");
		sb.append(size);
		sb.append(",");
		sb.append(ptime);
		sb.append(",");
		sb.append(itime);		
		return sb.toString();
	}
	
	@Override
	public int hashCode(){
		return this.getName().hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof BPackage))
			return false;
		BPackage p = (BPackage)o;
		return (!CompareUtil.ObjectDiffers(this.getName(), p.getName())
				&& (this.getSize() == p.getSize()) 
				&& !CompareUtil.ObjectDiffers(this.getPtime(), p.getPtime())
			   );
	}
}
