package org.cld.datacrawl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.RerunableTask;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.client.AppClientNodeInf;
import org.cld.taskmgr.client.ClientNodeImpl;
import org.cld.taskmgr.entity.Task;

//this is not needed when using hadoop task manager
public class CrawlClientNode implements AppClientNodeInf, CrawlConfListener {

	private static Logger logger =  LogManager.getLogger(CrawlClientNode.class);
	
	private ClientNodeImpl taskNode;
	private CrawlConf cconf;
	
	public static final String TASK_RUN_PARAM_CCONF="cconf";
	
	public ClientNodeImpl getTaskNode() {
		return taskNode;
	}
	public void setTaskNode(ClientNodeImpl taskNode) {
		this.taskNode = taskNode;
	}

	public CrawlConf getCConf(){
		return cconf;
	}
	public void setCConf(CrawlConf cconf){
		this.cconf = cconf;
	}

	@Override
	public void reload(){
		cconf.reload();
	}
	
	@Override
	public void start(ClientNodeImpl node, AppConf aconf) {
		this.taskNode = node;
		this.cconf = (CrawlConf)aconf;
		
		CrawlUtil.setupSessionFactory(node.getNC(), cconf);
		
		node.getTaskInstanceManager().setTaskSF((SessionFactory)DBFactory.getDBSF(
				node.getNC().getNodeId(), TaskMgr.moduleName));
		//
		this.cconf.addListener(this);
	}

	@Override
	public void stop(ClientNodeImpl node) {
	}
	
	@Override
	/**
	 * Turn a task into a runnable
	 * @param te: task entry
	 * @param runNum: how many times this task has been run, used to define priority
	 * @return
	 */
	public Runnable getRunnableTask(Task t, int runNum){
		TaskMgr tm = cconf.getTaskMgr();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TASK_RUN_PARAM_CCONF, cconf);
		return new RerunableTask(taskNode, tm, t, runNum, cconf.getTaskSF(), taskNode.getTaskInstanceManager(), false, params);
	}
	
	
	@Override
	public void crawlConfChanged(CrawlConfChangedEvent ccce) {
		logger.debug("ccce got:" + ccce);
		if (ccce.getOpType() == CrawlConfChangedEvent.OP_ADD){
			
		}else if (ccce.getOpType() == CrawlConfChangedEvent.OP_REMOVE){
			
		}else if (ccce.getOpType() == CrawlConfChangedEvent.OP_UPDATE){
			if (ccce.getPropName() == CrawlConfChangedEvent.PROP_NAME_PRODUCT_DEF){
				logger.debug("crawl conf product def changed.");

				ProductConf prdConf = ccce.getPrdConfValue();
				
				CrawlUtil.addPrdConfToSessionFactory(prdConf, cconf, taskNode.getNC().getNodeId());
			}
		}
	}
}
