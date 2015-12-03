package org.cld.hadooputil;

import java.io.BufferedReader;

import org.apache.hadoop.fs.FileSystem;

public class HdfsReader {
	
	private FileSystem fs;
	private BufferedReader br;
	
	public HdfsReader(FileSystem fs, BufferedReader br){
		this.fs = fs;
		this.br = br;
	}

	public FileSystem getFs() {
		return fs;
	}

	public void setFs(FileSystem fs) {
		this.fs = fs;
	}

	public BufferedReader getBr() {
		return br;
	}

	public void setBr(BufferedReader br) {
		this.br = br;
	}
	
	

}
