set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--corp info
drop table if exists SinaCorpInfo;
create external table SinaCorpInfo(stockid String,name String,EnglishName String, IPOMarket String, IPODate Date, IPOPrice decimal(10,2), leadUnderwriter String, foundDate Date, RegisteredCapital String, InstitutionType String, OrgType String, BoardSecretary String, CompanyPhone String, BoardSecretaryPhone String, CompanyFax String, BoardSecretaryFax String, CompanyEmail String, BoardSecretaryEmail String, CompanyWebsite String, zipcode String, InfoDisclosureWebsite String, NameHistory String, RegisteredAddress String, OfficeAddress String, CompanyInfo String, BusinessScope String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-info';

--corp manager
drop table if exists SinaCorpManager;
create external table SinaCorpManager( stockid String, name String, title String, dt Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-manager';

--corp related securities
drop table if exists SinaCorpRelatedSecurities;
create external table SinaCorpRelatedSecurities( stockid String, securityId String, securityName String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/securities';

--corp related indices
drop table if exists SinaCorpRelatedIndices;
create external table SinaCorpRelatedIndices( stockid String, indexId String, indexName String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/indices';

--corp related xis
drop table if exists SinaCorpRelatedXis;
create external table SinaCorpRelatedXis( stockid String, xiName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/xis';

--corp related other industries
drop table if exists SinaCorpRelatedIndustries;
create external table SinaCorpRelatedIndustries( stockid String, industryName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/industries';

--corp related other concepts
drop table if exists SinaCorpRelatedConcepts;
create external table SinaCorpRelatedConcepts( stockid String, conceptName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/concepts';

