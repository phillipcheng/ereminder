drop table if exists RangeStrategy;
create table RangeStrategy(symbol varchar(150), dt date, buyPrice decimal(20,2), primary key (symbol, dt));