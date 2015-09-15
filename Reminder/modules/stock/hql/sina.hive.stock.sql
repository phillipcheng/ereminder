set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--
drop table if exists SinaStockTopHolderCirculate;
create external table SinaStockTopHolderCirculate(stockid String, dt Date, publishDate Date, rank decimal(10,2), holderName String, volume decimal(20,2), percentage decimal(8,4), stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder-circulate/TopCirculateStockHolder';

drop table if exists SinaStockTopHolderSummary;
create external table SinaStockTopHolderSummary(stockid String, dt Date, publishDate Date, desc String, stockHolderNumber decimal(10,2), avgStockHolder decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder/Summary';

drop table if exists SinaStockTopHolder;
create external table SinaStockTopHolder(stockid String, dt Date, rank decimal(10,2), holderName String, volume decimal(20,2), percentage decimal(8,4), stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder/TopStockHolder';

drop table if exists SinaStockFundHolder;
create external table SinaStockFundHolder(stockid String, dt Date, fundName String, fundId String, volume decimal(20,2), circulatePercentage decimal(8,4), amount decimal(20,2), valuePercentage decimal(8,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-holder-fund/FundStockHolder';


drop table if exists SinaStockStructure;
create external table SinaStockStructure(stockid String, changeDate Date, pubDate Date, pic String, reason String, total decimal(20,4), circulate decimal(20,4), circulateA decimal(20,4), managerShare decimal(20,4), limitA decimal(20,4), circulateB decimal(20,4), limitB decimal(20,4), circulateH decimal(20,4), stateShare decimal(20,4), stateLegalPersonShare decimal(20,4), nationalLegalPersonShare decimal(20,4), nationalFounderShare decimal(20,4), MJLegalShare decimal(20,4), generalLegalShare decimal(20,4), strategyInvestShare decimal(20,4), fundShare decimal(20,4), xdShare decimal(20,4), staffShare decimal(20,4), priorityShare decimal(20,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-stock-structure';