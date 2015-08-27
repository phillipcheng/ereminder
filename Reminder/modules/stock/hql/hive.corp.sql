set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--corp info
drop table if exists CorpInfo;
create external table CorpInfo(stockid String,name String,EnglishName String, IPOMarket String, IPODate Date, IPOPrice decimal(10,2), leadUnderwriter String, foundDate Date, RegisteredCapital String, InstitutionType String, OrgType String, BoardSecretary String, CompanyPhone String, BoardSecretaryPhone String, CompanyFax String, BoardSecretaryFax String, CompanyEmail String, BoardSecretaryEmail String, CompanyWebsite String, zipcode String, InfoDisclosureWebsite String, NameHistory String, RegisteredAddress String, OfficeAddress String, CompanyInfo String, BusinessScope String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-info';

--corp manager
drop table if exists CorpManager;
create external table CorpManager( stockid String, name String, title String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-manager';

--corp related securities
drop table if exists CorpRelatedSecurities;
create external table CorpRelatedSecurities( stockid String, securityId String, securityName String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/securities';

--corp related indices
drop table if exists CorpRelatedIndices;
create external table CorpRelatedIndices( stockid String, indexId String, indexName String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/indices';

--corp related xis
drop table if exists CorpRelatedXis;
create external table CorpRelatedXis( stockid String, xiName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related/xis';

--corp related other industries
drop table if exists CorpRelatedIndustries;
create external table CorpRelatedIndustries( stockid String, industryName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/industries';

--corp related other concepts
drop table if exists CorpRelatedConcepts;
create external table CorpRelatedConcepts( stockid String, conceptName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/merge/sina-stock-corp-related-other/concepts';

