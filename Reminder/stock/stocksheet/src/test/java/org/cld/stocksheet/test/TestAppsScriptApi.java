package org.cld.stocksheet.test;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.stock.strategy.persist.RangeEntry;
import org.cld.stock.strategy.persist.StrategyPersistMgr;
import org.cld.stocksheet.AppsScriptApi;
import org.cld.stocksheet.SheetMgr;
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
	public void updateRangeData() throws IOException{
		AppsScriptApi.updateMarketData();
	}
	
	@Test
	public void dumpSymbols() throws IOException{
		String fileName = "symbols.txt";
		AppsScriptApi.dumpSymbols(fileName);
	}
}
