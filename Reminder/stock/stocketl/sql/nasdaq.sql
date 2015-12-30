drop table if exists NasdaqIds;
create table NasdaqIds(name varchar(150), stockid varchar(150), marketCap decimal(20,2), country varchar(30), ipoYear decimal(6,0), subsector varchar(40), primary key (stockid));

#NasdaqIPO
drop table if exists NasdaqIPO;
create table NasdaqIPO(name varchar(150), stockid varchar(150), marketid varchar(150), ipoprice decimal(10,2), shares decimal(20,2), offerAmount decimal(20, 2), dt Date, primary key (stockid));

drop table if exists NasdaqEarnAnnounce;
create table NasdaqEarnAnnounce(announceTime varchar(50), stockid varchar(50), dt Date, fiscalQuarter varchar(20), consensusEps decimal(10,4), numberEstimate decimal(10, 0), eps decimal(10,4), primary key (stockid, dt));

drop table if exists NasdaqEarnAnnounceTime;
create table NasdaqEarnAnnounceTime(dt Date, stockid varchar(50), announceTime varchar(50), primary key (stockid, dt));

#NasdaqFrQuarterBalanceSheet
drop table if exists NasdaqFrQuarterBalanceSheet;
create table NasdaqFrQuarterBalanceSheet(stockid varchar(150), quarter varchar(150), dt Date, cash decimal(20,2), shortTermInvestment decimal(20,2), NetReceivables decimal(20,2), OtherCurrentAssets decimal(20,2), totalCurrentAssets decimal(20,2), longTermInvestment decimal(20,2), fixedAssets decimal(20,2), goodwill decimal(20,2), intangibleAssets decimal(20,2), otherAssets decimal(20,2), deferredAssetChanges decimal(20,2), totalAssets decimal(20,2), accountsPayable decimal(20,2), shortTermDebt decimal(20,2), otherCurrentLiabilities decimal(20,2), totalCurrentLiabilities decimal(20,2), longTermDebt decimal(20,2), otherLiabilities decimal(20,2), deferredLiabilityCharges decimal(20,2), miscStocks decimal(20,2), minorityInterest decimal(20,2), totalLiabilities decimal(20,2), commonStock decimal(20,2), capitalSurplus decimal(20,2), retainedEarnings decimal(20,2), treasuryStock decimal(20,2), otherEquity decimal(20,2), totalEquity decimal(20,2), totalLiabilityEquity decimal(20,2), primary key (stockid, dt));

#NasdaqFrQuarterCashFlow
drop table if exists NasdaqFrQuarterCashFlow;
create table NasdaqFrQuarterCashFlow(stockid varchar(150), quarter varchar(150), dt Date, NetIncome decimal(20,2), depreciation decimal(20,2), netIncomeAdjustments decimal(20,2), accountsReceibles decimal(20,2), changesInventory decimal(20,2), otherOperatingActivities decimal(20,2), liabilities decimal(20,2), netCashFlowsOperating decimal(20,2), capitalExpenditures decimal(20,2), investments decimal(20,2), otherInvestingActivities decimal(20,2), netCashFlowsInvesting decimal(20,2), saleAndPurchaseOfStock decimal(20,2), netBorrowings decimal(20,2), otherFinancialActivities decimal(20,2), netCashFlowsFinancing decimal(20,2), effectOfExchangeRate decimal(20,2), netCashFlow decimal(20,2), primary key (stockid, dt));

#nasdaq-fr-quarter-IncomeStatement
drop table if exists NasdaqFrQuarterIncomeStatement;
create table NasdaqFrQuarterIncomeStatement(stockid varchar(150), quarter varchar(150), dt Date, totalRevenue decimal(20,2), costOfRevenue decimal(20,2), grossProfit decimal(20,2), randd decimal(20,2), salesGeneralAdmin decimal(20,2), nonRecurringItems decimal(20,2), otherOperationgItems decimal(20,2), OperatingIncome decimal(20,2), AdditionalIncomeExpenseItems decimal(20,2), earningBeforeInterestTax decimal(20,2), interestExpense decimal(20,2), earningBeforeTax decimal(20,2), incomeTax decimal(20,2), minorityInterest decimal(20,2), equityEarningLossUnconsolidatedSubsidary decimal(20,2), netIncomeContOperations decimal(20,2), netIncome decimal(20,2), netIncomeToShareHolder decimal(20,2), primary key (stockid, dt));

#nasdaq-fr-quarter-revenue
drop table if exists NasdaqFrQuarterRevenue;
create table NasdaqFrQuarterRevenue(stockid varchar(150), quarterend Date, revenue decimal(20,2), eps decimal(10,3), dividends decimal(10,3), dt Date, primary key (stockid, dt));

#nasdaq-issue-dividend
drop table if exists NasdaqDividend;
create table NasdaqDividend(stockid varchar(150), EffDate Date, Type varchar(150), CashAmount decimal(20,4), dt Date, RecordDate Date, PaymentDate Date, primary key (stockid, dt));

drop table if exists NasdaqExDivSplit;
create table NasdaqExDivSplit(stockid varchar(150), dt Date, info varchar(150), primary key (stockid, dt));

drop table if exists NasdaqSplit;
create table NasdaqSplit(stockid varchar(150), ratio varchar(50), paydate Date, exdt Date, dt Date, primary key (stockid, exdt));

drop table if exists NasdaqUpcomingDividend;
create table NasdaqUpcomingDividend(stockid varchar(150), exdt Date, dividend decimal(10,4), annualDividend decimal(10,4), recordDt Date, announceDt Date, payDt Date, primary key (stockid, exdt));

#nasdaq-fq-quote-historical
drop table if exists NasdaqFqHistory;
create table NasdaqFqHistory(stockid varchar(150), dt Date, open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint, adjClose decimal(20,2), primary key (stockid, dt));
create index Idx_NasdaqFqHistory_Date on NasdaqFqHistory (dt);
create index Idx_NasdaqFqHistory_stockid on NasdaqFqHistory (stockid);
create index Idx_NasdaqFqHistory_adjClose on NasdaqFqHistory (adjClose);

#nasdaq-quote-historical
drop table if exists NasdaqHistory;
create table NasdaqHistory(stockid varchar(150), dt Date, open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint, primary key (stockid, dt));

#nasdaq-quote-short-interest
drop table if exists NasdaqShortInterest;
create table NasdaqShortInterest(stockid varchar(150), dt Date, ShortInterest bigint, AvgDailyShareVolume bigint, daysToCover decimal(20,10), primary key (stockid, dt));

#nasdaq-holding-insiders
drop table if exists NasdaqHoldingInsiders;
create table NasdaqHoldingInsiders(stockid varchar(150), insider varchar(150), relation varchar(150), dt Date, transactionType varchar(150), ownerType varchar(150), sharesTraded decimal(20,2), lastPrice decimal(20,2), sharesHeld decimal(20,2));

#nasdaq-holding-institutional
drop table if exists NasdaqHoldingInstitutional;
create table NasdaqHoldingInstitutional(stockid varchar(150), institution varchar(150), dt Date, shareHeld bigint, shareChanges bigint, changePercent decimal(6,3), valueHeld bigint);

#nasdaq-holding-summary
drop table if exists NasdaqHoldingSummary;

#nasdaq-holding-top5
drop table if exists NasdaqHoldingTop5;