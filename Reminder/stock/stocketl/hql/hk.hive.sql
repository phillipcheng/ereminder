
drop table if exists HKExDivSplit;
create external table HKExDivSplit(stockid String, dt Date, info String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/hk-issue-xds-history';

--HK-quote-historical
drop table if exists HKFqHistory;
create external table HKFqHistory(stockid String, dt Date, Open decimal(20,2), high decimal(20,2), low decimal(20,2), close decimal(20,2), volume bigint, adjClose decimal(20,2)) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/hk-quote-fq-historical';