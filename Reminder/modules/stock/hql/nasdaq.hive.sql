--NasdaqIPO
drop table if exists NasdaqIPO;
create external table NasdaqIPO(name String, stockid String, marketid String, ipoprice decimal(10,2), shares decimal(20,2), offerAmount decimal(20, 2), dt Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-ipo';

--NasdaqFrQuarterBalanceSheet
drop table if exists NasdaqFrQuarterBalanceSheet;
create external table NasdaqFrQuarterBalanceSheet(stockid String, quarter String, dt Date, cash decimal(20,2), shortTermInvestment decimal(20,2), NetReceivables decimal(20,2), OtherCurrentAssets decimal(20,2), totalCurrentAssets decimal(20,2), longTermInvestment decimal(20,2), fixedAssets decimal(20,2), goodwill decimal(20,2), intangibleAssets decimal(20,2), otherAssets decimal(20,2), deferredAssetChanges decimal(20,2), totalAssets decimal(20,2), accountsPayable decimal(20,2), shortTermDebt decimal(20,2), otherCurrentLiabilities decimal(20,2), totalCurrentLiabilities decimal(20,2), longTermDebt decimal(20,2), otherLiabilities decimal(20,2), deferredLiabilityCharges decimal(20,2), miscStocks decimal(20,2), minorityInterest decimal(20,2), totalLiabilities decimal(20,2), commonStock decimal(20,2), capitalSurplus decimal(20,2), retainedEarnings decimal(20,2), treasuryStock decimal(20,2), otherEquity decimal(20,2), totalEquity decimal(20,2), totalLiabilityEquity decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-fr-quarter-BalanceSheet';

--NasdaqFrQuarterCashFlow
drop table if exists NasdaqFrQuarterCashFlow;
create external table NasdaqFrQuarterCashFlow(stockid String, quarter String, dt Date, NetIncome decimal(20,2), depreciation decimal(20,2), netIncomeAdjustments decimal(20,2), accountsReceibles decimal(20,2), changesInventory decimal(20,2), otherOperatingActivities decimal(20,2), liabilities decimal(20,2), netCashFlowsOperating decimal(20,2), capitalExpenditures decimal(20,2), investments decimal(20,2), otherInvestingActivities decimal(20,2), netCashFlowsInvesting decimal(20,2), saleAndPurchaseOfStock decimal(20,2), netBorrowings decimal(20,2), otherFinancialActivities decimal(20,2), netCashFlowsFinancing decimal(20,2), effectOfExchangeRate decimal(20,2), netCashFlow decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-fr-quarter-CashFlow';

--nasdaq-fr-quarter-IncomeStatement
drop table if exists NasdaqFrQuarterIncomeStatement;
create external table NasdaqFrQuarterIncomeStatement(stockid String, quarter String, dt Date, totalRevenue decimal(20,2), costOfRevenue decimal(20,2), grossProfit decimal(20,2), randd decimal(20,2), salesGeneralAdmin decimal(20,2), nonRecurringItems decimal(20,2), otherOperationgItems decimal(20,2), OperatingIncome decimal(20,2), AdditionalIncomeExpenseItems decimal(20,2), earningBeforeInterestTax decimal(20,2), interestExpense decimal(20,2), earningBeforeTax decimal(20,2), incomeTax decimal(20,2), minorityInterest decimal(20,2), equityEarningLossUnconsolidatedSubsidary decimal(20,2), netIncomeContOperations decimal(20,2), netIncome decimal(20,2), netIncomeToShareHolder decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-fr-quarter-IncomeStatement';

--nasdaq-fr-quarter-revenue
drop table if exists NasdaqFrQuarterRevenue;
create external table NasdaqFrQuarterRevenue(stockid String, quarterend Date, revenue decimal(20,2), eps decimal(10,3), dividends decimal(10,3), dt Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-fr-quarter-revenue';

drop table if exists NasdaqEarnAnnounce;
create external table NasdaqEarnAnnounce(announceTime varchar(50), stockid varchar(50), dt Date, fiscalQuarter varchar(20), consensusEps decimal(10,4), numberEstimate decimal(10, 0), eps decimal(10,4)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-earn-announce';

drop table if exists NasdaqEarnAnnounceTime;
create external table NasdaqEarnAnnounceTime(dt Date, stockid varchar(50), announceTime varchar(10)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-earn-announce-time';


--nasdaq-issue-dividend-history
drop table if exists NasdaqDividend;
create external table NasdaqDividend(stockid String, EffDate Date, Type String, CashAmount decimal(20,4), dt Date, RecordDate Date, PaymentDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-issue-dividend';

drop table if exists NasdaqExDivSplit;
create external table NasdaqExDivSplit(stockid String, dt Date, info String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-issue-xds-history';

drop table if exists NasdaqSplit;
create external table NasdaqSplit(stockid String, ratio String, paydt Date, exdt Date, dt Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-issue-split';

--nasdaq-quote-historical
drop table if exists NasdaqFqHistory;
create external table NasdaqFqHistory(stockid String, dt Date, Open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint, adjClose decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-fq-historical';


drop table if exists NasdaqHistory;
create external table NasdaqHistory(stockid String, dt Date, Open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-historical';

--nasdaq-quote-short-interest
drop table if exists NasdaqShortInterest;
create external table NasdaqShortInterest(stockid String, dt Date, ShortInterest bigint, AvgDailyShareVolume bigint, daysToCover decimal(20,10)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-short-interest';

--nasdaq-quote-premarket
drop table if exists NasdaqPremarket;
create external table NasdaqPremarket(stockid String, ttime Timestamp, price decimal(20,2), volume bigint) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-premarket';

--nasdaq-quote-afterhours
drop table if exists NasdaqAfterhours;
create external table NasdaqAfterhours(stockid String, ttime Timestamp, price decimal(20,2), volume bigint) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-afterhours';

--nasdaq-quote-tick
drop table if exists NasdaqTick;
create external table NasdaqTick(stockid String, ttime Timestamp, price decimal(20,2), volume bigint) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-quote-tick';

--nasdaq-holding-insiders
drop table if exists NasdaqHoldingInsiders;
create external table NasdaqHoldingInsiders(stockid String, insider String, relation String, dt Date, transactionType String, ownerType String, sharesTraded decimal(20,2), lastPrice decimal(20,2), sharesHeld decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-holding-insiders';

--nasdaq-holding-institutional
drop table if exists NasdaqHoldingInstitutional;
create external table NasdaqHoldingInstitutional(stockid String, institution String, dt Date, shareHeld bigint, shareChanges bigint, changePercent decimal(6,3), valueHeld bigint) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/nasdaq-holding-institutional';

--nasdaq-holding-summary
drop table if exists NasdaqHoldingSummary;

--nasdaq-holding-top5
drop table if exists NasdaqHoldingTop5;