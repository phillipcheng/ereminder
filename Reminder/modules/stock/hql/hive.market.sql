set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--market dzjy
create external table MarketDZJY(dt Date, stockid String, stockname String, price decimal(10,4), volume decimal(20,2), amount decimal(20,2), buyerAgent String, sellerAgent String, stockType String) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-dzjy';

--market fu quan
create external table MarketFQ(stockid String, dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2), fqIdx decimal(10,4)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-fq';

--market history
create external table MarketDaily(stockid String, dt Date, open decimal(10,4), high decimal(10,4), close decimal(10,4), low decimal(10,4), volume decimal(20,2), amount decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-history';

--market rzrq - summary
create external table MarketRZRQSummary(dt Date, market String, MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-rzrq/summary';

--market rzrq - detail
create external table MarketRZRQDetail(dt Date, idx decimal(10,2), stockid String, stockname String, MarginTradingBalance decimal(20,2), MarginTradingBuy decimal(20,2), MarginTradingReturn decimal(20,2), ShortSellBalance decimal(20,2), ShortSellBalanceStock decimal(20,2), ShortSellSellVolume decimal(20,2), ShortSellReturnVolume decimal(20,2), ShortSellLeftBalance decimal(20,2)) row format delimited fields terminated by ',' stored as textfile location '/reminder/items/sina-stock-market-rzrq/detail';

--market trade-detail
create external table MarketTradeDetail(stockid String, dt Timestamp, price decimal(10,2), delta decimal(6,2), volume decimal(20,2), amount decimal(20,2), isSell int) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-market-tradedetail/trade_detail/';

