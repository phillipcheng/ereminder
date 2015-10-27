set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--corp info
drop table if exists SinaCorpInfo;
create external table SinaCorpInfo(stockid String,name String,EnglishName String, IPOMarket String, IPODate Date, IPOPrice decimal(10,2), leadUnderwriter String, foundDate Date, RegisteredCapital String, InstitutionType String, OrgType String, BoardSecretary String, CompanyPhone String, BoardSecretaryPhone String, CompanyFax String, BoardSecretaryFax String, CompanyEmail String, BoardSecretaryEmail String, CompanyWebsite String, zipcode String, InfoDisclosureWebsite String, NameHistory String, RegisteredAddress String, OfficeAddress String, CompanyInfo String, BusinessScope String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-info';

drop table if existes SinaCorpIPO;
create external table SinaCorpIPO(compName String, shortName String, marketid String, stockid String, dt Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-ipo';

drop table if exists SinaCorpManager;
create external table SinaCorpManager( stockid String, name String, title String, dt Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-manager';

drop table if exists SinaCorpRelatedSecurities;
create external table SinaCorpRelatedSecurities( stockid String, securityId String, securityName String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/securities';

drop table if exists SinaCorpRelatedIndices;
create external table SinaCorpRelatedIndices( stockid String, indexId String, indexName String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/indices';

drop table if exists SinaCorpRelatedXis;
create external table SinaCorpRelatedXis( stockid String, xiName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/xis';

drop table if exists SinaCorpRelatedIndustries;
create external table SinaCorpRelatedIndustries( stockid String, industryName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/industries';

drop table if exists SinaCorpRelatedConcepts;
create external table SinaCorpRelatedConcepts( stockid String, conceptName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/concepts';

drop table if exists SinaCorpBulletin;
create external table SinaCorpBulletin( stockid String, dt Date, title String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-bulletin';

--fr
drop table if exists SinaFrAchieveNotice;
create external table SinaFrAchieveNotice(stockid String, dt Date, reportPeriod Date, aType String, summary String, content String, earningSamePeriodlastYear decimal(10,4), cp decimal(5,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-achievenotice';

--fr asset-devalue
drop table if exists SinaFrAssetDevalue;
create external table SinaFrAssetDevalue(stockid String, dt Date, badDebitReserve decimal(20,2), AmongWhichAccountsReceivable decimal(20,2), OtherReceivable decimal(20,2), ImpairmentCurrentInvest decimal(20,2), AmongWhichStockInvest decimal(20,2), AmongWhichBondsInvest decimal(20,2), ImpairmentCurrentInventory decimal(20,2), AmongWhichInventoryProduct decimal(20,2), AmongWhichRawMaterial decimal(20,2), AmongWhichMaterialAndSupply decimal(20,2), AmangWhichLowValueConsumptionGoods decimal(20,2), ImpairmentAvailableForSellFinancialAsset decimal(20,2), ImpairmentLongTermInvest decimal(20,2), AmongWhichLongTermStockInvest decimal(20,2), AmongWhichHeldToMaturityInvest decimal(20,2), ImpairmentRealEstateInvest decimal(20,2), FixedAssetDepreciation decimal(20,2), AmongWhichBuildingAndConstruction decimal(20,2), AmongWhichMachineEquipment decimal(20,2), ImpairmentEngineeringMaterial decimal(20,2), ImpairementConstructionInProcess decimal(20,2), ImpairmentProductiveBioAsset decimal(20,2), AmongWhichMatureProductiveBioAsset decimal(20,2), ImpairementOilAndGas decimal(20,2), ImpairmentIntangibleAsset decimal(20,2), AmangWhichPatentRight decimal(20,2), AmongWhichTradeMarkRight decimal(20,2), ImpairmentGoodWill decimal(20,2), ImpairementEntrustedLoan decimal(20,2), AmongWhichShortTerm decimal(20,2), AmongWhichLongTerm decimal(20,2), Others decimal(20,2), total decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-assetdevalue-year';

--fr footnote-account
drop table if exists SinaFrFootNoteAccount;
create external table SinaFrFootNoteAccount(stockid String, dt Date, debtor String, AmountOwed decimal(20,2), OwedTime String, OwedReason String, Unit String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/account/';

--fr footnote-inventory
drop table if exists SinaFrFootNoteInventory;
create external table SinaFrFootNoteInventory(stockid String, dt Date, inventoryItem String, Amount decimal(20,2), DevalueReserve decimal(20,2), Unit String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/inventory';

--fr footnote-recievableAging
drop table if exists SinaFrFootNoteRecievableAging;
create external table SinaFrFootNoteRecievableAging(stockid String, dt Date, Age String, Recievable decimal(20,2), BadAccountReserve decimal(20,2), BadAccountRatio decimal(10,5), Unit String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/recievableAging';

--fr footnote-tax
drop table if exists SinaFrFootNoteTax;
create external table SinaFrFootNoteTax(stockid String, dt Date, taxItem String, planTaxRate decimal(20,2), actualTaxRate decimal(20,2), comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/tax';

--fr footnote-income-industry
drop table if exists SinaFrFootNoteIncomeIndustry;
create external table SinaFrFootNoteIncomeIndustry(stockid String, dt Date, mainOpItem String, mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/incomeStructureByIndustry';

--fr footnote-income-product
drop table if exists SinaFrFootNoteIncomeProduct;
create external table SinaFrFootNoteIncomeProduct(stockid String, dt Date, mainOpItem String, mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/incomeStructureByIndustry';

--fr footnote-income-region
drop table if exists SinaFrFootNoteIncomeRegion;
create external table SinaFrFootNoteIncomeRegion(stockid String, dt Date, mainOpItem String, mainOpIncome decimal(20,2), mainOpIncomeRatio decimal(10,4), mainOpCost decimal(20,2), mainOpCostRatio decimal(10,4), mainOpRevenue decimal(20,2), mainOpRevenueRatio decimal(10,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-footnote/incomeStructureByRegion';

--fr footnote-guideline
drop table if exists SinaFrGuideline;
create external table SinaFrGuideline(stockid String, dt Date, DilutedEPS decimal(10,4), WeightedEPS decimal(10,4), AdjustedEPS decimal(10,4), ExcludeExtraEPS decimal(10,4), NAPS decimal(10,4), AdjustedNAPS decimal(10,4), OpCashFlowPS decimal(10,4), CaptialReservePS decimal(10,4), RetainedEarningPS decimal(10,4), AdjustedNAPS_dup decimal(10,4), TotalAssetProfitRate decimal(10,4), MainOpProfitRate decimal(10,4), TotalAssetNetProfitRate decimal(10,4), CostProfitRate decimal(10,4), OpProfitRate decimal(10,4), MainOpCostRation decimal(10,4), NetProfitMarginOnSales decimal(10,4), ReturnOnEquityRate decimal(10,4), ReturnOnNetWorthRate decimal(10,4), ReturnOnAssetsRate decimal(10,4), SalesGrossMargin decimal(10,4), TriExpensesRatio decimal(10,4), NonMainOpRatio decimal(10,4), MainProfitRatio decimal(10,4), DividentPayoutRatio decimal(10,4), ROI decimal(10,4), MainProfit decimal(20,2), ROE decimal(10,4), WeightedROE decimal(10,4), NetProfitExclExtraItem decimal(20,2), MainBusiRevenueIncreaseRate decimal (10,4), NetProfitGrowthRate decimal(10,4), NetAssetGrowthRate decimal(10,4), TotalAssetGrowthRate decimal(10,4), AccountsReceivableTurnoverRate decimal(10,4), AccountsReceivableTurnoverDays decimal(10,4), InventoryTurnoverRate decimal(10,4), InventoryTurnoverDays decimal(10,4), FixedAssetTurnoverRate decimal(10,4), TotalAssetTurnoverRate decimal(10,4), TotalAssetTurnoverDays decimal(10,4), CurrentAssetTurnoverRate decimal(10,4), CurrentAssetTurnoverDays decimal(10,4), StockHolderEquityTurnoverRate decimal(10,4), CurrentRatio decimal(10,4), AcidTestRatio decimal(10,4), CashRatio decimal(10,4), InterestCoverRatio decimal(10,4), LiabilityToWorkingCapitalRatio decimal(10,4), LiabilityToStockHoldersEquityRatio decimal(10,4), LongTermDebtRatio decimal(10,4), StockHolderEquityToFixedAssetRatio decimal(10,4), LiabilityToOwnerEquity decimal(10,4), LongTermAssetToLongTermCashRatio decimal(10,4), CapitalizationRate decimal(10,4), NetFixedAssetRatio decimal(10,4), CapitalImmobilizedRatio decimal(10,4), DebtEquityRatio decimal(10,4), LiquidationRatio decimal(10,4), FixedAssetRatio decimal(10,4), AssetLiabilityRatio decimal(10,4), TotalAsset decimal(20,2), CashFlowToSalesIncome decimal(10,4), ReturnOnAssetAndCashFlow decimal(10,4), CashFlowToNetProfit decimal(10,4), CashFlowToAliability decimal(10,4), CashFlowRatio decimal(10,4), ShortTermStockInvest decimal(20,2), ShortTermBondsInvest decimal(20,2), ShortTermOtherInvest decimal(20,2), LongTermStockInvest decimal(20,2), LongTermBondsInvest decimal(20,2), LongTermOtherInvest decimal(20,2), AccountsReceivableWithinOneYear decimal(20,2), AccountsReceivableOneToTwoYear decimal(20,2), AccountsReceivableTwoToThreeYear decimal(20,2), AccountsReceivableWithinThreeYear decimal(20,2), PaymentInAdvanceWithinOneYear decimal(20,2), PaymentInAdvanceOneToTwoYear decimal(20,2), PaymentInAdvanceTwoToThreeYear decimal(20,2), PaymentInAdvanceWithinThreeYear decimal(20,2), OtherReceivableWithinOneYear decimal(20,2), OtherReceivableOneToTwoYear decimal(20,2), OtherReceivableTwoToThreeYear decimal(20,2), OtherReceivableWithinThreeYear decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-fr-guideline-year';

-- fr balance-sheet 95 fields
drop table if exists SinaFrBalanceSheet;
create external table SinaFrBalanceSheet(stockid String, dt Date, MonetaryFunds decimal(20,2), MonetarySub1 decimal(20,2), DepositCentralBank decimal(20,2), TransactionSettlementFunds decimal(20,2), PreciousMetal decimal(20,2), DepositInOtherBanks decimal(20,2), LoansToOtherBank decimal(20,2), LoansToOtherBank1 decimal(20,2), LoansToFinancialInstitutes decimal(20,2), DepositsInCorrespondentBanks decimal(20,2), StartingFundDepositNotes decimal(20,2), DerivativeAssets decimal(20,2), TradingFinancialAssets decimal(20,2), BuyingBackTheSaleOfFinancialAssets decimal(20,2), DiscountOfBill decimal(20,2), ImportExportBillAdvance decimal(20,2), AccountsReceivable decimal(20,2), AccountsInAdvance decimal(20,2), InterestReceivable decimal(20,2), DisbursementOfAdvancesAndLoans decimal(20,2), AllowanceForLoanLosses decimal(20,2), AgencyAssets decimal(20,2), AvailableForSaleFinancialAssets decimal(20,2), HeldToMaturityInvestment decimal(20,2), OtherReceivable decimal(20,2), LongTermAccountsReceivable decimal(20,2), LongTermEquityInvestment decimal(20,2), InvestmentSubsidiary decimal(20,2), InvestmentRealEstates decimal(20,2), InvestmentReceivable decimal(20,2), AmortizationExpense decimal(20,2), FixedAssetsNet decimal(20,2), ConstructionInProgress decimal(20,2), DisposalOfFixedAssets decimal(20,2), IntangibleAssets decimal(20,2), goodwill decimal(20,2), LongTermDeferredAndPrepaidExpenses decimal(20,2), PendingDebtAssets decimal(20,2), PDAImpairment decimal(20,2), PDANet decimal(20,2), TotalDeferredTaxesDebitItem decimal(20,2), OtherAssets decimal(20,2), BadAssets decimal(20,2), TotalAssets decimal(20,2), BorrowingsFromCentralBank decimal(20,2), MonetaryLiabilities decimal(20,2), ReceiptOfDepositsAndDepositsFromOtherBanks decimal(20,2), DueToPlacementsWithBanksAndOtherFinancialInstitutions decimal(20,2), LoansFromOtherBanks decimal(20,2), DepositsInCorrespondentBanks1 decimal(20,2), ForeignGovLoans decimal(20,2), DerivativeDebts decimal(20,2), TradingFinancialLiabilities decimal(20,2), FundsFromSellingOutAndRepurchasingFinancialAssets decimal(20,2), 	CustomerMoneyDeposits decimal(20,2), NotesFinancing decimal(20,2), DraftsAndTelegraphicTransfersPayable decimal(20,2), WithholdingExpenses decimal(20,2), 	IssueCertificateOfDeposit decimal(20,2), OutwardRemittance decimal(20,2), Remittance1 decimal(20,2), WelfarePayable decimal(20,2), TaxesPayable decimal(20,2), 	InterestsPayable decimal(20,2), AccountsPayable decimal(20,2), SpecialPayable decimal(20,2), DividentsPayable decimal(20,2), OtherPayable decimal(20,2), LiabilitiesVicariousBusiness decimal(20,2), EstimatedLiabilities decimal(20,2), DeferredProfits decimal(20,2), LongTermPayable decimal(20,2), BondsPayable	 decimal(20,2), SubordinatedBonds decimal(20,2), DeferredIncomeTaxLiabilities decimal(20,2), OtherLiabilities decimal(20,2), LiabilityTotal decimal(20,2), CapitalPaidIn decimal(20,2), CapitalReserve decimal(20,2), FairValueGainAndLoss decimal(20,2), HeldToMaturityInvestmentsNotCarryOverLoss decimal(20,2), StockTreasury decimal(20,2), ReserveSurplus decimal(20,2), ReserveOrdinaryRisk decimal(20,2), TrustCompensationReserve decimal(20,2), ProfitUndistributed decimal(20,2), ProposedCashDividends decimal(20,2), CapitalForeignCorrency decimal(20,2), OtherReserve decimal(20,2), EquityAttribToParentCompanyTotal decimal(20,2), InterestsMinority decimal(20,2), EquityTotalShareholders decimal(20,2), TotalLiabilityShareholderEquity decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-fr-quarter-BalanceSheet';

--fr cash-flow
drop table if exists SinaFrCashFlow;
create external table SinaFrCashFlow(stockid String, dt Date, NetReduceOfLoansAndAdvancesToCustomers decimal(20,2),NetIncreaseBorrowFromCentralBank decimal(20,2),NetIncreaseCustomerDepositsAndInterbankDeposits decimal(20,2),NetIncreaseCustomerDeposits decimal(20,2),NetIncreaseInterbankDeposits decimal(20,2),NetRecoverDueFromBanksAndOtherFinancialInstitutions decimal(20,2),BorrowedCapitalCashInflows decimal(20,2),NetRecoveredLoansToOtherBanks decimal(20,2),NetAbsorptionRepurchaseItemsSold decimal(20,2),NetRecoveredBuyingResaleItems decimal(20,2),NetIncreaseDisposalOfFinancialAssets decimal(20,2),CashFromInterestFeesAndCommissions decimal(20,2),CashFromInterest decimal(20,2),CashFromFees decimal(20,2),CashFromTransactionsWithFinancialInstitutions decimal(20,2),CashFromIntermediateBusiness decimal(20,2),CashFromExchanges decimal(20,2),CashFromOtherBusi decimal(20,2),CashFromMarginDeposit decimal(20,2),CashFromEntrustedFunds decimal(20,2),CashFromPreciousMetal decimal(20,2),CashFromDiscounts decimal(20,2),CashFromSalesOfGoodsOrServices decimal(20,2),CashFromTaxRefunded decimal(20,2),CashFromOtherLiabilities decimal(20,2),CashFromRecievingPreviouslyWrittenOffLoansAndRecievables decimal(20,2),CashFromOtherReceivablesPayableTempReduce decimal(20,2),CashFromDisposalOfRepossessedAssets decimal(20,2),CashFromOtherActivities decimal(20,2),CashFromOperActivities decimal(20,2),NetIncreaseLoansAndAdvancesToCustomers decimal(20,2),NetIncreasePaymentsCentralBanksAndInterbank decimal(20,2),NetIncreasePaymentsCentralBanks decimal(20,2),NetIncreasePaymentsInterBanks decimal(20,2),LoansToOtherBanks decimal(20,2),RepayCentralBankLoan decimal(20,2),DepositsPayment decimal(20,2),DecreaseSaveWithBanksAndOtherFinancialInstitutions decimal(20,2),RepayBorrowFromBanksAndOtherFinancialInstitutions decimal(20,2),NetRepurchaseReimbursementsToSell decimal(20,2),CashSpendOnSecuritiesInvestment decimal(20,2),NetPaymentToBuyResale decimal(20,2),NetDecreaseDisposalOfFinancialAssetsForSale decimal(20,2),CashPaymentOfInterestFeesAndCommissions decimal(20,2),CashPaymentOfInterest decimal(20,2),CashPaymentOfFees decimal(20,2),CashPaymentToAndForEmployee decimal(20,2),CashPaymentOfTax decimal(20,2),CashPaymentOfOperExpense decimal(20,2),CashPaymentToFinancialInstitutions decimal(20,2),CashPaymentToDiscounts decimal(20,2),WrittenOffBadLoansAndInterestRecovery decimal(20,2),ReducedCommissionAndAgencyBusiness decimal(20,2),CashOutflowOfPreciousMetal decimal(20,2),CashPayedForGoodsAndServices decimal(20,2),NetDecreaseInOtherBorrowedFunds decimal(20,2),CashPaymentForOtherDecreasedAssets decimal(20,2),CashPaymentForOtherReducedAccounts decimal(20,2),CashPaymentToOtherBusiRelated decimal(20,2),CashPaymentBusiSubTotal decimal(20,2),NetCashFlowOp decimal(20,2),CashRecievedFromInvestmentWithdraw decimal(20,2),CashRecievedFromInvestmentGain decimal(20,2),CashDividendsOrProfitShareReceived decimal(20,2),CashReceivedFromBondInterest decimal(20,2),CashFromDisposalOfAssets decimal(20,2),CashFromDisposalOfEquityInvestments decimal(20,2),CashFromSubsidiary decimal(20,2),CashFromOtherInvestment decimal(20,2),CashFromInvestmentSubtotal decimal(20,2),CashInvested decimal(20,2),CashForEquityIncreased decimal(20,2),CashForBonds decimal(20,2),CashForMergeAcquisition decimal(20,2),CashPaidInConstruction decimal(20,2),CashPaidForAssets decimal(20,2),CashPaidForSubsidiariesAcquired decimal(20,2),CashForOtherInvestRelatedActivities decimal(20,2),CashOutflowSubtotalForInvest decimal(20,2),NetCashFlowsFromInvest decimal(20,2),CashReceivedByAbsorbingInvestments decimal(20,2),CashAbsorbedFromIssueSecuritizedAssets decimal(20,2),CashReceivedFromIssuingBonds decimal(20,2),CashReceivedFromIssuanceSubordinatedBonds decimal(20,2),CashReceivedFromCapitalIncrease decimal(20,2),CashReceivedRelateToOtherFinancingActivities decimal(20,2),CashInflowsFromFinancingActivities decimal(20,2),CashPaidForDebt decimal(20,2),CashPaidForDividendsProfitsOrInterest decimal(20,2),CashPaidForInterest decimal(20,2),PaymentOfIPOExpenses decimal(20,2),CashPaidToOtherFinancingActivities decimal(20,2),CashOutflowsForFinancingActivitiesSubtotal decimal(20,2),NetCashFlowForFinancingActivities decimal(20,2),EffectOfExchangeRateChangesOnCashEqui decimal(20,2),CashEqui decimal(20,2),InitialCashEqui decimal(20,2),CurrentCashEqui decimal(20,2),NetProfit decimal(20,2),MinorityShareholderReturns decimal(20,2),ProvisionForImpairmentOfAssets decimal(20,2),ProvisionForBadDebts decimal(20,2),LoanLossProvisionsInPreparation decimal(20,2),ReversalOfImpairmentDueFromBanks decimal(20,2),DepreciationOfOilAndGasAssetsAndProductiveBiologicalAssets decimal(20,2),DepreciationOfInvestmentProperty decimal(20,2),AmortizationOfIntangibleDeferredAndOtherAssets decimal(20,2),AmortizationOfIntangibleAssets decimal(20,2),AmortizationOfLongTermExpenses decimal(20,2),AmortizationOfLongTermAssets decimal(20,2),DisposalOfFixedAssetsAndOtherLongTermProductionOfIntangibleLossesGains decimal(20,2),LossGainOnDisposalOfInvestmentProperty decimal(20,2),LossesOnScrappingOfFixedAssets decimal(20,2),FinancialExpenses decimal(20,2),InvestmentLoss decimal(20,2),ChangesInFairValueOnForeignExchange decimal(20,2),ExchangeGainLoss decimal(20,2),DerivativeNetProfit decimal(20,2),DiscountedBackToPull decimal(20,2),DecreaseInInventories decimal(20,2),ReduceLoans decimal(20,2),IncreaseInDeposits decimal(20,2),NetLendingMoney decimal(20,2),ReducingFinancialAssets decimal(20,2),IncreaseExpectedLiabilities decimal(20,2),ReceivedWrittenOffPayments decimal(20,2),DecreaseInDeferredTaxAssets decimal(20,2),IncreaseInDeferredIncomeTaxLiabilities decimal(20,2),IncreaseInOperatingReceivables decimal(20,2),IncreaseInOperatingPayables decimal(20,2),DecreaseInOperatingOtherAssets decimal(20,2),OtherIncreasesInOperatingLiabilities decimal(20,2),OtherCosts decimal(20,2),NetCashFlowFromOperatingActivities decimal(20,2),FixedAssetsToRepayDebt decimal(20,2),InvestmentDebt decimal(20,2),FixedAssetsInvestment decimal(20,2),DebtIntoCapital decimal(20,2),ConvertibleBondDueInOneYearFixedAssetsLeased decimal(20,2),FinanceLeasedFixedAssets decimal(20,2),OtherFinancialActivitiesNotInvolvingCashReceiptsAndPayments decimal(20,2),PeriodEndCash decimal(20,2),PeriodBeginCash decimal(20,2),PeriodEndCashEqui decimal(20,2),PeriodBeginCashEqui decimal(20,2),NetIncreaseCashEqui decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-fr-quarter-CashFlow';

--fr profit-statement 47 fields
drop table if exists SinaFrProfitStatement;
--create external table SinaFrProfitStatement(stockid String,dt Date,BusinessIncome decimal(20,2),InterestRevenue decimal(20,2),InterestIncome decimal(20,2),InterestExpense decimal(20,2),FeesAndCommissionRevenue decimal(20,2),FeesAndCommissionIncome decimal(20,2),FeesAndCommissionExpense decimal(20,2),IntermediaryBusinessRevenue decimal(20,2),IntermediaryBusinessIncome decimal(20,2),IntermediaryBusinessExpense decimal(20,2),TradingRevenue decimal(20,2),DerivativeTradingRevenue decimal(20,2),ExchangeRevenue decimal(20,2),InvestmentRevenue decimal(20,2),AssociatedCompanyInvestmentRevenue decimal(20,2),FairValueGainLoss decimal(20,2),OtherBusinessIncome decimal(20,2),Opex decimal(20,2),BusinessTaxAndSurcharge decimal(20,2),OperationAdminExpense decimal(20,2),AssetImpairment decimal(20,2),Depreciation decimal(20,2),BadDebitReserve decimal(20,2),OtherExpense decimal(20,2),OperatingRevenue decimal(20,2),NoneOperatingRevenue decimal(20,2),NoneOperatingExpense decimal(20,2),TotalRevenue decimal(20,2),Tax decimal(20,2),MinorityShareholdersEquity decimal(20,2),RevenueToParentCompany decimal(20,2),PeriodBeginUndistributedProfit decimal(20,2),DistributableProfit decimal(20,2),StatutorySurplusReserve decimal(20,2),StatutoryPublicWelfareFund decimal(20,2),GeneralReserve decimal(20,2),TrustCompensationReserve decimal(20,2),DistributableProfitToShareholders decimal(20,2),PreferredDividendsPayable decimal(20,2),SurplusReserve decimal(20,2),CommonStockDividendPayable decimal(20,2),CommonStockDividendPayableTransferredToCapital decimal(20,2),UndistributedProfit decimal(20,2),BasicEPS decimal(20,2),DilutedEPS decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-fr-quarter-ProfitStatement';
create external table SinaFrProfitStatement(stockid String, dt Date, BusinessIncome decimal(20,2), BusinessProfit decimal(20,2), TotalProfit decimal(20,2), BasicEPS decimal(20,2),DilutedEPS decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-fr-quarter-ProfitStatement';

--
drop table if exists SinaShareBonusDividend;
create external table SinaShareBonusDividend(stockid String, dt Date, SongGu decimal(10,2), ZhuanZeng decimal(10,2), Devidend decimal(10,2), progress String, ExDate Date, RegDate Date, XStockPublicDate Date, comment String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-issue-sharebonus/dividend';

--
drop table if exists SinaShareBonusAlloted;
create external table SinaShareBonusAlloted(stockid String, dt Date, AllotNumberEveryTen decimal(10,2), price decimal(10,4), base decimal(20,2), ExDate Date, RegDate Date, PayStartDate Timestamp, PayEndDate Date, XStockPublicDate Date, TotalAmount decimal(20,2), comment String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-issue-sharebonus/allotted';

--market dzjy
drop table if exists SinaMarketDZJY;
create external table SinaMarketDZJY(dt Date, stockid String, stockname String, price decimal(10,4), volume decimal(20,2), amount decimal(20,2), buyerAgent String, sellerAgent String, stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-market-dzjy';

--market fu quan
drop table if exists SinaMarketFQ;
create external table SinaMarketFQ(stockid String, dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2), fqIdx decimal(10,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-market-fq';

--market history
drop table if exists SinaMarketDaily;
create external table SinaMarketDaily(stockid String, dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-market-history';

--market rzrq - summary
drop table if exists SinaMarketRZRQSummary;
create external table SinaMarketRZRQSummary(dt Date, market String, MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-market-rzrq/summary';

--market rzrq - detail
drop table if exists SinaMarketRZRQDetail;
create external table SinaMarketRZRQDetail(dt Date, idx decimal(10,2), stockid String, stockname String, MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2), ShortSellBalanceStock decimal(20,2), ShortSellSellVolume decimal(20,2), ShortSellReturnVolume decimal(20,2), ShortSellLeftBalance decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-market-rzrq/detail';

--market trade-detail
drop table if exists SinaMarketTradeDetail;
create external table SinaMarketTradeDetail(stockid String, dt Timestamp, price decimal(10,2), delta decimal(6,2), volume decimal(20,2), amount decimal(20,2), isSell int) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-market-tradedetail/';

--
drop table if exists SinaStockTopHolderSummary;
create external table SinaStockTopHolderSummary(stockid String, dt Date, publishDate Date, desc String, stockHolderNumber decimal(10,2), avgStockHolder decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder/Summary';

drop table if exists SinaStockTopHolder;
create external table SinaStockTopHolder(stockid String, dt Date, rank decimal(10,2), holderName String, volume decimal(20,2), percentage decimal(8,4), stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder/TopStockHolder';

drop table if exists SinaStockTopHolderCirculate;
create external table SinaStockTopHolderCirculate(stockid String, dt Date, publishDate Date, rank decimal(10,2), holderName String, volume decimal(20,2), percentage decimal(8,4), stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder-circulate/TopCirculateStockHolder';

drop table if exists SinaStockFundHolder;
create external table SinaStockFundHolder(stockid String, dt Date, fundName String, fundId String, volume decimal(20,2), circulatePercentage decimal(8,4), amount decimal(20,2), valuePercentage decimal(8,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder-fund/FundStockHolder';

drop table if exists SinaStockStructure;
create external table SinaStockStructure(stockid String, dt Date, pubDate Date, pic String, reason String, total decimal(20,4), circulate decimal(20,4), circulateA decimal(20,4), managerShare decimal(20,4), limitA decimal(20,4), circulateB decimal(20,4), limitB decimal(20,4), circulateH decimal(20,4), stateShare decimal(20,4), stateLegalPersonShare decimal(20,4), nationalLegalPersonShare decimal(20,4), nationalFounderShare decimal(20,4), MJLegalShare decimal(20,4), generalLegalShare decimal(20,4), strategyInvestShare decimal(20,4), fundShare decimal(20,4), xdShare decimal(20,4), staffShare decimal(20,4), priorityShare decimal(20,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-structure';

