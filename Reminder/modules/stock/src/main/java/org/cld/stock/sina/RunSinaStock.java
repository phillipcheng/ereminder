package org.cld.stock.sina;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunSinaStock {
	protected static Logger logger =  LogManager.getLogger(RunSinaStock.class);
	
	public static void main(String[] args) throws Exception {
		String propFile="";
		String marketId ="";
		boolean allHistory = false;
		String cmd="";
		if (args.length>=4){
			propFile = args[0];
			marketId = args[1];
			SinaStockBase ssb = new SinaStockBase(propFile, marketId);
			allHistory = Boolean.parseBoolean(args[2]);
			cmd = args[3];
			int argIdx=4;
			if ("brs_ids".equals(cmd)){
				ssb.run_browse_idlist();
			}
			//行情走势
			else if ("brs_tradedetail".equals(cmd)){
				if (allHistory){
					ssb.run_browse_tradedetail(SinaStockBase.HS_A_FIRST_DATE_DETAIL_TRADE);
				}else{
					if (args.length>=argIdx+1){
						String startDate = args[argIdx];
						ssb.run_browse_tradedetail(startDate);
					}else{
						System.out.println("propFile marketId 'false' 'brs_tradedetail' ?startDate?");
					}
				}
			}else if ("brs_rzrq".equals(cmd)){
				if (allHistory){
					ssb.run_browse_market_rzrq(SinaStockBase.HS_A_FIRST_DATE_RZRQ);
				}else{
					if (args.length>=argIdx+1){
						String startDate = args[argIdx];
						ssb.run_browse_tradedetail(startDate);
					}else{
						System.out.println("propFile marketId 'false' 'brs_rzrq' ?startDate?");
					}
				}
			}else if ("brs_mkt_history".equals(cmd)){
				if (allHistory){
					ssb.run_browse_market_history();
				}else{
					if (args.length>argIdx+2){
						int year=Integer.parseInt(args[argIdx]);
						int quarter = Integer.parseInt(args[argIdx+1]);
						ssb.run_browse_market_quarter(year, quarter);
					}else{
						System.out.println("propFile marketId 'false' 'brs_mkt_history' ?year? ?quarter?");
					}
				}
			}else if ("mrg_mkt_history".equals(cmd)){
				if (allHistory){
					ssb.run_merge_market_history();
				}else{
					if (args.length>argIdx+2){
						int year=Integer.parseInt(args[argIdx]);
						int quarter = Integer.parseInt(args[argIdx+1]);
						ssb.run_merge_market_history_quarter(year, quarter);
					}else{
						System.out.println("propFile marketId 'false' 'mrg_mkt_history' ?year? ?quarter?");
					}
				}
			}
			//公司资料
			else if ("brs_corp_info".equals(cmd)){
				ssb.run_browse_corp_info();
			}else if ("brs_corp_mgr".equals(cmd)){
				ssb.run_corp_manager(allHistory);
			}else if ("brs_corp_related".equals(cmd)){
				ssb.run_corp_related();
			}else if ("brs_corp_related_other".equals(cmd)){
				ssb.run_corp_related_other();
			}
			//发行分配
			else if ("brs_issue_sharebonus".equals(cmd)){
				ssb.run_issue_sharebonus();
			}
			//股本股东
			else if ("brs_stock_structure".equals(cmd)){
				ssb.run_stock_structure();
			}else if ("brs_stock_holder".equals(cmd)){
				ssb.run_stock_holder(allHistory);
			}else if ("brs_stock_holder_circulate".equals(cmd)){
				ssb.run_circulate_stock_holder(allHistory);
			}else if ("brs_stock_holder_fund".equals(cmd)){
				ssb.run_fund_stock_holder(allHistory);
			}
			//财务数据
			else if ("brs_fr".equals(cmd)){
				if (allHistory){
					ssb.run_browse_fr_history();
				}else{
					if (args.length>argIdx+2){
						int year=Integer.parseInt(args[argIdx]);
						int quarter = Integer.parseInt(args[argIdx+1]);
						ssb.run_browse_fr_quarter(year, quarter);
					}else{
						System.out.println("propFile marketId 'false' 'brs_fr' ?year? ?quarter?");
					}
				}
			}else if ("convert_fr_history_to_csv".equals(cmd)){
				ssb.run_convert_fr_history_tabular_to_csv();
			}else if ("split_fr_history_by_quarter".equals(cmd)){
				ssb.run_fr_reformat();
			}else if ("brs_fr_footnote".equals(cmd)){
				ssb.run_browse_fr_footnote_history();
			}else if ("brs_fr_achievenotice".equals(cmd)){
				ssb.run_fr_achievenotice();
			}else if ("brs_fr_guideline".equals(cmd)){
				ssb.run_fr_finance_guideline();
			}else if ("brs_fr_assetdevalue".equals(cmd)){
				ssb.run_fr_assetdevalue();
			}else{
				System.out.println(String.format("cmd %s not supported.", cmd));
			}
		}else{
			System.out.println("at least: propFile marketId allHistory cmd");
		}
	}
}
