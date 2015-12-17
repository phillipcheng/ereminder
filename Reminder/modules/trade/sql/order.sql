drop index StockPosition_stopSellOrderId on StockPosition;
drop index StockPosition_limitSellOrderId on StockPosition;
drop table if exists StockPosition;
create table StockPosition(symbol varchar(50), orderqty decimal(10,2), orderprice decimal(10,2), buySubmitDt DATETIME, buyOrderId varchar(50), stopSellOrderId varchar(50), limitSellOrderId varchar(50), soMap varchar(2000), primary key (buyOrderId));
create index StockPosition_stopSellOrderId on StockPosition (stopSellOrderId);
create index StockPosition_limitSellOrderId on StockPosition (limitSellOrderId);