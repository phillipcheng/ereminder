package org.cld.taskmgr.test;


import java.util.ArrayList;
import java.util.List;

import org.cld.taskmgr.entity.Task;

public class TaskMgrTestUtil {
	
	public static List<Task> getTaskList(int size){
		List<Task> tkList = new ArrayList<Task>();
		for (int i=0; i<size; i++){
			Task t = new Task();
			t.setName(i+"");
			t.setId(t.genId());
			tkList.add(t);
		}
		return tkList;
	}
	
	public static List<Task> getLongTaskList(int size){
		List<Task> tkList = new ArrayList<Task>();
		for (int i=0; i<size; i++){
			Task t = new Task();
			t.setName(i+"");
			t.setId(t.genId());
			tkList.add(t);
		}
		return tkList;
	}

}
