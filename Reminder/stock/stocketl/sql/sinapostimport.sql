#
update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=3 and b.title like '%第一季度%' and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=3 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=9 and b.title like '%第三季度%' and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=9 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=6 and (b.title like '%半年%' or b.title like '%中期报告%') and extract(YEAR from ps.dt)=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=6 and ps.pubDt is null;

update SinaFrProfitStatement ps set pubDt = (select min(dt) from SinaCorpBulletin b where ps.stockid=b.stockid and extract(MONTH from ps.dt)=12 and (b.title like '%年度报告%' or b.title like '%年报%') and extract(YEAR from ps.dt)+1=extract(YEAR from b.dt)) where extract(MONTH from ps.dt)=12 and ps.pubDt is null;

#
update SinaFrAchieveNotice set summary=content where summary='';

update SinaFrAchieveNotice set aType='预升' where (summary like '%增长%' or summary like '%增幅%' or summary like '%上升%' or summary like '%上涨%' or summary like '%增加%' or summary like '%涨幅%' or summary like '%预增%' or summary like '%相比增%') and aType='';

update SinaFrAchieveNotice set aType='预降' where (summary like '%下降%' or summary like '%降幅%' or summary like '%下跌%' or summary like '%减少%' or summary like '%降低%' or summary like '%预减%' or summary like '%下滑%' or summary like '%下浮%') and aType='';

update SinaFrAchieveNotice set aType='减亏' where (summary like '%减亏%') and aType='';