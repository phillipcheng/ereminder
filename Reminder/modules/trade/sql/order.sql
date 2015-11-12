drop table if exists StockPosition;
create table StockPosition(dt Date, orderqty decimal(10,2), orderprice decimal(10,2), symbol varchar(50), isOpenPos decimal(2,0), primary key (symbol, dt));
create index StockPosition_isOpen on StockPosition(isOpenPos);