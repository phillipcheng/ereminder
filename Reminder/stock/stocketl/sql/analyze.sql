drop table if exists TradeDetail;
create table TradeDetail(sn varchar(100), symbol varchar(10), buydate DATETIME, buyPrice decimal(10,5), sellDate DATETIME, orderType varchar(40), sellPrice decimal(10,5), percentage decimal(10,5), primary key (sn, symbol, buydate));


drop table if exists NasdaqProfile;
create table NasdaqProfile(symbol varchar(10), name varchar(100), lastsale decimal(10,4), marketcap decimal(30,2), adr decimal(20,2), ipoyear varchar(10), sector varchar(30), industry varchar(100), primary key (symbol));