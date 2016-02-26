package stanford.algo2.test.week1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import stanford.algo2.week1.Job;
import stanford.algo2.week1.ScheduleType;
import stanford.algo2.week1.Scheduling;

public class TestSchduling {

	private static Logger logger =  LogManager.getLogger(TestSchduling.class);
	
	@Test
	public void test1() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(TestSchduling.class.getResourceAsStream("jobs.txt")));
		Scheduling sch = new Scheduling(ScheduleType.difference);
		int count = Integer.parseInt(br.readLine());
		logger.info(String.format("%d numbers of records.", count));
		while (--count>=0){
			String line = br.readLine();
			String[] wl = line.split(" ");
			int weight = Integer.parseInt(wl[0]);
			int length = Integer.parseInt(wl[1]);
			Job j = new Job(weight, length);
			sch.addJob(j);
		}
		long wct = sch.getWeightedCompleteTime();
		logger.info(String.format("wct:%d", wct));
		br.close();
	}
	
	@Test
	public void test2() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(TestSchduling.class.getResourceAsStream("jobs.txt")));
		Scheduling sch = new Scheduling(ScheduleType.ratio);
		int count = Integer.parseInt(br.readLine());
		logger.info(String.format("%d numbers of records.", count));
		while (--count>=0){
			String line = br.readLine();
			String[] wl = line.split(" ");
			int weight = Integer.parseInt(wl[0]);
			int length = Integer.parseInt(wl[1]);
			Job j = new Job(weight, length);
			sch.addJob(j);
		}
		long wct = sch.getWeightedCompleteTime();
		logger.info(String.format("wct:%d", wct));
		br.close();
	}
}
