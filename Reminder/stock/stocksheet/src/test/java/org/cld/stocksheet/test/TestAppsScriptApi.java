package org.cld.stocksheet.test;

import java.io.IOException;
import java.util.List;

import org.cld.stock.strategy.persist.RangeEntry;
import org.cld.stock.strategy.persist.StrategyPersistMgr;
import org.cld.stocksheet.AppsScriptApi;
import org.cld.stocksheet.SheetMgr;
import org.junit.Test;

public class TestAppsScriptApi {
	
	@Test
	public void testGetFoldersUnderRoot() throws IOException{
		AppsScriptApi.getFoldersUnderRoot();
	}

	@Test
	public void mergeRangeData() throws IOException{
		List<RangeEntry> rel = AppsScriptApi.getRangeData();
		SheetMgr sheetMgr = new SheetMgr();
		StrategyPersistMgr.mergeRangeEntry(sheetMgr.getDbconf(), rel);
	}
	
	@Test
	public void updateRangeData() throws IOException{
		AppsScriptApi.updateMarketData();
	}
}
