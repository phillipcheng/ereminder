package org.cld.stocksheet.test;

import java.io.IOException;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.persist.RangeEntry;
import org.cld.stock.strategy.persist.StrategyPersistMgr;
import org.cld.stocksheet.AppsScriptApi;
import org.cld.stocksheet.SheetMgr;
import org.cld.trade.mgmt.AutoTraderMXBean;
import org.junit.Test;

public class TestAppsScriptApi {
	private static Logger logger =  LogManager.getLogger(TestAppsScriptApi.class);
	
	@Test
	public void testGetFoldersUnderRoot() throws IOException{
		AppsScriptApi.getFoldersUnderRoot();
	}

	@Test
	public void installRangeData() throws IOException{
		List<RangeEntry> rel = AppsScriptApi.getRangeData();
		SheetMgr sheetMgr = new SheetMgr();
		StrategyPersistMgr.cleanRangeEntry(sheetMgr.getDbconf());
		StrategyPersistMgr.installRangeEntry(sheetMgr.getDbconf(), rel);
	}
	
	@Test
	public void jmxInvokeUpdateRangeStrategyData() throws IOException{
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://192.85.246.17:9595/jmxrmi");
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		try {
			ObjectName mbeanName = new ObjectName("stock.trade:type=AutoTrader");
			AutoTraderMXBean atMBean = JMX.newMBeanProxy(mbsc, mbeanName, AutoTraderMXBean.class, true);
			atMBean.setupStrategys();
		}catch(Exception e){
			logger.error("", e);
		}
	}
	
	@Test
	public void updateRangeDataClose() throws IOException{
		AppsScriptApi.updateMarketData(false);
	}
	
	@Test
	public void dumpSymbols() throws IOException{
		String fileName = "symbols.txt";
		AppsScriptApi.dumpSymbols(fileName);
	}
}
