package org.cld.taskmgr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.util.IdUtil;
import org.cld.util.distribute.SimpleNodeConf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class TaskUtil {

	private static Logger logger =  LogManager.getLogger(TaskUtil.class);
	
	public static Set<Task> convertToSet(List<Task> tl){
		LinkedHashSet<Task> lhs = new LinkedHashSet<Task>();
		for (int i=0; i<tl.size(); i++){
			lhs.add(tl.get(i));
		}
		return lhs;
	}
	
	public static List<String> getKeyList(List<? extends Task> tl){
		List<String> kl = new ArrayList<String>();
		for (int i=0 ; i<tl.size(); i++){
			Task t = tl.get(i);
			kl.add(t.getId());
		}
		return kl;
	}
	
	public static List<String> getKeyList(Set<Task> tl){
		List<String> kl = new ArrayList<String>();
		Iterator<Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static Set<String> getKeySet(List<Task> tl){
		Set<String> kl = new HashSet<String>();
		Iterator<Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static Set<String> getKeySet(Set<? extends Task> tl){
		Set<String> kl = new HashSet<String>();
		Iterator<? extends Task> it = tl.iterator();
		while (it.hasNext()){			
			kl.add(it.next().getId());
		}
		return kl;
	}
	
	public static Map<String, SimpleNodeConf> getSimpleNodeConfs(Map<String, NodeConf> ncs){
		Map<String, SimpleNodeConf> sncMap = new HashMap<String, SimpleNodeConf>();
		Iterator<String> keys = ncs.keySet().iterator();
		while (keys.hasNext()){
			String key = keys.next();
			SimpleNodeConf snc = new SimpleNodeConf();
			NodeConf nc = ncs.get(key);
			snc.setNodeId(nc.getNodeId());
			snc.setServer(nc.isServer());
			snc.setThreadSize(nc.getThreadSize());
			sncMap.put(key, snc);
		}
		return sncMap;
	}
	
	public static String taskToJson(Task t){
		t.toParamData();
		ObjectWriter ow = new ObjectMapper().writer().with(new MinimalPrettyPrinter());
		try {
			String json = ow.writeValueAsString(t);
			return json;
		} catch (JsonProcessingException e) {
			logger.error("",e );
			return null;
		}
	}
	
	public static Task taskFromJson(String json){
		ObjectMapper mapper = new ObjectMapper();
		try {
			Task t = mapper.readValue(json, Task.class);
			Class<? extends Task> clazz = (Class<? extends Task>) Class.forName(t.getTtype());
			t =  mapper.readValue(json, clazz);
			t.fromParamData();
			return t;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	//send the taskList to the cluster
	public static void executeTasks(ClientNodeImpl taskNode, List<Task> taskList){
		Set<String> tks = TaskUtil.getKeySet(taskList);
		TaskPersistMgr.addTasks(taskNode.getTaskInstanceManager().getTaskSF(), 
				TaskUtil.convertToSet(taskList));
		try {
			if (taskNode.getSNI()!=null)
				taskNode.getSNI().addTasks(taskNode.getNC().getNodeId(), tks);
			else{
				//if server not found add to my executor (client node)
				List<String> tkl = TaskUtil.getKeyList(taskList);
				taskNode.clientAddTasks(tkl);
			}
		} catch (RemoteException e) {
			logger.error("", e);
		}
	}
}
