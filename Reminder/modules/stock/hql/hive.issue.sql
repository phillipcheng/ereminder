set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--market dzjy
create external table MarketDZJY(dt Date, stockid String, stockname String, price decimal(10,4), volume decimal(20,2), amount decimal(20,2), buyerAgent String, sellerAgent String, stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-dzjy';
