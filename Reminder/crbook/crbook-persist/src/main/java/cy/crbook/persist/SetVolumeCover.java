package cy.crbook.persist;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.jdbc.SqlUtil;

import cy.common.entity.Book;
import cy.common.entity.Reading;
import cy.common.entity.Volume;

public class SetVolumeCover {
	
	public Logger logger = LogManager.getLogger(SetVolumeCover.class);
	
	private JDBCPersistService pService;
	
	public SetVolumeCover(JDBCPersistService pService){
		this.pService = pService;
	}
	
	private final String getVolCountByParentCatAndDataNotLikeSQL = "select count(*) as cnt from " + DBHeader.TABLE_VOL + 
			" where data not like ? and booknum>0 and type=1 and pcat in ";
    public long getVCByPCatAndDataNotLike(List<String> pcatValue, String data){    
		String inSql = SqlUtil.generateInParameters(pcatValue);
    	return SqlUtil.getSingleIntResultSQL(getVolCountByParentCatAndDataNotLikeSQL+inSql, 
    			new Object[]{data, pcatValue}, pService.getDataSource());	
    }

    private final String getVolByPCatAndDataLikeSQL="select" + JDBCPersistService.allVolumeDBFields + "from " + DBHeader.TABLE_VOL 
    		+ " where data like ? and booknum>0 and type=1 and pcat in ";
    public List<Volume> getVolumesByPCatAndDataLike(List<String> pcatValue, String data, 
    		Connection db, int offset, int limit){  
    	String inSql = SqlUtil.generateInParameters(pcatValue);
    	logger.info("getVolByPCatAndDataSQL sql:"+getVolByPCatAndDataLikeSQL+inSql+ ", pcatValue:" + pcatValue);
    	return pService.getVolumesByParam(getVolByPCatAndDataLikeSQL+inSql, new Object[]{data, pcatValue}, db, offset, limit);
    }
    
    private final String getVolByPCatAndDataNotLikeSQL="select" + JDBCPersistService.allVolumeDBFields + "from " + DBHeader.TABLE_VOL 
    		+ " where data not like ? and booknum>0 and type=1 and pcat in ";
    private List<Volume> getVolumesByPCatAndDataNotLike(List<String> pcatValue, String data, int offset, int limit){  
    	String inSql = SqlUtil.generateInParameters(pcatValue);
    	logger.info("getVolByPCatAndDataSQL sql:"+getVolByPCatAndDataNotLikeSQL+inSql+ ", pcatValue:" + pcatValue);
    	return pService.getVolumesByParam(getVolByPCatAndDataNotLikeSQL + inSql, new Object[]{data, pcatValue}, null, offset, limit);
    }
	//return the volume count whose cover is empty and parent category is given and book count > 0
	private List<Volume> getSubV(List<String> cats){
		int limit=50;
		return getVolumesByPCatAndDataNotLike(cats, "{\"coverUri\"%", 0, limit);
	}
	//return the volumes whose cover is empty and parent category is given and book count > 0
	private long getSubVC(List<String> cats){
		return getVCByPCatAndDataNotLike(cats, "{\"coverUri\"%");
	}
	
    private void setFirstNonEmptyCover(ReadingChildInfo ci, Connection con){
    	//select one whose coverUri is not empty
		List<String> cats = new ArrayList<String>();
		cats.add(ci.me.getId());
		List<Volume> vl = getVolumesByPCatAndDataLike(cats, "{\"coverUri\"%", con, 0, 1);
		if (vl.size()>0){
			Volume v = vl.get(0);
			ci.me.setCoverUri(v.getCoverUri());
			ci.me.dataToJSON();
			pService.insertOrUpdateReading(ci.me, con);
		}
    }
    
	public void postProcess(ReadingChildInfo ci) {
		Connection con =null;
		try{
			con = pService.getDataSource().getConnection();
			String coverUrl=null;
			if (ci.firstChild!=null){
				if (ci.firstChild instanceof Book){
					coverUrl = pService.getCoverUri((Book)ci.firstChild, con);
				}else{
					coverUrl = ci.firstChild.getCoverUri();
				}
				
				if (coverUrl!=null && !"".equals(coverUrl)){
					ci.me.setCoverUri(coverUrl);
					ci.me.dataToJSON();
					pService.insertOrUpdateReading(ci.me, con);
				}else{
					setFirstNonEmptyCover(ci, con);
				}
			}else{
				setFirstNonEmptyCover(ci, con);
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
    
	private final String getEmptyCoverVCSQL="select count(*) from " + DBHeader.TABLE_VOL 
    		+ " where data not like ? and booknum>0 and type=1 ";
	//return the volumes whose cover is empty, whose book count > 0 and return ordered by id
	public long getEmptyCoverVC(){
    	return SqlUtil.getSingleIntResultSQL(getEmptyCoverVCSQL, new Object[]{"{\"coverUri\"%"}, pService.getDataSource());
	}
	private final String getEmptyCoverVolumesSQL="select" + JDBCPersistService.allVolumeDBFields + "from " + DBHeader.TABLE_VOL 
    		+ " where data not like ? and booknum>0 and type=1 ";
	//return the volumes whose cover is empty, whose book count > 0 and return ordered by id
	public Volume getTopEmptyCoverVolume(int offset){
    	List<Volume> vl = pService.getVolumesByParam(getEmptyCoverVolumesSQL, new Object[]{"{\"coverUri\"%"}, 
    			null, offset, 1, " order by id ");
    	if (vl.size()>0){
    		return vl.get(0);
    	}else{
    		return null;
    	}
	}
	
	//
	public ReadingChildInfo dfsSetCover(Reading r){
		if (r instanceof Volume){
			Volume v = (Volume) r;
			List<String> cats = new ArrayList<String>();
			cats.add(r.getId());
			long subVC = getSubVC(cats);
			long tbc = pService.getBCByCat(cats);
			if (subVC==0){
				//all books under, finish recursion
				if (tbc>0){
					//set first child: the 1st book whose page number > 0
					List<Book> bl = pService.getBooksByCatAndMinTotalPage(r.getId(), 1, 0, 1);
					Book b = null;
					if (bl.size()>0){
						b = bl.get(0);
						b.expand();
					}
					ReadingChildInfo rci = new ReadingChildInfo(r, b, tbc);
					postProcess(rci);
					return rci;
				}else{
					//no empty cover sub cat, no sub books, using the cover of 1st sub cat
					ReadingChildInfo rci = new ReadingChildInfo(r, null, 0);
					postProcess(rci);
					return rci;
				}
			}else{
				//has more layer, start recursion
				ReadingChildInfo retRCI=new ReadingChildInfo(r);
				int count=0;
				
				long beforeVC = 0;
				long afterVC=0;
				do{
					beforeVC = getSubVC(cats);
					List<String> mycats = new ArrayList<String>();
					mycats.add(v.getId());
					//get the top limit sub volumes whose cover is empty, then set the cover
					List<Volume> vl = getSubV(cats);
					for (Volume c: vl){	
						logger.debug("dfs volume:" + c.getId() + "," + c.getName());
						//start recursion
						ReadingChildInfo rci = dfsSetCover(c);
						if (rci!=null){
							if (count==0){
								//first child whose coverUri is empty
								retRCI.firstChild=rci.firstChild;
							}
							retRCI.totalChildBookCount+=rci.totalChildBookCount;
							count++;
						}
					}					
					postProcess(retRCI);
					afterVC = getSubVC(cats);
				}while((beforeVC-afterVC)>0);//some solved, until no more solved
				return retRCI;
			}			
		}else{
			ReadingChildInfo rci = new ReadingChildInfo(r, r, 1);
			postProcess(rci);
			return rci;
		}
	}
	
	
	//
	public void setAllCoverUrl(){
		SetVolumeCover svc = new SetVolumeCover(pService);
		long beforeVC = 0;
		long afterVC=0;
		List<String> troubleIds = new ArrayList<String>();
		do{
			beforeVC = svc.getEmptyCoverVC();
			Volume v = svc.getTopEmptyCoverVolume(troubleIds.size());
			if (v!=null){
				logger.debug("dfs volume:" + v.getId() + "," + v.getName());
				svc.dfsSetCover(v);
			}else{
				break;
			}
			afterVC = svc.getEmptyCoverVC();
			if (beforeVC == afterVC){
				//find a trouble volume
				troubleIds.add(v.getId());
				logger.info("added trouble id:" + v.getId());
			}
		}while(true);
	}
}