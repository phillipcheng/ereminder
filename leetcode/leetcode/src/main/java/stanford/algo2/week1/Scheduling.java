package stanford.algo2.week1;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import algo.util.SortedKeyOrderedValueListMap;

public class Scheduling {

	private static Logger logger =  LogManager.getLogger(Scheduling.class);
	
	SortedKeyOrderedValueListMap<Float, Job> wmap = new SortedKeyOrderedValueListMap<Float, Job>();
	ScheduleType st;
	
	public Scheduling(ScheduleType st){
		this.st = st;
	}
	
	public void addJob(Job j){
		if (st == ScheduleType.difference){
			wmap.put((float)j.weight-j.length, j);
		}else if (st == ScheduleType.ratio){
			wmap.put(j.weight/(float)j.length, j);
		}
	}
	
	public long getWeightedCompleteTime(){
		long wct =0;
		long ct = 0;
		while (!wmap.isEmpty()){
			float key = wmap.lastKey();
			Job j = wmap.get(key);
			logger.info(String.format("key:%.2f, job %s selected.", key, j));
			wct+= j.weight * (ct + j.length);
			ct += j.length;
			wmap.removeTop(key);
		}
		return wct;
	}
}
