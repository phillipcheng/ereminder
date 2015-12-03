package org.cld.stock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceSeg {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public static String TAG_OPEN="open";
	public static String TAG_CLOSE="close";
	
	private String fromTag;
	private Date fromDt;
	private float fromPrice;
	private String toTag;
	private Date toDt;
	private float toPrice;
	private float value;

	public PriceSeg(String fromTag, Date fromDt, float fromPrice, String toTag, Date toDt, float toPrice){
		this.fromTag = fromTag;
		this.fromDt = fromDt;
		this.fromPrice = fromPrice;
		this.toTag = toTag;
		this.toDt = toDt;
		this.toPrice = toPrice;
		this.value = toPrice - fromPrice;
	}
	
	public String toString(){
		return String.format("seg: from %s %s %.2f to %s %s %.2f, value:%.2f", sdf.format(fromDt), fromTag, fromPrice, sdf.format(toDt), toTag, toPrice, value);
	}
}
