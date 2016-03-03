package algo.util;

import java.util.ArrayList;
import java.util.List;

public class Interval {
	public int start;
	public int end;
	public Interval() { start = 0; end = 0; }
	public Interval(int s, int e) { start = s; end = e; }
	public Interval(String s){
		s = s.replace("[", "").replace("]", "");
		String[] str = s.split(",");
		start = Integer.parseInt(str[0]);
		end = Integer.parseInt(str[1]);
	}
	public String toString(){
		return String.format("[%d,%d]", start, end);
	}
	
	//[1,3],[2,6],[8,10],[15,18]
	public static List<Interval> fromString(String str){
		String[] stra = str.split("\\],\\[");
		List<Interval> li = new ArrayList<Interval>();
		for (String a:stra){
			li.add(new Interval(a));
		}
		return li;
	}
	
	public static String toString(List<Interval> li){
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<li.size(); i++){
			Interval it = li.get(i);
			if (i>0){
				sb.append(",");
			}
			sb.append(it.toString());
		}
		return sb.toString();
	}
}
