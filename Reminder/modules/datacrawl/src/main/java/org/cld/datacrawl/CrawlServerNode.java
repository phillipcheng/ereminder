package org.cld.datacrawl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.DBFactory;
import org.cld.taskmgr.AppConf;
import org.cld.taskmgr.TaskMgr;
import org.cld.taskmgr.TaskUtil;
import org.cld.taskmgr.entity.Task;
import org.cld.taskmgr.entity.TaskPersistMgr;
import org.cld.taskmgr.server.AppServerNodeInf;
import org.cld.taskmgr.server.ServerNodeImpl;
import org.hibernate.SessionFactory;

public class CrawlServerNode implements AppServerNodeInf, CrawlConfListener {

	private static Logger logger =  LogManager.getLogger(CrawlServerNode.class);
	
	private CrawlConf cconf;
	private ServerNodeImpl serverNode;
	
	
	public CrawlConf getCConf(){
		return cconf;
	}
	
	public void setCConf(CrawlConf cconf){
		this.cconf = cconf;
	}
	
	public ServerNodeImpl getServerNode(){
		return serverNode;
	}
	
	public void setServerNode(ServerNodeImpl serverNode){
		this.serverNode = serverNode;
	}
	
	
	@Override
	public void start(ServerNodeImpl node, AppConf aconf) {
		this.cconf = (CrawlConf)aconf;
		this.serverNode = node;
		
		CrawlUtil.setupSessionFactory(node.getNC(), cconf);
		
		serverNode.setTaskMgrSF((SessionFactory)DBFactory.getDBSF(node.getNC().getNodeId(), TaskMgr.moduleName));
		//register server with stored tasks and startable tasks
		List<Task> telist = TaskPersistMgr.getMyTasks(cconf.getTaskSF(), serverNode.getNC().getNodeId());
		List<Task> telist2 = cconf.getTaskMgr().getStartableTasks();
		TaskPersistMgr.addTasks(serverNode.getTaskMgrSF(), telist2);
		telist.addAll(telist2);
		logger.info("startable tasks:" + telist);
		serverNode.localRegister(serverNode.getNC().getNodeId(), serverNode.getNC(), TaskUtil.convertToSet(telist));
		
		cconf.addListener(this);

	}
	
	@Override
	public void stop(ServerNodeImpl serverNode) {
	}
	
	@Override
	public void reload(){
		cconf.reload();
	}


	@Override
	public void crawlConfChanged(CrawlConfChangedEvent ccce) {
		logger.debug("ccce got:" + ccce);
		if (ccce.getOpType() == CrawlConfChangedEvent.OP_ADD){
		}else if (ccce.getOpType() == CrawlConfChangedEvent.OP_REMOVE){
		}else if (ccce.getOpType() == CrawlConfChangedEvent.OP_UPDATE){
			if (ccce.getPropName() == CrawlConfChangedEvent.PROP_NAME_PRODUCT_DEF){
			}
		}
	}

}
