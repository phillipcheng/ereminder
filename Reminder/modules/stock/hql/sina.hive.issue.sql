set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--
drop table if exists SinaShareBonusDividend;
create external table SinaShareBonusDividend(stockid String, annouceDate Date, SongGu decimal(10,2), ZhuanZeng decimal(10,2), Devidend decimal(10,2), progress String, ExDate Date, RegDate Date, XStockPublicDate Date, comment String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-issue-sharebonus/dividend';

--
drop table if exists SinaShareBonusAlloted;
create external table SinaShareBonusAlloted(stockid String, dt Date, AllotNumberEveryTen decimal(10,2), price decimal(10,4), base decimal(20,2), ExDate Date, RegDate Date, PayStartDate Timestamp, PayEndDate Date, XStockPublicDate Date, TotalAmount decimal(20,2), comment String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/merge/sina-stock-issue-sharebonus/allotted';
