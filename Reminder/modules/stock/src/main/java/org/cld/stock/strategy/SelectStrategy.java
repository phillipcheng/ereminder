package org.cld.stock.strategy;

import java.util.List;

import org.cld.datacrawl.CrawlConf;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class SelectStrategy {
	
	String name;
	String outputDir;
	Object[] scripts;//elements of the scripts are either string[] or string

	public SelectStrategy(){
	}
	
	public SelectStrategy(String name, String outputDir, Object[] scripts){
		this.name = name;
		this.outputDir = outputDir;
		this.scripts = scripts;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOutputDir() {
		return outputDir;
	}
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	public Object[] getScripts() {
		return scripts;
	}
	public void setScripts(Object[] scripts) {
		this.scripts = scripts;
	}
	
	public abstract List<String> select(CrawlConf cconf, SelectStrategy ss, Object param);

}
