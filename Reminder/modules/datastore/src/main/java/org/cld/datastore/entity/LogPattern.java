package org.cld.datastore.entity;

import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LogPattern")
public class LogPattern {

	public static final String regexpSeperator="|||";
	
	@Column(name = "id")
	@Id 
	@GeneratedValue
	private String id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "regexp")
	private String regexp;//can be several lines, seperated by |||
	
	private transient Pattern[] patterns;
	
	@Column(name = "utime")
	private Date utime;
	
	@Column(name = "desc")
	private String desc;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegexp() {
		return regexp;
	}
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
	public Date getUtime() {
		return utime;
	}
	public void setUtime(Date utime) {
		this.utime = utime;
	}
	public Pattern[] getPatterns() {
		return patterns;
	}
	
	public void compile(){
		String[] re = regexp.split(Pattern.quote(regexpSeperator));
		patterns = new Pattern[re.length];
		for (int i=0; i<re.length; i++){
			patterns[i] = Pattern.compile(re[i], Pattern.DOTALL);
		}
	}
	
	public LogPattern(){
		
	}
	
	public LogPattern(String regexp){
		this.regexp = regexp;
		compile();
	}
	
	public LogPattern(String name, String regexp, String desc){
		this.name = name;
		this.regexp = regexp;
		this.desc = desc;
		compile();
	}
}
