package org.cld.webconf;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datacrawl.CrawlClientNode;
import org.cld.datacrawl.CrawlConf;
import org.cld.datacrawl.CrawlServerNode;
import org.cld.datacrawl.test.CrawlTestUtil;
import org.cld.taskmgr.Node;

public class InitListener implements ServletContextListener {

	private static Logger logger = LogManager.getLogger("cld.jsp");
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();
		String testNodeProperties = ctx.getInitParameter(ConfServlet.TESTNODE_PROPERTIES_KEY);
		if (testNodeProperties!=null){
			ConfServlet.setCrawlTestNode(CrawlTestUtil.getCCNode(testNodeProperties));
			logger.info(String.format("init param: %s, %s", ConfServlet.TESTNODE_PROPERTIES_KEY, testNodeProperties));
		}
		ConfServlet.bookWebAppRoot = ctx.getInitParameter(ConfServlet.BOOK_WEBAPP_ROOT_KEY);
		logger.info(String.format("init param: %s, %s", ConfServlet.BOOK_WEBAPP_ROOT_KEY, ConfServlet.bookWebAppRoot));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
