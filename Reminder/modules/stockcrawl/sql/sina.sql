drop table if exists SinaStockIds;
create table SinaStockIds(stockid varchar(150), primary key (stockid));

#corp
drop table if exists SinaCorpInfo;
create table SinaCorpInfo(stockid varchar(150),name varchar(150),EnglishName varchar(150), IPOMarket varchar(150), IPODate Date, IPOPrice decimal(10,2), leadUnderwriter varchar(150), foundDate Date, RegisteredCapital varchar(150), InstitutionType varchar(150), OrgType varchar(150), BoardSecretary varchar(150), CompanyPhone varchar(150), BoardSecretaryPhone varchar(150), CompanyFax varchar(150), BoardSecretaryFax varchar(150), CompanyEmail varchar(150), BoardSecretaryEmail varchar(150), CompanyWebsite varchar(150), zipcode varchar(150), InfoDisclosureWebsite varchar(150), NameHistory varchar(150), RegisteredAddress varchar(150), OfficeAddress varchar(150), CompanyInfo varchar(150), BusinessScope varchar(150), primary key (stockid));

drop table if exists SinaCorpIPO;
create table SinaCorpIPO(stockid varchar(20), compName varchar(150), price decimal(10,2), totalRaise decimal(20,2), volume decimal(20,2), dt Date, underwriter varchar(100), pubDate Date);

drop table if exists SinaCorpManager;
create table SinaCorpManager(stockid varchar(150), name varchar(150), title varchar(150), dt Date, endDate Date);

drop table if exists SinaCorpRelatedSecurities;
create table SinaCorpRelatedSecurities( stockid varchar(150), securityId varchar(150), securityName varchar(150));

drop table if exists SinaCorpRelatedIndices;
create table SinaCorpRelatedIndices( stockid varchar(150), indexId varchar(150), indexName varchar(150), startDate Date, endDate Date);

drop table if exists SinaCorpRelatedXis;
create table SinaCorpRelatedXis( stockid varchar(150), xiName varchar(150), comment varchar(150));

drop table if exists SinaCorpRelatedIndustries;
create table SinaCorpRelatedIndustries( stockid varchar(150), industryName varchar(150), comment varchar(150));

drop table if exists SinaCorpRelatedConcepts;
create table SinaCorpRelatedConcepts( stockid varchar(150), conceptName varchar(150), comment varchar(150));

drop table if exists SinaCorpBulletin;
create table SinaCorpBulletin( stockid varchar(150), dt Date, title varchar(150));
create index Idx_SinaCorpBulletin_StockId_Date on SinaCorpBulletin (stockid, dt);

#fr
drop table if exists SinaFrAchieveNotice;
create table SinaFrAchieveNotice(stockid varchar(150), dt Date, reportPeriod Date, aType varchar(150), summary varchar(150), content varchar(150), earningSamePeriodlastYear decimal(10,4), cp decimal(5,2), primary key (stockid, dt));

drop table if exists SinaFrAssetDevalue;
create table SinaFrAssetDevalue(stockid varchar(150), dt Date, badDebitReserve decimal(20,2), AmongWhichAccountsReceivable decimal(20,2), OtherReceivable decimal(20,2), ImpairmentCurrentInvest decimal(20,2), AmongWhichStockInvest decimal(20,2), AmongWhichBondsInvest decimal(20,2), ImpairmentCurrentInventory decimal(20,2), AmongWhichInventoryProduct decimal(20,2), AmongWhichRawMaterial decimal(20,2), AmongWhichMaterialAndSupply decimal(20,2), AmangWhichLowValueConsumptionGoods decimal(20,2), ImpairmentAvailableForSellFinancialAsset decimal(20,2), ImpairmentLongTermInvest decimal(20,2), AmongWhichLongTermStockInvest decimal(20,2), AmongWhichHeldToMaturityInvest decimal(20,2), ImpairmentRealEstateInvest decimal(20,2), FixedAssetDepreciation decimal(20,2), AmongWhichBuildingAndConstruction decimal(20,2), AmongWhichMachineEquipment decimal(20,2), ImpairmentEngineeringMaterial decimal(20,2), ImpairementConstructionInProcess decimal(20,2), ImpairmentProductiveBioAsset decimal(20,2), AmongWhichMatureProductiveBioAsset decimal(20,2), ImpairementOilAndGas decimal(20,2), ImpairmentIntangibleAsset decimal(20,2), AmangWhichPatentRight decimal(20,2), AmongWhichTradeMarkRight decimal(20,2), ImpairmentGoodWill decimal(20,2), ImpairementEntrustedLoan decimal(20,2), AmongWhichShortTerm decimal(20,2), AmongWhichLongTerm decimal(20,2), Others decimal(20,2), total decimal(20,2));

drop table if exists SinaFrFootNoteAccount;
create table SinaFrFootNoteAccount(stockid varchar(150), dt Date, debtor varchar(150), AmountOwed decimal(20,2), OwedTime varchar(150), OwedReason varchar(150), Unit varchar(150));

drop table if exists SinaFrFootNoteInventory;
create table SinaFrFootNoteInventory(stockid varchar(150), dt Date, inventoryItem varchar(150), Amount decimal(20,2), DevalueReserve decimal(20,2), Unit varchar(150));

drop table if exists SinaFrFootNoteRecievableAging;
create table SinaFrFootNoteRecievableAging(stockid varchar(150), dt Date, Age varchar(150), Recievable decimal(20,2), BadAccountReserve decimal(20,2), BadAccountRatio decimal(10,5), Unit varchar(150));

drop table if exists SinaFrFootNoteTax;
create table SinaFrFootNoteTax(stockid varchar(150), dt Date, taxItem varchar(150), planTaxRate decimal(20,2), actualTaxRate decimal(20,2), comment varchar(150));

drop table if exists SinaFrFootNoteIncomeIndustry;
create table SinaFrFootNoteIncomeIndustry(stockid varchar(150), dt Date, mainOpItem varchar(150), mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2));

drop table if exists SinaFrFootNoteIncomeProduct;
create table SinaFrFootNoteIncomeProduct(stockid varchar(150), dt Date, mainOpItem varchar(150), mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2));

drop table if exists SinaFrFootNoteIncomeRegion;
create table SinaFrFootNoteIncomeRegion(stockid varchar(150), dt Date, mainOpItem varchar(150), mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2));

drop table if exists SinaFrGuideline;
create table SinaFrGuideline(stockid varchar(150), dt Date, DilutedEPS decimal(10,4), WeightedEPS decimal(10,4), AdjustedEPS decimal(10,4), ExcludeExtraEPS decimal(10,4), NAPS decimal(10,4), AdjustedNAPS decimal(10,4), OpCashFlowPS decimal(10,4), CaptialReservePS decimal(10,4), RetainedEarningPS decimal(10,4), AdjustedNAPS_dup decimal(10,4), TotalAssetProfitRate decimal(10,4), MainOpProfitRate decimal(10,4), TotalAssetNetProfitRate decimal(10,4), CostProfitRate decimal(10,4), OpProfitRate decimal(10,4), MainOpCostRation decimal(10,4), NetProfitMarginOnSales decimal(10,4), ReturnOnEquityRate decimal(10,4), ReturnOnNetWorthRate decimal(10,4), ReturnOnAssetsRate decimal(10,4), SalesGrossMargin decimal(10,4), TriExpensesRatio decimal(10,4), NonMainOpRatio decimal(10,4), MainProfitRatio decimal(10,4), DividentPayoutRatio decimal(10,4), ROI decimal(10,4), MainProfit decimal(20,2), ROE decimal(10,4), WeightedROE decimal(10,4), NetProfitExclExtraItem decimal(20,2), MainBusiRevenueIncreaseRate decimal (10,4), NetProfitGrowthRate decimal(10,4), NetAssetGrowthRate decimal(10,4), TotalAssetGrowthRate decimal(10,4), AccountsReceivableTurnoverRate decimal(10,4), AccountsReceivableTurnoverDays decimal(10,4), InventoryTurnoverRate decimal(10,4), InventoryTurnoverDays decimal(10,4), FixedAssetTurnoverRate decimal(10,4), TotalAssetTurnoverRate decimal(10,4), TotalAssetTurnoverDays decimal(10,4), CurrentAssetTurnoverRate decimal(10,4), CurrentAssetTurnoverDays decimal(10,4), StockHolderEquityTurnoverRate decimal(10,4), CurrentRatio decimal(10,4), AcidTestRatio decimal(10,4), CashRatio decimal(10,4), InterestCoverRatio decimal(10,4), LiabilityToWorkingCapitalRatio decimal(10,4), LiabilityToStockHoldersEquityRatio decimal(10,4), LongTermDebtRatio decimal(10,4), StockHolderEquityToFixedAssetRatio decimal(10,4), LiabilityToOwnerEquity decimal(10,4), LongTermAssetToLongTermCashRatio decimal(10,4), CapitalizationRate decimal(10,4), NetFixedAssetRatio decimal(10,4), CapitalImmobilizedRatio decimal(10,4), DebtEquityRatio decimal(10,4), LiquidationRatio decimal(10,4), FixedAssetRatio decimal(10,4), AssetLiabilityRatio decimal(10,4), TotalAsset decimal(20,2), CashFlowToSalesIncome decimal(10,4), ReturnOnAssetAndCashFlow decimal(10,4), CashFlowToNetProfit decimal(10,4), CashFlowToAliability decimal(10,4), CashFlowRatio decimal(10,4), ShortTermStockInvest decimal(20,2), ShortTermBondsInvest decimal(20,2), ShortTermOtherInvest decimal(20,2), LongTermStockInvest decimal(20,2), LongTermBondsInvest decimal(20,2), LongTermOtherInvest decimal(20,2), AccountsReceivableWithinOneYear decimal(20,2), AccountsReceivableOneToTwoYear decimal(20,2), AccountsReceivableTwoToThreeYear decimal(20,2), AccountsReceivableWithinThreeYear decimal(20,2), PaymentInAdvanceWithinOneYear decimal(20,2), PaymentInAdvanceOneToTwoYear decimal(20,2), PaymentInAdvanceTwoToThreeYear decimal(20,2), PaymentInAdvanceWithinThreeYear decimal(20,2), OtherReceivableWithinOneYear decimal(20,2), OtherReceivableOneToTwoYear decimal(20,2), OtherReceivableTwoToThreeYear decimal(20,2), OtherReceivableWithinThreeYear decimal(20,2));

drop table if exists SinaFrBalanceSheet;
create table SinaFrBalanceSheet(stockid varchar(150), dt Date, MonetaryFunds decimal(20,2), MonetarySub1 decimal(20,2), DepositCentralBank decimal(20,2), TransactionSettlementFunds decimal(20,2), PreciousMetal decimal(20,2), DepositInOtherBanks decimal(20,2), LoansToOtherBank decimal(20,2), LoansToOtherBank1 decimal(20,2), LoansToFinancialInstitutes decimal(20,2), DepositsInCorrespondentBanks decimal(20,2), StartingFundDepositNotes decimal(20,2), DerivativeAssets decimal(20,2), TradingFinancialAssets decimal(20,2), BuyingBackTheSaleOfFinancialAssets decimal(20,2), DiscountOfBill decimal(20,2), ImportExportBillAdvance decimal(20,2), AccountsReceivable decimal(20,2), AccountsInAdvance decimal(20,2), InterestReceivable decimal(20,2), DisbursementOfAdvancesAndLoans decimal(20,2), AllowanceForLoanLosses decimal(20,2), AgencyAssets decimal(20,2), AvailableForSaleFinancialAssets decimal(20,2), HeldToMaturityInvestment decimal(20,2), OtherReceivable decimal(20,2), LongTermAccountsReceivable decimal(20,2), LongTermEquityInvestment decimal(20,2), InvestmentSubsidiary decimal(20,2), InvestmentRealEstates decimal(20,2), InvestmentReceivable decimal(20,2), AmortizationExpense decimal(20,2), FixedAssetsNet decimal(20,2), ConstructionInProgress decimal(20,2), DisposalOfFixedAssets decimal(20,2), IntangibleAssets decimal(20,2), goodwill decimal(20,2), LongTermDeferredAndPrepaidExpenses decimal(20,2), PendingDebtAssets decimal(20,2), PDAImpairment decimal(20,2), PDANet decimal(20,2), TotalDeferredTaxesDebitItem decimal(20,2), OtherAssets decimal(20,2), BadAssets decimal(20,2), TotalAssets decimal(20,2), BorrowingsFromCentralBank decimal(20,2), MonetaryLiabilities decimal(20,2), ReceiptOfDepositsAndDepositsFromOtherBanks decimal(20,2), DueToPlacementsWithBanksAndOtherFinancialInstitutions decimal(20,2), LoansFromOtherBanks decimal(20,2), DepositsInCorrespondentBanks1 decimal(20,2), ForeignGovLoans decimal(20,2), DerivativeDebts decimal(20,2), TradingFinancialLiabilities decimal(20,2), FundsFromSellingOutAndRepurchasingFinancialAssets decimal(20,2), 	CustomerMoneyDeposits decimal(20,2), NotesFinancing decimal(20,2), DraftsAndTelegraphicTransfersPayable decimal(20,2), WithholdingExpenses decimal(20,2), 	IssueCertificateOfDeposit decimal(20,2), OutwardRemittance decimal(20,2), Remittance1 decimal(20,2), WelfarePayable decimal(20,2), TaxesPayable decimal(20,2), 	InterestsPayable decimal(20,2), AccountsPayable decimal(20,2), SpecialPayable decimal(20,2), DividentsPayable decimal(20,2), OtherPayable decimal(20,2), LiabilitiesVicariousBusiness decimal(20,2), EstimatedLiabilities decimal(20,2), DeferredProfits decimal(20,2), LongTermPayable decimal(20,2), BondsPayable	 decimal(20,2), SubordinatedBonds decimal(20,2), DeferredIncomeTaxLiabilities decimal(20,2), OtherLiabilities decimal(20,2), LiabilityTotal decimal(20,2), CapitalPaidIn decimal(20,2), CapitalReserve decimal(20,2), FairValueGainAndLoss decimal(20,2), HeldToMaturityInvestmentsNotCarryOverLoss decimal(20,2), StockTreasury decimal(20,2), ReserveSurplus decimal(20,2), ReserveOrdinaryRisk decimal(20,2), TrustCompensationReserve decimal(20,2), ProfitUndistributed decimal(20,2), ProposedCashDividends decimal(20,2), CapitalForeignCorrency decimal(20,2), OtherReserve decimal(20,2), EquityAttribToParentCompanyTotal decimal(20,2), InterestsMinority decimal(20,2), EquityTotalShareholders decimal(20,2), TotalLiabilityShareholderEquity decimal(20,2));

drop table if exists SinaFrCashFlow;
create table SinaFrCashFlow(stockid varchar(150), dt Date, NetReduceOfLoansAndAdvancesToCustomers decimal(20,2),NetIncreaseBorrowFromCentralBank decimal(20,2),NetIncreaseCustomerDepositsAndInterbankDeposits decimal(20,2),NetIncreaseCustomerDeposits decimal(20,2),NetIncreaseInterbankDeposits decimal(20,2),NetRecoverDueFromBanksAndOtherFinancialInstitutions decimal(20,2),BorrowedCapitalCashInflows decimal(20,2),NetRecoveredLoansToOtherBanks decimal(20,2),NetAbsorptionRepurchaseItemsSold decimal(20,2),NetRecoveredBuyingResaleItems decimal(20,2),NetIncreaseDisposalOfFinancialAssets decimal(20,2),CashFromInterestFeesAndCommissions decimal(20,2),CashFromInterest decimal(20,2),CashFromFees decimal(20,2),CashFromTransactionsWithFinancialInstitutions decimal(20,2),CashFromIntermediateBusiness decimal(20,2),CashFromExchanges decimal(20,2),CashFromOtherBusi decimal(20,2),CashFromMarginDeposit decimal(20,2),CashFromEntrustedFunds decimal(20,2),CashFromPreciousMetal decimal(20,2),CashFromDiscounts decimal(20,2),CashFromSalesOfGoodsOrServices decimal(20,2),CashFromTaxRefunded decimal(20,2),CashFromOtherLiabilities decimal(20,2),CashFromRecievingPreviouslyWrittenOffLoansAndRecievables decimal(20,2),CashFromOtherReceivablesPayableTempReduce decimal(20,2),CashFromDisposalOfRepossessedAssets decimal(20,2),CashFromOtherActivities decimal(20,2),CashFromOperActivities decimal(20,2),NetIncreaseLoansAndAdvancesToCustomers decimal(20,2),NetIncreasePaymentsCentralBanksAndInterbank decimal(20,2),NetIncreasePaymentsCentralBanks decimal(20,2),NetIncreasePaymentsInterBanks decimal(20,2),LoansToOtherBanks decimal(20,2),RepayCentralBankLoan decimal(20,2),DepositsPayment decimal(20,2),DecreaseSaveWithBanksAndOtherFinancialInstitutions decimal(20,2),RepayBorrowFromBanksAndOtherFinancialInstitutions decimal(20,2),NetRepurchaseReimbursementsToSell decimal(20,2),CashSpendOnSecuritiesInvestment decimal(20,2),NetPaymentToBuyResale decimal(20,2),NetDecreaseDisposalOfFinancialAssetsForSale decimal(20,2),CashPaymentOfInterestFeesAndCommissions decimal(20,2),CashPaymentOfInterest decimal(20,2),CashPaymentOfFees decimal(20,2),CashPaymentToAndForEmployee decimal(20,2),CashPaymentOfTax decimal(20,2),CashPaymentOfOperExpense decimal(20,2),CashPaymentToFinancialInstitutions decimal(20,2),CashPaymentToDiscounts decimal(20,2),WrittenOffBadLoansAndInterestRecovery decimal(20,2),ReducedCommissionAndAgencyBusiness decimal(20,2),CashOutflowOfPreciousMetal decimal(20,2),CashPayedForGoodsAndServices decimal(20,2),NetDecreaseInOtherBorrowedFunds decimal(20,2),CashPaymentForOtherDecreasedAssets decimal(20,2),CashPaymentForOtherReducedAccounts decimal(20,2),CashPaymentToOtherBusiRelated decimal(20,2),CashPaymentBusiSubTotal decimal(20,2),NetCashFlowOp decimal(20,2),CashRecievedFromInvestmentWithdraw decimal(20,2),CashRecievedFromInvestmentGain decimal(20,2),CashDividendsOrProfitShareReceived decimal(20,2),CashReceivedFromBondInterest decimal(20,2),CashFromDisposalOfAssets decimal(20,2),CashFromDisposalOfEquityInvestments decimal(20,2),CashFromSubsidiary decimal(20,2),CashFromOtherInvestment decimal(20,2),CashFromInvestmentSubtotal decimal(20,2),CashInvested decimal(20,2),CashForEquityIncreased decimal(20,2),CashForBonds decimal(20,2),CashForMergeAcquisition decimal(20,2),CashPaidInConstruction decimal(20,2),CashPaidForAssets decimal(20,2),CashPaidForSubsidiariesAcquired decimal(20,2),CashForOtherInvestRelatedActivities decimal(20,2),CashOutflowSubtotalForInvest decimal(20,2),NetCashFlowsFromInvest decimal(20,2),CashReceivedByAbsorbingInvestments decimal(20,2),CashAbsorbedFromIssueSecuritizedAssets decimal(20,2),CashReceivedFromIssuingBonds decimal(20,2),CashReceivedFromIssuanceSubordinatedBonds decimal(20,2),CashReceivedFromCapitalIncrease decimal(20,2),CashReceivedRelateToOtherFinancingActivities decimal(20,2),CashInflowsFromFinancingActivities decimal(20,2),CashPaidForDebt decimal(20,2),CashPaidForDividendsProfitsOrInterest decimal(20,2),CashPaidForInterest decimal(20,2),PaymentOfIPOExpenses decimal(20,2),CashPaidToOtherFinancingActivities decimal(20,2),CashOutflowsForFinancingActivitiesSubtotal decimal(20,2),NetCashFlowForFinancingActivities decimal(20,2),EffectOfExchangeRateChangesOnCashEqui decimal(20,2),CashEqui decimal(20,2),InitialCashEqui decimal(20,2),CurrentCashEqui decimal(20,2),NetProfit decimal(20,2),MinorityShareholderReturns decimal(20,2),ProvisionForImpairmentOfAssets decimal(20,2),ProvisionForBadDebts decimal(20,2),LoanLossProvisionsInPreparation decimal(20,2),ReversalOfImpairmentDueFromBanks decimal(20,2),DepreciationOfOilAndGasAssetsAndProductiveBiologicalAssets decimal(20,2),DepreciationOfInvestmentProperty decimal(20,2),AmortizationOfIntangibleDeferredAndOtherAssets decimal(20,2),AmortizationOfIntangibleAssets decimal(20,2),AmortizationOfLongTermExpenses decimal(20,2),AmortizationOfLongTermAssets decimal(20,2),DisposalOfFixedAssets20Gains decimal(20,2),LossGainOnDisposalOfInvestmentProperty decimal(20,2),LossesOnScrappingOfFixedAssets decimal(20,2),FinancialExpenses decimal(20,2),InvestmentLoss decimal(20,2),ChangesInFairValueOnForeignExchange decimal(20,2),ExchangeGainLoss decimal(20,2),DerivativeNetProfit decimal(20,2),DiscountedBackToPull decimal(20,2),DecreaseInInventories decimal(20,2),ReduceLoans decimal(20,2),IncreaseInDeposits decimal(20,2),NetLendingMoney decimal(20,2),ReducingFinancialAssets decimal(20,2),IncreaseExpectedLiabilities decimal(20,2),ReceivedWrittenOffPayments decimal(20,2),DecreaseInDeferredTaxAssets decimal(20,2),IncreaseInDeferredIncomeTaxLiabilities decimal(20,2),IncreaseInOperatingReceivables decimal(20,2),IncreaseInOperatingPayables decimal(20,2),DecreaseInOperatingOtherAssets decimal(20,2),OtherIncreasesInOperatingLiabilities decimal(20,2),OtherCosts decimal(20,2),NetCashFlowFromOperatingActivities decimal(20,2),FixedAssetsToRepayDebt decimal(20,2),InvestmentDebt decimal(20,2),FixedAssetsInvestment decimal(20,2),DebtIntoCapital decimal(20,2),ConvertibleBondDueInOneYearFixedAssetsLeased decimal(20,2),FinanceLeasedFixedAssets decimal(20,2),OtherFinancialActivitiesNotInvolvingCashReceiptsAndPayments decimal(20,2),PeriodEndCash decimal(20,2),PeriodBeginCash decimal(20,2),PeriodEndCashEqui decimal(20,2),PeriodBeginCashEqui decimal(20,2),NetIncreaseCashEqui decimal(20,2));

drop table if exists SinaFrProfitStatement;
create table SinaFrProfitStatement(stockid varchar(150), dt Date, BusinessIncome decimal(20,2), BusinessProfit decimal(20,2), TotalProfit decimal(20,2), BasicEPS decimal(20,2),DilutedEPS decimal(20,2), primary key (stockid, dt));
alter table SinaFrProfitStatement add column pubDt Date;
create index SinaFrProfitStatement_pubDt on SinaFrProfitStatement (pubDt);

#
drop table if exists SinaShareBonusDividend;
create table SinaShareBonusDividend(stockid varchar(150), dt Date, SongGu decimal(10,2), ZhuanZeng decimal(10,2), Devidend decimal(10,2), progress varchar(150), ExDate Date, RegDate Date, XStockPublicDate Date, comment varchar(150) , primary key (stockid, dt));

drop table if exists SinaShareBonusAlloted;
create table SinaShareBonusAlloted(stockid varchar(150), dt Date, AllotNumberEveryTen decimal(10,2), price decimal(10,4), base decimal(20,2), ExDate Date, RegDate Date, PayStartDate Timestamp, PayEndDate Date, XStockPublicDate Date, TotalAmount decimal(20,2), comment varchar(150), primary key (stockid, dt));

drop table if exists SinaAddStock;
create table SinaAddStock(stockid varchar(20), dt Date, addType varchar(40), price decimal(10,4), cost decimal(20,2), volume decimal(20,2), primary key (stockid, dt));

#market dzjy
drop table if exists SinaMarketDZJY;
create table SinaMarketDZJY(dt Date, stockid varchar(150), stockname varchar(150), price decimal(10,4), volume decimal(20,2), amount decimal(20,2), buyerAgent varchar(150), sellerAgent varchar(150), stockType varchar(150), primary key (stockid, dt));

#market fu quan
drop table if exists SinaMarketFQ;
create table SinaMarketFQ(stockid varchar(150), dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2), fqIdx decimal(10,4), primary key (stockid, dt));
create index Idx_SinaMarketFQ_Date on SinaMarketFQ (dt);
alter table SinaMarketFQ add column obsEps decimal(10,4);
create index Idx_SinaMarketFQ_ObsEps on SinaMarketFQ (obsEps);

#market history
drop table if exists SinaMarketDaily;
create table SinaMarketDaily(stockid varchar(150), dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2), primary key (stockid, dt));

#market rzrq - summary
drop table if exists SinaMarketRZRQSummary;
create table SinaMarketRZRQSummary(dt Date, market varchar(150), MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2), primary key (market, dt));

#market rzrq - detail
drop table if exists SinaMarketRZRQDetail;
create table SinaMarketRZRQDetail(dt Date, idx decimal(10,2), stockid varchar(150), stockname varchar(150), MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2), ShortSellBalanceStock decimal(20,2), ShortSellSellVolume decimal(20,2), ShortSellReturnVolume decimal(20,2), ShortSellLeftBalance decimal(20,2));

#
drop table if exists SinaStockTopHolderSummary;
create table SinaStockTopHolderSummary(stockid varchar(150), dt Date, publishDate Date, description varchar(150), stockHolderNumber decimal(10,2), avgStockHolder decimal(20,2));

drop table if exists SinaStockTopHolder;
create table SinaStockTopHolder(stockid varchar(150), dt Date, rank decimal(10,2), holderName varchar(150), volume decimal(20,2), percentage decimal(8,4), stockType varchar(150));

drop table if exists SinaStockTopHolderCirculate;
create table SinaStockTopHolderCirculate(stockid varchar(150), dt Date, publishDate Date, rank decimal(10,2), holderName varchar(150), volume decimal(20,2), percentage decimal(8,4), stockType varchar(150));

drop table if exists SinaStockFundHolder;
create table SinaStockFundHolder(stockid varchar(150), dt Date, fundName varchar(150), fundId varchar(150), volume decimal(20,2), circulatePercentage decimal(8,4), amount decimal(20,2), valuePercentage decimal(8,4));

drop table if exists SinaStockStructure;
create table SinaStockStructure(stockid varchar(150), dt Date, pubDate Date, pic varchar(150), reason varchar(150), total decimal(20,4), circulate decimal(20,4), circulateA decimal(20,4), managerShare decimal(20,4), limitA decimal(20,4), circulateB decimal(20,4), limitB decimal(20,4), circulateH decimal(20,4), stateShare decimal(20,4), stateLegalPersonShare decimal(20,4), nationalLegalPersonShare decimal(20,4), nationalFounderShare decimal(20,4), MJLegalShare decimal(20,4), generalLegalShare decimal(20,4), strategyInvestShare decimal(20,4), fundShare decimal(20,4), xdShare decimal(20,4), staffShare decimal(20,4), priorityShare decimal(20,4));

