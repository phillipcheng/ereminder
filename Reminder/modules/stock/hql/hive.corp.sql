set hive.exec.mode.local.auto=true;
set hive.cli.print.header=true;
set hive.cli.print.current.db=true;
set hive.auto.convert.join=true;
set hive.mapred.supports.subdirectories=true;
set mapred.input.dir.recursive=true;

--corp info
create external table CorpInfo(stockid String,name String,EnglishName String, IPOMarket String, IPODate Date, IPOPrice decimal(10,2), leadUnderwriter String, foundDate Date, RegisteredCapital String, InstitutionType String, OrgType String, BoardSecretary String, CompanyPhone String, BoardSecretaryPhone String, CompanyFax String, BoardSecretaryFax String, CompanyEmail String, BoardSecretaryEmail String, CompanyWebsite String, zipcode String, InfoDisclosureWebsite String, NameHistory String, RegisteredAddress String, OfficeAddress String, CompanyInfo String, BusinessScope String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-info';

--corp manager
create external table CorpManager( stockid String, name String, title String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-manager';

--corp related securities
create external table CorpRelatedSecurities( stockid String, securityId String, securityName String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-related/securities';

--corp related indices
create external table CorpRelatedIndices( stockid String, indexId String, indexName String, startDate Date, endDate Date) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-related/indices';

--corp related xis
create external table CorpRelatedXis( stockid String, xiName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-related/xis';

--corp related other industries
create external table CorpRelatedIndustries( stockid String, industryName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-related-other/industries';

--corp related other concepts
create external table CorpRelatedConcepts( stockid String, conceptName String, comment String) row format delimited fields terminated by ',' escaped by '\\' stored as textfile location '/reminder/items/sina-stock-corp-related-other/concepts';

