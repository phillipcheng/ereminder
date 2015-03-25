package org.cld.taskmgr;


public class Main {
	
	public static void usage(){
		System.out.print("main conf.file");
	}
	public static void main(String args[]) {
		if (args.length!=1)
			usage();
		else{
			String conf = args[0];
			Node n = new Node(conf);
			n.start(false);
		}
		
	}
}
