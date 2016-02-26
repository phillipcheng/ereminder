package leet.algo;

import java.util.ArrayList;
import java.util.List;


class Segment{
	//0:constants
	//1:.
	//2:.*/c*
	int type; 
	String constants;
	char c;
	
	Segment(String constants){
		this.type = 0;
		this.constants =constants;
	}
	
	Segment(){//1
		this.type = 1;
	}
	
	Segment(char c){
		this.type = 2;
		this.c = c;
	}
	
	public String toString(){
		if (type==0){
			return "[seg const:" + constants + "]";
		}else if (type==1){
			return "[seg .]";
		}else if (type==2){
			return "[seg " + c + "*]";
		}else{
			return "illegal seg";
		}
	}
}

//Implement regular expression matching with support for '.' and '*'.
public class RegExpMatch {
	
	public static final boolean isDebug = true;
	void log(Object s){
		if (isDebug){
			System.out.println(s);
		}
	}
	
	
	private int idx=0; //the idx of the input char array under matching
	private char[] chars; //the input char array
	

	/**
	 * 
	 * @param p
	 * @return null for illegal pattern
	 */
	List<Segment> parseP(String p){
		List<Segment> sl = new ArrayList<Segment>();
		char[] chars = p.toCharArray();
		char curCh;
		String buffer="";//content which is not added to result
		for (int i=0; i<chars.length; i++){
			curCh = chars[i];
			if (curCh=='.'){
				if ("".equals(buffer)){
					buffer=".";
				}else if (".".equals(buffer)){
					Segment s = new Segment();
					sl.add(s);
					buffer=".";
				}else if ("*".equals(buffer)){//should not happen
					log("[matched]*.  is ilegal." + p);
					return null;
				}else {//buffer should be a constant string
					Segment s = new Segment(buffer);
					sl.add(s);
					buffer=".";
				}
			}else if (curCh=='*'){
				if ("".equals(buffer)){
					log("[matched]*  is ilegal." + p);
					return null;
				}else if (".".equals(buffer)){
					Segment s = new Segment('.');
					sl.add(s);
					buffer="";
				}else if ("*".equals(buffer)){
					log("[matched]**  is ilegal." + p);
					return null;
				}else{// abc*
					
					if (buffer.length()>1){
						String a = buffer.substring(0, buffer.length()-1);
						Segment s = new Segment(a);
						sl.add(s);
					}
					
					char c = buffer.charAt(buffer.length()-1);
					Segment s= new Segment(c);
					sl.add(s);
					buffer="";
				}
			}else{//current char is a constant ch
				if ("".equals(buffer)){
					buffer=curCh+"";
				}else if (".".equals(buffer)){
					Segment s = new Segment();
					sl.add(s);
					buffer=curCh+"";
				}else if ("*".equals(buffer)){
					log("[matched]*const  is ilegal." + p);
					return null;
				}else{
					buffer+=(curCh+"");
				}
			}			
		}
		//treat the last buffer
		if ("".equals(buffer)){
			//do nothing
		}else if (".".equals(buffer)){
			Segment s = new Segment();
			sl.add(s);
		}else if ("*".equals(buffer)){//should not happen
			log("[matched]*  is ilegal." + p);
			return null;
		}else {//buffer should be a constant string
			Segment s = new Segment(buffer);
			sl.add(s);
		}
		return sl;
	}
	
	private boolean matchType0(Segment seg){
		int len = seg.constants.length();
		if (idx+len>chars.length){
			log(seg + "not matched:" + new String(chars, idx, chars.length-idx));
			return false;
		}else{
			String str = new String(chars, idx, len);
			if (seg.constants.equals(str)){
				idx+=len;
				log(seg + " matched:" + str);
				return true;
			}else{
				log(seg + " not matched:" + new String(chars, idx, len));
				return false;
			}
		}
	}
	
	public boolean isMatch(String s, String p) {
		log("pattern:" + p);
		log("input:" + s);
		
		chars = s.toCharArray();
		List<Segment> ls = parseP(p);
		
		log(ls);
		
		
		if (ls!=null){
			idx=0;//idx of the chars
			for (int i=0;i<ls.size(); i++){
				Segment seg = ls.get(i);
				if (seg.type==0){//constant
					boolean b = matchType0(seg);
					if (!b){
						return false;
					}
				}else if (seg.type==1){//.
					if (idx>=chars.length){
						log("no char to match .: idx:" + idx + ", len:" + chars.length);
					}else{
						//not the last char
						log(seg + " matched: "+ chars[idx]);
						idx++;
					}
				}else if (seg.type==2){//.*
					List<Segment> bufferLS =  new ArrayList<Segment>();
					while (seg.type!=0){
						bufferLS.add(seg);
						i++;
						if (i<ls.size()){
							seg = ls.get(i);
						}else{
							break;
						}
					}
					//count seg1
					int count1=0;
					for (Segment s1: bufferLS){
						if (s1.type==1){
							count1++;
						}
					}
					if (seg.type==0){
						//matching [seg2,(seg1|seg2)*] seg0
						int k=idx;
						boolean pass=false;
						for (k = idx; k<=chars.length-seg.constants.length(); k++){
							if (seg.constants.equals(new String(chars, k, seg.constants.length()))){
								if (k-idx>=count1){
									log("seg list:" + bufferLS + " and " + seg + " mathces: " + new String(chars, idx, k+seg.constants.length()-idx));
									idx = k+seg.constants.length();
									pass=true;
									break;
								}
							}
						}
						if (!pass){
							log("seg list:" + bufferLS + " does not match: " + new String(chars, idx, chars.length-idx));
							return false;
						}
					}else{
						//reached the end of seg list
						//matching [seg2, (seg1|seg2)*] to all the rest of the chars						
						if (idx+count1<=chars.length){
							log("seg list:" + bufferLS + " mathces: " + new String(chars, idx, chars.length-idx));
							return true;
						}else{
							log("seg list:" + bufferLS + " can not match: " + new String(chars, idx, chars.length-idx));
							return false;
						}
					}					
				}else{
					log("wrong segment type:" + seg.type);
				}
			}
			
			if (idx==chars.length){
				return true; //matched all the pattern
			}else if (idx<chars.length){
				log("still remain input not matched:" + new String(chars, idx, chars.length-idx));
				return false;
			}else{
				log("Never here.");
				return false;
			}
		}else{
			log("illegal Pattern:" + p);
			return false;
		}
    }

}
