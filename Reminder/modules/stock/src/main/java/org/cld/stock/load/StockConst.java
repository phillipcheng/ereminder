package org.cld.stock.load;

public class StockConst {
	public static final String param_sh_columns="columns";
	public static final String param_sh_rows="rows";

	public static final String sh_periodid_12m="5000";
	public static final String sh_periodid_9m="4400";
	public static final String sh_periodid_6m="1000";
	public static final String sh_periodid_3m="4000";
	
	public static final String param_sz_basic="basicInfo";
	public static final String param_sz_other="otherinfo";
	public static final String sz_periodid_12m="GB0110";
	public static final String sz_periodid_9m="GB0710";
	public static final String sz_periodid_6m="GB0310";
	public static final String sz_periodid_3m="GB0510";
	
	//sample id: 600012.SS&report_period_id=5000
	public static final String SH_REPORT_PERIOD_ID_KEY="report_period_id=";
	public static final String SH_REPORT_ID_KEY=".SS&";
	
	public static final String SH_STOCK_SITE_ID="shse-stock-basic";
	public static final String SZ_STOCK_SITE_ID="szse-stock-basic";
	public static final String HK_STOCK_SITE_ID="hkse-stock-basic";
	
	public static final int INT=1;
	public static final int BIGINT=2;
	public static final int FLOAT=3;
	public static final int STRING=4;
	public static final int DATE=5;
	//
	public static final String SH_STOCK_MARKET_ID="SH";
	public static final String SZ_STOCK_MARKET_ID="SZ";
	public static final String HK_STOCK_MARKET_ID="HK";
	
	public static final int TCBI_INSERT_PARAM_NUMBER=215;
	public static final int[] TCBI_TYPES = new int[TCBI_INSERT_PARAM_NUMBER];
	
	public static final int idx_mid=0;//string
	{TCBI_TYPES[idx_mid]=STRING;}
	public static final int idx_tid=1;//string
	{TCBI_TYPES[idx_tid]=STRING;}
	public static final int idx_pid=2;//date
	{TCBI_TYPES[idx_pid]=DATE;}
	//基本信息 3 to 31
	public static final int idx_name=3;//string  '公司法定中文名称',
	{TCBI_TYPES[idx_name]=STRING;}
	public static final int idx_legalrep=4;//string  '公司法定代表人',
	{TCBI_TYPES[idx_legalrep]=STRING;}
	public static final int idx_companyaddr=5;//string  '公司注册地址',
	{TCBI_TYPES[idx_companyaddr]=STRING;}
	public static final int idx_zipcode=6;//string  '公司办公地址邮政编码',
	{TCBI_TYPES[idx_zipcode]=STRING;}
	public static final int idx_website=7;//string  '网址',
	{TCBI_TYPES[idx_website]=STRING;}
	public static final int idx_bsname=8;//string  '董事会秘书姓名',
	{TCBI_TYPES[idx_bsname]=STRING;}
	public static final int idx_bsphone=9;//string  '董事会秘书电话',
	{TCBI_TYPES[idx_bsphone]=STRING;}
	public static final int idx_bsemail=10;//string  '董事会秘书电子信箱',
	{TCBI_TYPES[idx_bsemail]=STRING;}
	public static final int idx_stockholdernum=11;//int '报告期末股东总数',
	{TCBI_TYPES[idx_stockholdernum]=INT;}
	public static final int idx_numberbonussharepertenshare=12;//float '每10股送红股数',
	{TCBI_TYPES[idx_numberbonussharepertenshare]=FLOAT;}
	public static final int idx_dividentpertenshare=13;//float '每10股派息数',
	{TCBI_TYPES[idx_dividentpertenshare]=FLOAT;}
	public static final int idx_numbershareconvertedbycaptitalreservepertenshare=14;//float '每10股转增数',
	{TCBI_TYPES[idx_numbershareconvertedbycaptitalreservepertenshare]=FLOAT;}
	public static final int idx_revenuethisperiod=15;//float '本期营业收入',
	{TCBI_TYPES[idx_revenuethisperiod]=FLOAT;}
	public static final int idx_profitthisperiod=16;//float '本期营业利润',
	{TCBI_TYPES[idx_profitthisperiod]=FLOAT;}
	public static final int idx_profitthisyear=17;//float '利润总额',
	{TCBI_TYPES[idx_profitthisyear]=FLOAT;}
	public static final int idx_profitbelongstockholder=18;//float '归属于上市公司股东的净利润',
	{TCBI_TYPES[idx_profitbelongstockholder]=FLOAT;}
	public static final int idx_profitshn=19;//float '归属于上市公司股东的扣除非经常性损益的净利润',
	{TCBI_TYPES[idx_profitshn]=FLOAT;}
	public static final int idx_cashflow=20;//float '经营活动产生的现金流量净额',
	{TCBI_TYPES[idx_cashflow]=FLOAT;}
	public static final int idx_totalCapital=21;//float '总资产',
	{TCBI_TYPES[idx_totalCapital]=FLOAT;}
	public static final int idx_equityStakeHolder=22;//float '所有者权益',
	{TCBI_TYPES[idx_equityStakeHolder]=FLOAT;}
	public static final int idx_basicEarningPerShare=23;//float '基本每股收益',
	{TCBI_TYPES[idx_basicEarningPerShare]=FLOAT;}
	public static final int idx_dilutedEarningPerShare=24;//float '稀释每股收益',
	{TCBI_TYPES[idx_dilutedEarningPerShare]=FLOAT;}
	public static final int idx_basicEpsNetOfNonRecurringGainsAndLosses=25;//float '扣除非经常性损益后的基本每股收益',
	{TCBI_TYPES[idx_basicEpsNetOfNonRecurringGainsAndLosses]=FLOAT;}
	public static final int idx_fullyDilutedReturnOnEquityRatio=26;//float '全面摊薄净资产收益率',
	{TCBI_TYPES[idx_fullyDilutedReturnOnEquityRatio]=FLOAT;}
	public static final int idx_weightedAverageReturnOnEquityRatio=27;//float '加权平均净资产收益率',
	{TCBI_TYPES[idx_weightedAverageReturnOnEquityRatio]=FLOAT;}
	public static final int idx_fullyDilutedReturnOnEquityNetOfNonRecurringGainsAndLossesRatio=28;//float '扣除非经常性损益后全面摊薄净资产收益率',
	{TCBI_TYPES[idx_fullyDilutedReturnOnEquityNetOfNonRecurringGainsAndLossesRatio]=FLOAT;}
	public static final int idx_weightedAverageReturnOnEquityNetOfNonRecurringGainsAndLossesRatio=29;//float '扣除非经常性损益后的加权平均净资产收益率',
	{TCBI_TYPES[idx_weightedAverageReturnOnEquityNetOfNonRecurringGainsAndLossesRatio]=FLOAT;}
	public static final int idx_netCashFlowsPerShareFromOperatingActivities=30;//float '每股经营活动产生的现金流量净额',
	{TCBI_TYPES[idx_netCashFlowsPerShareFromOperatingActivities]=FLOAT;}
	public static final int idx_netAssetsPerShareAttributableToShareholdersOfListedCompany=31;//float '归属于上市公司股东的每股净资产',
	{TCBI_TYPES[idx_netAssetsPerShareAttributableToShareholdersOfListedCompany]=FLOAT;}
	//--股本结构  32-47
	public static final int idx_numberOfStateOwnedFloatingSharesWithTradingLimitedConditions=32;//bigint '国家持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_numberOfStateOwnedFloatingSharesWithTradingLimitedConditions]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByStateLegalEntity=33;//bigint '国有法人持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByStateLegalEntity]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticCapital=34;//bigint '其他有限售条件的内资流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticCapital]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticLegalEntity=35;//bigint '境内法人持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticLegalEntity]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticNaturalPerson=36;//bigint '境内自然人持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByDomesticNaturalPerson]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignCapital=37;//bigint '有限售条件的外资流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignCapital]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignLegalEntity=38;//bigint '境外法人持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignLegalEntity]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignNaturalPerson=39;//bigint '境外自然人持有的有限售条件流通股份数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditionsHeldByForeignNaturalPerson]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithOtherTradingLimitedConditions=40;//bigint '其他有限售股流通股数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithOtherTradingLimitedConditions]=BIGINT;}
	public static final int idx_NumberOfFloatingSharesWithTradingLimitedConditions=41;//bigint '有限售条件流通股数',
	{TCBI_TYPES[idx_NumberOfFloatingSharesWithTradingLimitedConditions]=BIGINT;}
	public static final int idx_NumberOfCommonFloatingSharesRMBDenominatedWithoutTradingLimitedConditions=42;//bigint '无限售条件人民币普通股数',
	{TCBI_TYPES[idx_NumberOfCommonFloatingSharesRMBDenominatedWithoutTradingLimitedConditions]=BIGINT;}
	public static final int idx_NumberOfDomesticallyListedSharesWithoutTradingLimitedConditionsHeldByForeignInvestors=43;//bigint '无限售条件境内上市的外资股数',
	{TCBI_TYPES[idx_NumberOfDomesticallyListedSharesWithoutTradingLimitedConditionsHeldByForeignInvestors]=BIGINT;}
	public static final int idx_NumberOfOverseasListedSharesWithoutTradingLimitedConditionsHeldByForeignInvestors=44;//bigint '无限售条件境外上市的外资股数',
	{TCBI_TYPES[idx_NumberOfOverseasListedSharesWithoutTradingLimitedConditionsHeldByForeignInvestors]=BIGINT;}
	public static final int idx_NumberOfOtherListedMarketableSharesWithoutTradingLimitedConditions=45;//bigint '其他无限售条件已上市流通股份数',
	{TCBI_TYPES[idx_NumberOfOtherListedMarketableSharesWithoutTradingLimitedConditions]=BIGINT;}
	public static final int idx_SubTotalOfFloatingSharesWithoutTradingLimitedConditions=46;//bigint '无限售条件流通股份合计',
	{TCBI_TYPES[idx_SubTotalOfFloatingSharesWithoutTradingLimitedConditions]=BIGINT;}
	public static final int idx_TotalShares=47;//bigint '股份总数',
	{TCBI_TYPES[idx_TotalShares]=BIGINT;}
	//--another 16 ratio is omitted
	//--资产负债表
	public static final int idx_monetaryFunds=48;//float '货币资金',
	{TCBI_TYPES[idx_monetaryFunds]=FLOAT;}
	public static final int idx_transactionSettlementFunds=49;//float '结算备付金',
	{TCBI_TYPES[idx_transactionSettlementFunds]=FLOAT;}
	public static final int idx_loansToOtherBank =50;//float '拆出资金',
	{TCBI_TYPES[idx_loansToOtherBank]=FLOAT;}
	public static final int idx_tradingFinancialAssets=51;//float '交易性金融资产',
	{TCBI_TYPES[idx_tradingFinancialAssets]=FLOAT;}
	public static final int idx_notesReceivable =52;//float '应收票据',
	{TCBI_TYPES[idx_notesReceivable]=FLOAT;}
	public static final int idx_accountsReceivable=53;//float '应收帐款',
	{TCBI_TYPES[idx_accountsReceivable]=FLOAT;}
	public static final int idx_accountsInAdvance=54;//float '预付帐款',
	{TCBI_TYPES[idx_accountsInAdvance]=FLOAT;}
	public static final int idx_insurancePremiumReceivable=55;//float '应收保费',
	{TCBI_TYPES[idx_insurancePremiumReceivable]=FLOAT;}
	public static final int idx_reinsurancePremiumReceivable =56;//float '应收分保账款',
	{TCBI_TYPES[idx_reinsurancePremiumReceivable]=FLOAT;}
	public static final int idx_reserveReinsuranceContractReceivable=57;//float '应收分保合同准备金',
	{TCBI_TYPES[idx_reserveReinsuranceContractReceivable]=FLOAT;}
	public static final int idx_interestReceivable=58;//float '应收利息',
	{TCBI_TYPES[idx_interestReceivable]=FLOAT;}
	public static final int idx_dividentReceivable=59;//float '应收股利',
	{TCBI_TYPES[idx_dividentReceivable]=FLOAT;}
	public static final int idx_otherReceivable=60;//float '其他应收款',
	{TCBI_TYPES[idx_otherReceivable]=FLOAT;}
	public static final int idx_BuyInAndReturnToAndSellFinancialAssets=61;//float '买入返售金融资产',
	{TCBI_TYPES[idx_BuyInAndReturnToAndSellFinancialAssets]=FLOAT;}
	public static final int idx_inventories=62;//float '库存',
	{TCBI_TYPES[idx_inventories]=FLOAT;}
	public static final int idx_nonCurrentAssetsExpireInAYear=63;//float '一年内到期的非流动资产',
	{TCBI_TYPES[idx_nonCurrentAssetsExpireInAYear]=FLOAT;}
	public static final int idx_otherCurrentAssets=64;//float '其他流动资产',
	{TCBI_TYPES[idx_otherCurrentAssets]=FLOAT;}
	public static final int idx_SubTotalOfCurrentAssets=65;//float '流动资产合计',
	{TCBI_TYPES[idx_SubTotalOfCurrentAssets]=FLOAT;}
	public static final int idx_DisbursementOfAdvancesAndLoans=66;//float '发放贷款和垫款',
	{TCBI_TYPES[idx_DisbursementOfAdvancesAndLoans]=FLOAT;}
	public static final int idx_AvailableForSaleFinancialAssets=67;//float '可供出售金融资产',
	{TCBI_TYPES[idx_AvailableForSaleFinancialAssets]=FLOAT;}
	public static final int idx_HeldToMaturityInvestment=68;//float '持有至到期投资',
	{TCBI_TYPES[idx_HeldToMaturityInvestment]=FLOAT;}
	public static final int idx_LongTermAccountsReceivable=69;//float '长期应收款',
	{TCBI_TYPES[idx_LongTermAccountsReceivable]=FLOAT;}
	public static final int idx_LongTermEquityInvestment=70;//float '长期股权投资',
	{TCBI_TYPES[idx_LongTermEquityInvestment]=FLOAT;}
	public static final int idx_InvestmentRealEstates=71;//float '投资性房地产',
	{TCBI_TYPES[idx_InvestmentRealEstates]=FLOAT;}
	public static final int idx_FixedAssetsNet=72;//float '固定资产净额',
	{TCBI_TYPES[idx_FixedAssetsNet]=FLOAT;}
	public static final int idx_ConstructionInProgress=73;//float '在建工程',
	{TCBI_TYPES[idx_ConstructionInProgress]=FLOAT;}
	public static final int idx_ConstructionMaterials=74;//float '工程物资',
	{TCBI_TYPES[idx_ConstructionMaterials]=FLOAT;}
	public static final int idx_DisposalOfFixedAssets=75;//float '固定资产清理',
	{TCBI_TYPES[idx_DisposalOfFixedAssets]=FLOAT;}
	public static final int idx_capitalizedBiologicalAssets=76;//float '生产性生物资产',
	{TCBI_TYPES[idx_capitalizedBiologicalAssets]=FLOAT;}
	public static final int idx_OilAndGasAssets=77;//float '油气资产',
	{TCBI_TYPES[idx_OilAndGasAssets]=FLOAT;}
	public static final int idx_IntangibleAssets=78;//float '无形资产',
	{TCBI_TYPES[idx_IntangibleAssets]=FLOAT;}
	public static final int idx_RAndDExpenses=79;//float '开发支出',
	{TCBI_TYPES[idx_RAndDExpenses]=FLOAT;}
	public static final int idx_goodwill=80;//float '商誉',
	{TCBI_TYPES[idx_goodwill]=FLOAT;}
	public static final int idx_LongTermDeferredAndPrepaidExpenses=81;//float '长期待摊费用',
	{TCBI_TYPES[idx_LongTermDeferredAndPrepaidExpenses]=FLOAT;}
	public static final int idx_TotalDeferredTaxesDebitItem =82;//float '递延税款借项合计',
	{TCBI_TYPES[idx_TotalDeferredTaxesDebitItem]=FLOAT;}
	public static final int idx_OtherLongTermAssets=83;//float '其他长期资产',
	{TCBI_TYPES[idx_OtherLongTermAssets]=FLOAT;}
	public static final int idx_TotalNonCurrentAssets=84;//float '非流动资产合计',
	{TCBI_TYPES[idx_TotalNonCurrentAssets]=FLOAT;}
	public static final int idx_TotalAssets=85;//float '资产总计',
	{TCBI_TYPES[idx_TotalAssets]=FLOAT;}
	public static final int idx_ShortTermLoans=86;//float '短期借款',
	{TCBI_TYPES[idx_ShortTermLoans]=FLOAT;}
	public static final int idx_BorrowingsFromCentralBank=87;//float '向中央银行借款',
	{TCBI_TYPES[idx_BorrowingsFromCentralBank]=FLOAT;}
	public static final int idx_ReceiptOfDepositsAndDepositsFromOtherBanks =88;//float '吸收存款及同业存放',
	{TCBI_TYPES[idx_ReceiptOfDepositsAndDepositsFromOtherBanks]=FLOAT;}
	public static final int idx_LoansFromOtherBanks=89;//float '拆入资金',
	{TCBI_TYPES[idx_LoansFromOtherBanks]=FLOAT;}
	public static final int idx_TradingFinancialLiabilities=90;//float '交易性金融负债',
	{TCBI_TYPES[idx_TradingFinancialLiabilities]=FLOAT;}
	public static final int idx_NotesPayable=91;//float '应付票据',
	{TCBI_TYPES[idx_NotesPayable]=FLOAT;}
	public static final int idx_AccountsPayable=92;//float '应付帐款',
	{TCBI_TYPES[idx_AccountsPayable]=FLOAT;}
	public static final int idx_AccountsReceivedInAdvance=93;//float '预收帐款',
	{TCBI_TYPES[idx_AccountsReceivedInAdvance]=FLOAT;}
	public static final int idx_FundsFromSellingOutAndRepurchasingFinancialAssets=94;//float '卖出回购金融资产款',
	{TCBI_TYPES[idx_FundsFromSellingOutAndRepurchasingFinancialAssets]=FLOAT;}
	public static final int idx_FeesAndCommissionsPayable=95;//float '应付手续费及佣金',
	{TCBI_TYPES[idx_FeesAndCommissionsPayable]=FLOAT;}
	public static final int idx_EmployeeCompensationPayable=96;//float '应付职工薪酬',
	{TCBI_TYPES[idx_EmployeeCompensationPayable]=FLOAT;}
	public static final int idx_TaxesPayable=97;//float '应交税金',
	{TCBI_TYPES[idx_TaxesPayable]=FLOAT;}
	public static final int idx_InterestsPayable =98;//float '应付利息',
	{TCBI_TYPES[idx_InterestsPayable]=FLOAT;}
	public static final int idx_DividentsPayable=99;//float '应付股利',
	{TCBI_TYPES[idx_DividentsPayable]=FLOAT;}
	public static final int idx_OtherPayable=100;//float '其他应付款',
	{TCBI_TYPES[idx_OtherPayable]=FLOAT;}
	public static final int idx_ReinsuredAccountsPayable=101;//float '应付分保账款',
	{TCBI_TYPES[idx_ReinsuredAccountsPayable]=FLOAT;}
	public static final int idx_ReservesForInsurance=102;//float '保险合同准备金',
	{TCBI_TYPES[idx_ReservesForInsurance]=FLOAT;}
	public static final int idx_FundsFromSecuritiesTradingAgency=103;//float '代理买卖证券款',
	{TCBI_TYPES[idx_FundsFromSecuritiesTradingAgency]=FLOAT;}
	public static final int idx_FundsFromUnderwritingSecuritiesAgency=104;//float '代理承销证券款',
	{TCBI_TYPES[idx_FundsFromUnderwritingSecuritiesAgency]=FLOAT;}
	public static final int idx_LongTermLiabilitiesDueWithinOneYear=105;//float '一年内到期的长期负债',
	{TCBI_TYPES[idx_LongTermLiabilitiesDueWithinOneYear]=FLOAT;}
	public static final int idx_OtherCurrentLiabilities=106;//float '其他流动负债',
	{TCBI_TYPES[idx_OtherCurrentLiabilities]=FLOAT;}
	public static final int idx_SubTotalOfCurrentLiabilities=107;//float '流动负债合计',
	{TCBI_TYPES[idx_SubTotalOfCurrentLiabilities]=FLOAT;}
	public static final int idx_LongTermLoans=108;//float '长期借款',
	{TCBI_TYPES[idx_LongTermLoans]=FLOAT;}
	public static final int idx_BondsPayable=109;//float '应付债券',
	{TCBI_TYPES[idx_BondsPayable]=FLOAT;}
	public static final int idx_LongTermPayable=110;//float '长期应付款',
	{TCBI_TYPES[idx_LongTermPayable]=FLOAT;}
	public static final int idx_SpecialPayables=111;//float '专项应付款',
	{TCBI_TYPES[idx_SpecialPayables]=FLOAT;}
	public static final int idx_EstimatedLiabilities=112;//float '预计负债',
	{TCBI_TYPES[idx_EstimatedLiabilities]=FLOAT;}
	public static final int idx_TotalDeferredTaxesCreditItem=113;//float '递延税款贷项合计',
	{TCBI_TYPES[idx_TotalDeferredTaxesCreditItem]=FLOAT;}
	public static final int idx_OtherLongTermLiabilities=114;//float '其他长期负债',
	{TCBI_TYPES[idx_OtherLongTermLiabilities]=FLOAT;}
	public static final int idx_liabilityLongTermTotal=115;//float '长期负债合计',
	{TCBI_TYPES[idx_liabilityLongTermTotal]=FLOAT;}
	public static final int idx_liabilitytotal=116;//float '负债合计',
	{TCBI_TYPES[idx_liabilitytotal]=FLOAT;}
	public static final int idx_capitalpaidin=117;//float '股本',
	{TCBI_TYPES[idx_capitalpaidin]=FLOAT;}
	public static final int idx_capitalreserve=118;//float '资本公积',
	{TCBI_TYPES[idx_capitalreserve]=FLOAT;}
	public static final int idx_stocktreasury=119;//float '库存股',
	{TCBI_TYPES[idx_stocktreasury]=FLOAT;}
	public static final int idx_reservesurplus=120;//float '盈余公积',
	{TCBI_TYPES[idx_reservesurplus]=FLOAT;}
	public static final int idx_reserveordinaryrisk=121;//float '一般风险准备',
	{TCBI_TYPES[idx_reserveordinaryrisk]=FLOAT;}
	public static final int idx_profitundistributed=122;//float '未分配利润',
	{TCBI_TYPES[idx_profitundistributed]=FLOAT;}
	public static final int idx_capitalforeigncorrency=123;//float '外币报表折算差额',
	{TCBI_TYPES[idx_capitalforeigncorrency]=FLOAT;}
	public static final int idx_equityattribtoparentcompanytotal=124;//float 'Total equity attributable to the parent company owners 归属于母公司所有者权益合计',
	{TCBI_TYPES[idx_equityattribtoparentcompanytotal]=FLOAT;}
	public static final int idx_interestsminority=125;//float 'Minority Interests 少数股东权益',
	{TCBI_TYPES[idx_interestsminority]=FLOAT;}
	public static final int idx_equitytotalshareholders=126;//float '股东权益合计',
	{TCBI_TYPES[idx_equitytotalshareholders]=FLOAT;}
	public static final int idx_totalliabilityshareholderequity=127;//float 'Total Liabilities And Shareholders Equity 负债和股东权益合计',
	{TCBI_TYPES[idx_totalliabilityshareholderequity]=FLOAT;}
	//--利润表
	public static final int idx_revenuetotal=128;//float '营业总收入',
	{TCBI_TYPES[idx_revenuetotal]=FLOAT;}
	public static final int idx_revenue=129;//float '营业收入',
	{TCBI_TYPES[idx_revenue]=FLOAT;}
	public static final int idx_incomefinancialassetinterest=130;//float '金融资产利息收入',
	{TCBI_TYPES[idx_incomefinancialassetinterest]=FLOAT;}
	public static final int idx_earnedinsurancepremium=131;//float '已赚保费',
	{TCBI_TYPES[idx_earnedinsurancepremium]=FLOAT;}
	public static final int idx_feesandcommissionincome=132;//float '手续费及佣金收入',
	{TCBI_TYPES[idx_feesandcommissionincome]=FLOAT;}
	public static final int idx_totaloperatingcost=133;//float '营业总成本',
	{TCBI_TYPES[idx_totaloperatingcost]=FLOAT;}
	public static final int idx_operatingcost=134;//float '营业成本',
	{TCBI_TYPES[idx_operatingcost]=FLOAT;}
	public static final int idx_financialassetinterstexpense=135;//float '金融资产利息支出',
	{TCBI_TYPES[idx_financialassetinterstexpense]=FLOAT;}
	public static final int idx_feesandcommissionexpense=136;//float '手续费及佣金支出',
	{TCBI_TYPES[idx_feesandcommissionexpense]=FLOAT;}
	public static final int idx_refundedpremium=137;//float '退保金',
	{TCBI_TYPES[idx_refundedpremium]=FLOAT;}
	public static final int idx_netamountcompensationpayout=138;//float '赔付支出净额',
	{TCBI_TYPES[idx_netamountcompensationpayout]=FLOAT;}
	public static final int idx_netamountreservereinsurancecontract=139;//float '提取保险合同准备金净额',
	{TCBI_TYPES[idx_netamountreservereinsurancecontract]=FLOAT;}
	public static final int idx_policydividendpayment=140;//float '保单红利支出',
	{TCBI_TYPES[idx_policydividendpayment]=FLOAT;}
	public static final int idx_reinsuredexpense=141;//float '分保费用',
	{TCBI_TYPES[idx_reinsuredexpense]=FLOAT;}
	public static final int idx_businesstaxextra=142;//float '营业税金及附加',
	{TCBI_TYPES[idx_businesstaxextra]=FLOAT;}
	public static final int idx_salesexpense=143;//float '销售费用',
	{TCBI_TYPES[idx_salesexpense]=FLOAT;}
	public static final int idx_managementexpense=144;//float '管理费用',
	{TCBI_TYPES[idx_managementexpense]=FLOAT;}
	public static final int idx_financeexpense=145;//float '财务费用',
	{TCBI_TYPES[idx_financeexpense]=FLOAT;}
	public static final int idx_assetsimpairementloss=146;//float '资产减值损失',
	{TCBI_TYPES[idx_assetsimpairementloss]=FLOAT;}
	public static final int idx_incomeschangeinfairvalue=147;//float '公允价值变动收益',
	{TCBI_TYPES[idx_incomeschangeinfairvalue]=FLOAT;}
	public static final int idx_investmentincome=148;//float '投资收益',
	{TCBI_TYPES[idx_investmentincome]=FLOAT;}
	public static final int idx_investmentincomejointventurepartnership=149;//float '对联营企业和合营企业的投资收益',
	{TCBI_TYPES[idx_investmentincomejointventurepartnership]=FLOAT;}
	public static final int idx_foreignexchangeincome=150;//float '汇兑收益',
	{TCBI_TYPES[idx_foreignexchangeincome]=FLOAT;}
	public static final int idx_operateprofit=151;//float '营业利润',
	{TCBI_TYPES[idx_operateprofit]=FLOAT;}
	public static final int idx_nonoperatingrevenue=152;//float '营业外收入',
	{TCBI_TYPES[idx_nonoperatingrevenue]=FLOAT;}
	public static final int idx_nonoperatingexpenditure=153;//float '营业外支出',
	{TCBI_TYPES[idx_nonoperatingexpenditure]=FLOAT;}
	public static final int idx_netlossdisposalofnoncurrentassets=154;//float '非流动资产处置净损失',
	{TCBI_TYPES[idx_netlossdisposalofnoncurrentassets]=FLOAT;}
	public static final int idx_incomebeforetax=155;//float '利润总额',
	{TCBI_TYPES[idx_incomebeforetax]=FLOAT;}
	public static final int idx_incometax=156;//float '所得税',
	{TCBI_TYPES[idx_incometax]=FLOAT;}
	public static final int idx_netprofit=157;//float '净利润',
	{TCBI_TYPES[idx_netprofit]=FLOAT;}
	public static final int idx_netprofitattributabletoparentcompany=158;//float '归属于母公司所有者的净利润',
	{TCBI_TYPES[idx_netprofitattributabletoparentcompany]=FLOAT;}
	public static final int idx_minorityshareholderincome=159;//float '少数股东损益',
	{TCBI_TYPES[idx_minorityshareholderincome]=FLOAT;}
	public static final int idx_basiceps=160;//float '基本每股收益',
	{TCBI_TYPES[idx_basiceps]=FLOAT;}
	public static final int idx_dilutedeps=161;//float '稀释每股收益',
	{TCBI_TYPES[idx_dilutedeps]=FLOAT;}
	//--现金流量表
	public static final int idx_CashReceivedFromSellingCommoditiesAndProvidingLaborServices=162;//float '销售商品提供劳务收到的现金',
	{TCBI_TYPES[idx_CashReceivedFromSellingCommoditiesAndProvidingLaborServices]=FLOAT;}
	public static final int idx_NetIncreaseOfDepositsFromCustomersAndOtherBanks=163;//float '客户存款和同业存放款项净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfDepositsFromCustomersAndOtherBanks]=FLOAT;}
	public static final int idx_NetIncreaseOfBorrowingsFromCentralBank=164;//float '向中央银行借款净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfBorrowingsFromCentralBank]=FLOAT;}
	public static final int idx_NetIncreaseOfLoansFromOtherFinancialInstitutions=165;//float '向其他金融机构拆入资金净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfLoansFromOtherFinancialInstitutions]=FLOAT;}
	public static final int idx_CashReceivedFromReceivingInsurancePremiumOfOriginalInsuranceContract=166;//float '收到原保险合同保费取得的现金',
	{TCBI_TYPES[idx_CashReceivedFromReceivingInsurancePremiumOfOriginalInsuranceContract]=FLOAT;}
	public static final int idx_NetCashReceivedFromReinsuranceBusiness=167;//float '收到再保险业务现金净额',
	{TCBI_TYPES[idx_NetCashReceivedFromReinsuranceBusiness]=FLOAT;}
	public static final int idx_NetIncreaseOfPolicyHolderDepositsAndInvestmentFunds=168;//float '保户储金及投资款净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfPolicyHolderDepositsAndInvestmentFunds]=FLOAT;}
	public static final int idx_NetIncreaseOfDisposalOfTradingFinancialAssets=169;//float '处置交易性金融资产净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfDisposalOfTradingFinancialAssets]=FLOAT;}
	public static final int idx_CashReceivedFromInterestsFeesAndCommissions=170;//float '收取利息,手续费及佣金的现金',
	{TCBI_TYPES[idx_CashReceivedFromInterestsFeesAndCommissions]=FLOAT;}
	public static final int idx_NetIncreaseOfLoansFromOtherBanks=171;//float '拆入资金净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfLoansFromOtherBanks]=FLOAT;}
	public static final int idx_NetCapitalIncreaseOfRepurchaseBusiness=172;//float '回购业务资金净增加额',
	{TCBI_TYPES[idx_NetCapitalIncreaseOfRepurchaseBusiness]=FLOAT;}
	public static final int idx_RefundsOfTaxesAndLevies=173;//float '收到的税费返还',
	{TCBI_TYPES[idx_RefundsOfTaxesAndLevies]=FLOAT;}
	public static final int idx_ItemsOfOtherReceivedCashRelevantToOperatingActivities=174;//float '收到的其他与经营活动有关的现金',
	{TCBI_TYPES[idx_ItemsOfOtherReceivedCashRelevantToOperatingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashInflowsFromOperatingActivities=175;//float '经营活动现金流入小计',
	{TCBI_TYPES[idx_SubTotalOfCashInflowsFromOperatingActivities]=FLOAT;}
	public static final int idx_CashPaidForPurchasingCommoditiesAndReceivingLaborService=176;//float '购买商品接受劳务支付的现金',
	{TCBI_TYPES[idx_CashPaidForPurchasingCommoditiesAndReceivingLaborService]=FLOAT;}
	public static final int idx_NetIncreaseOfCustomerLoansAndAdvances=177;//float '客户贷款及垫款净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfCustomerLoansAndAdvances]=FLOAT;}
	public static final int idx_NetIncreaseOfDepositsInCentralBankAndOtherBanks=178;//float '存放中央银行和同业款项净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfDepositsInCentralBankAndOtherBanks]=FLOAT;}
	public static final int idx_CashPaidForIndemnityOfOriginalInsuranceContract=179;//float '支付原保险合同赔付款项的现金',
	{TCBI_TYPES[idx_CashPaidForIndemnityOfOriginalInsuranceContract]=FLOAT;}
	public static final int idx_CashPaidForInterestsFeesAndCommissions=180;//float '支付利息,手续费及佣金的现金',
	{TCBI_TYPES[idx_CashPaidForInterestsFeesAndCommissions]=FLOAT;}
	public static final int idx_CashPaidForPolicyDividends=181;//float '支付保单红利的现金',
	{TCBI_TYPES[idx_CashPaidForPolicyDividends]=FLOAT;}
	public static final int idx_CashPaidToAndOnBehalfOfEmployees=182;//float '支付给职工以及为职工支付的现金',
	{TCBI_TYPES[idx_CashPaidToAndOnBehalfOfEmployees]=FLOAT;}
	public static final int idx_CashPaidForTaxes=183;//float '支付的各项税费',
	{TCBI_TYPES[idx_CashPaidForTaxes]=FLOAT;}
	public static final int idx_CashPaidRelatingToOtherOperatingActivities=184;//float '支付的其他与经营活动有关的现金',
	{TCBI_TYPES[idx_CashPaidRelatingToOtherOperatingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashOutflowsFromOperatingActivities=185;//float '经营活动现金流出小计',
	{TCBI_TYPES[idx_SubTotalOfCashOutflowsFromOperatingActivities]=FLOAT;}
	public static final int idx_NetCashFlowsArisingFromOperatingActivities=186;//float '经营活动现金流量净额',
	{TCBI_TYPES[idx_NetCashFlowsArisingFromOperatingActivities]=FLOAT;}
	public static final int idx_CashReceivedFromSalesOfInvestments=187;//float '收回投资所收到的现金',
	{TCBI_TYPES[idx_CashReceivedFromSalesOfInvestments]=FLOAT;}
	public static final int idx_CashReceivedFromReturnOnInvestments=188;//float '取得投资收益所收到的现金',
	{TCBI_TYPES[idx_CashReceivedFromReturnOnInvestments]=FLOAT;}
	public static final int idx_NetCashReceivedFromDisposalOfFixedAssetsIntangibleAssetsAndOtherLongTermAssets=189;//float '处置固定资产,无形资产和其他长期资产而收回的现金',
	{TCBI_TYPES[idx_NetCashReceivedFromDisposalOfFixedAssetsIntangibleAssetsAndOtherLongTermAssets]=FLOAT;}
	public static final int idx_IncludingCashReceivedFromSalesOfSubsidiaries=190;//float '收回投资所收到的现金中的出售子公司收到的现金',
	{TCBI_TYPES[idx_IncludingCashReceivedFromSalesOfSubsidiaries]=FLOAT;}
	public static final int idx_ItemsOfOtherReceivedCashRelevantToInvestingActivities=191;//float '收到的其他与投资活动有关的现金',
	{TCBI_TYPES[idx_ItemsOfOtherReceivedCashRelevantToInvestingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashInflowsFromInvestingActivities=192;//float '投资活动现金流入小计',
	{TCBI_TYPES[idx_SubTotalOfCashInflowsFromInvestingActivities]=FLOAT;}
	public static final int idx_CashPaidToAcquireFixedAssetsIntangibleAssetsAndOtherLongTermAssets=193;//float '购建固定资产、无形资产和其他长期资产所支付的现金',
	{TCBI_TYPES[idx_CashPaidToAcquireFixedAssetsIntangibleAssetsAndOtherLongTermAssets]=FLOAT;}
	public static final int idx_CashPaidToInvestments=194;//float '投资所支付的现金',
	{TCBI_TYPES[idx_CashPaidToInvestments]=FLOAT;}
	public static final int idx_NetIncreaseOfPledgeLoans=195;//float '质押贷款净增加额',
	{TCBI_TYPES[idx_NetIncreaseOfPledgeLoans]=FLOAT;}
	public static final int idx_NetCashReceivedFromPaymentOfSubsidiariesAndOtherBusinessUnits=196;//float '取得子公司及其他营业单位支付的现金净额',
	{TCBI_TYPES[idx_NetCashReceivedFromPaymentOfSubsidiariesAndOtherBusinessUnits]=FLOAT;}
	public static final int idx_OtherCashPaidRelatingToInvestingActivities=197;//float '支付的其他与投资活动有关的现金',
	{TCBI_TYPES[idx_OtherCashPaidRelatingToInvestingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashOutflowsFromInvestingActivities=198;//float '投资活动现金流出小计',
	{TCBI_TYPES[idx_SubTotalOfCashOutflowsFromInvestingActivities]=FLOAT;}
	public static final int idx_NetCashFlowsFromInvestingActivities=199;//float '投资活动产生的现金流量净额',
	{TCBI_TYPES[idx_NetCashFlowsFromInvestingActivities]=FLOAT;}
	public static final int idx_ProceedsFromIssuingStocksAndBonds=200;//float '吸收投资所收到的现金',
	{TCBI_TYPES[idx_ProceedsFromIssuingStocksAndBonds]=FLOAT;}
	public static final int idx_CashReceivedFromSubsidiaryCompanyAbsorbedInvestmentOfMinorityShareholdersEquity=201;//float '吸收投资所收到的现金中的子公司吸收少数股东权益性投资收到的现金',
	{TCBI_TYPES[idx_CashReceivedFromSubsidiaryCompanyAbsorbedInvestmentOfMinorityShareholdersEquity]=FLOAT;}
	public static final int idx_ProceedsFromBorrowings=202;//float '借款所收到的现金',
	{TCBI_TYPES[idx_ProceedsFromBorrowings]=FLOAT;}
	public static final int idx_CashReceivedFromIssuingBonds=203;//float '发行债券所收到的现金',
	{TCBI_TYPES[idx_CashReceivedFromIssuingBonds]=FLOAT;}
	public static final int idx_ProceedsReceivedFromOtherFinancingActivities=204;//float '收到其他与筹资活动有关的现金',
	{TCBI_TYPES[idx_ProceedsReceivedFromOtherFinancingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashInflowsFromFinancingActivities=205;//float '筹资活动现金流入小计',
	{TCBI_TYPES[idx_SubTotalOfCashInflowsFromFinancingActivities]=FLOAT;}
	public static final int idx_RepaymentOfBorrowings=206;//float '偿还债务所支付的现金',
	{TCBI_TYPES[idx_RepaymentOfBorrowings]=FLOAT;}
	public static final int idx_CashPaidForDistributingDividendsAndProfitsOrPayingInterests=207;//float '分配股利利润或偿付利息所支付的现金',
	{TCBI_TYPES[idx_CashPaidForDistributingDividendsAndProfitsOrPayingInterests]=FLOAT;}
	public static final int idx_DividendsOfMinorityShareholdersPaidBySubsidiaryCompany=208;//float '分配股利利润或偿付利息所支付的现金中的支付少数股东的股利',
	{TCBI_TYPES[idx_DividendsOfMinorityShareholdersPaidBySubsidiaryCompany]=FLOAT;}
	public static final int idx_OtherPaidCashRelevantToFinancingActivities=209;//float '支付的其他与筹资活动有关的现金',
	{TCBI_TYPES[idx_OtherPaidCashRelevantToFinancingActivities]=FLOAT;}
	public static final int idx_SubTotalOfCashOutflowsFromFinancingActivities=210;//float '筹资活动现金流出小计',
	{TCBI_TYPES[idx_SubTotalOfCashOutflowsFromFinancingActivities]=FLOAT;}
	public static final int idx_NetCashFlowsFromFinancingActivities=211;//float '筹资活动产生的现金流量净额',
	{TCBI_TYPES[idx_NetCashFlowsFromFinancingActivities]=FLOAT;}
	public static final int idx_InfluenceOfFluctuationInExchangeRateOnCash=212;//float '汇率变动对现金的影响',
	{TCBI_TYPES[idx_InfluenceOfFluctuationInExchangeRateOnCash]=FLOAT;}
	public static final int idx_NetIncreaseInCashAndCashEquivalents=213;//float '现金及现金等价物净增加额',
	{TCBI_TYPES[idx_NetIncreaseInCashAndCashEquivalents]=FLOAT;}
	public static final int idx_CashAndCashEquivalents=214;//float '现金及现金等价物余额'
	{TCBI_TYPES[idx_CashAndCashEquivalents]=FLOAT;}
}
