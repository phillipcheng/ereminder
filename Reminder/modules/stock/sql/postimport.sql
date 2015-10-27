update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=3 and b.title like '%第一季度%' and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=3 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=9 and b.title like '%第三季度%' and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=9 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=6 and (b.title like '%半年%' or b.title like '%中期报告%') and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=6 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=12 and (b.title like '%年度报告%' or b.title like '%年报%') and extract(YEAR from ps.dt)+1=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=12 and ps.pubDt is null;