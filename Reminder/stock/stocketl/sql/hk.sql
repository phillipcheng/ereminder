#HKIds
drop table if exists HKIds;
create table HKIds(stockid varchar(150), primary key (stockid));

#
drop table if exists HKExDivSplit;
create table HKExDivSplit(stockid varchar(150), dt Date, info varchar(150), primary key (stockid, dt));

#
drop table if exists HKFqHistory;
create table HKFqHistory(stockid varchar(150), dt Date, open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint, adjClose decimal(20,2), primary key (stockid, dt));
create index Idx_HKFqHistory_Date on HKFqHistory (dt);
create index Idx_HKFqHistory_stockid on HKFqHistory (stockid);
create index Idx_HKFqHistory_adjClose on HKFqHistory (adjClose);