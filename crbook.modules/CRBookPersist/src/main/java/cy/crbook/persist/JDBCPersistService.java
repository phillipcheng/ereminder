package cy.crbook.persist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;
import org.cld.util.jdbc.SqlUtil;

import cy.common.entity.Book;
import cy.common.entity.Page;
import cy.common.entity.Reading;
import cy.common.entity.Volume;
import cy.common.persist.RemotePersistManager;

public class JDBCPersistService implements RemotePersistManager{

	public Logger logger = LogManager.getLogger(JDBCPersistService.class);
	
	private DataSource dataSource;
	
	public JDBCPersistService(){
	}
	
	public JDBCPersistService(DataSource ds){
		this();
		this.dataSource = ds;		
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
    
    public void dropDB(DataSource ds, String tableSQL){
		Connection con =null;
		try{
			con =  ds.getConnection();
			SqlUtil.execUpdateSQL(con, tableSQL);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
    public void dropDB(DataSource ds){
		Connection con =null;
		try{
			con =  ds.getConnection();

			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_NAME_INDEX_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_CAT_INDEX_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_NAME_INDEX_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_PCAT_INDEX_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_AUTHOR_INDEX_DROP);
			
			SqlUtil.execUpdateSQL(con, DBHeader.SESSION_TABLE_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.PAGE_TABLE_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_TABLE_DROP);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_TABLE_DROP);
			
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
    
	public void createDB(DataSource ds){
		Connection con =null;
		try{
			con =  ds.getConnection();
			
			SqlUtil.execUpdateSQL(con, DBHeader.SESSION_TABLE_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.PAGE_TABLE_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_TABLE_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_TABLE_CREATE);
			
			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_NAME_INDEX_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.BOOK_CAT_INDEX_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_NAME_INDEX_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_PCAT_INDEX_CREATE);
			SqlUtil.execUpdateSQL(con, DBHeader.VOL_AUTHOR_INDEX_CREATE);
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public final String insertOrReplaceVolume = "replace into " + DBHeader.TABLE_VOL + 
	" (id, type, name, utime, pcat, author, data, booknum) values (?, ?, ?, ?, ?, ?, ?, ?)";
	
	public final String insertOrReplacePage = "replace into " + DBHeader.TABLE_PAGE + 
	" (id, pagenum, data, utime) values (?, ?, ?, ?)";
	
	public final String insertOrReplaceBook = "replace into " + DBHeader.TABLE_BOOK + 
	" (id, type, name, totalpage, lastpage, utime, data, cat, indexpage, author, status) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public String getPageBgUrlDirectly(Book b, Page p, int pageNum){
		PatternResult pattern = b.getPageBgUrlPattern();
		if (pattern!=null){
			return PatternResult.guessUrl(pattern, pageNum-1);
		}else{
			String url="";
			if (b.getbUrl()!=null){ 
				url = b.getbUrl() + p.getBackgroundUri();
			}
			if (b.getsUrl()!=null){
				url = url + b.getsUrl();
			}
			return url;
		}
	}
	
	//pagenum is from 1 to totalPage
	public String getPageBgUrl(Book b, int pageNum, Connection con){
		if (b!=null){
			PatternResult pattern = b.getPageBgUrlPattern();
			if (pattern!=null){
				return PatternResult.guessUrl(pattern, pageNum-1);
			}
			
			Page p = getPage(b.getId(), pageNum, con);
			if (p!=null){
				String url="";
				if (b.getbUrl()!=null){ 
					url = b.getbUrl() + p.getBackgroundUri();
				}
				if (b.getsUrl()!=null){
					url = url + b.getsUrl();
				}
				return url;
			}
		}
		return null;
	}
	
	public String getPageBgUrl(Book b, int pageNum){
		Connection con =null;
		try{
			con =  dataSource.getConnection();
			return getPageBgUrl(b, pageNum, con);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
    
	public void createBookAndPageUrls(Book b, List<String> pageUris){
		String[] aPageUris = new String[pageUris.size()];
		pageUris.toArray(aPageUris);
		Arrays.sort(aPageUris);
		List<Page> pages = new ArrayList<Page>();
		for (int i=0; i<aPageUris.length; i++){
			String strP = aPageUris[i];
			Page p = new Page();
			p.setBookid(b.getId());
			p.setBackgroundUri(strP);
			p.setPageNum(i+1);//page number ranging from 1 to totalPage
			p.setUtime(RemotePersistManager.SDF_SERVER_DTZ.format(new Date()));
			pages.add(p);
			logger.warn( "page to create:" + p);
		}
		b.setTotalPage(aPageUris.length);
		b.setUtime(new Date());
		logger.warn( "book to create:" + b);
		
		insertOrUpdateBook(b);
		
		for (int i=0; i<pages.size(); i++){
			Page p = pages.get(i);
			insertOrUpdatePage(p);
		}	
	}
	
	public void createBookAndPages(Book b, List<Page> pages){    	
		Connection con = null;    	
	  	try {
	  		con = dataSource.getConnection();
			b.setUtime(new Date());
			logger.info("book to create:" + b);
			
			insertOrUpdateBook(b, con);
			
			for (int i=0; i<pages.size(); i++){
				Page p = pages.get(i);
				insertOrUpdatePage(p, con);
			}	
	  	}catch(Exception e){
	  		logger.error("", e);
	  	}finally{
	  		SqlUtil.closeResources(con, null);
	  	}
	}
	
	public long insertOrUpdateReading(Reading r, Connection con){
		if (r instanceof Volume){
			return insertOrUpdateVolume((Volume)r, con);
		}else if (r instanceof Book){
			return insertOrUpdateBook((Book)r, con);
		}else{
			logger.error("no such type to insert or update.");
			return -1;
		}
	}
	//return the all cats down to leaf cat (containing only books), not including v itself
	//return in depth first order
	private List<Volume> getRecursiveCatDown(Volume v){
		List<Volume> lv = new ArrayList<Volume>();
		List<String> cats = new ArrayList<String>();
		cats.add(v.getId());
		lv = getVolumesByPCat(cats, -1, 0);
		if (lv.size()==0){
			return lv;
		}else{
			List<Volume> retlv = new ArrayList<Volume>();
			for (int i=0; i<lv.size(); i++){
				Volume va = lv.get(i);
				retlv.addAll(getRecursiveCatDown(va));
			}
			retlv.addAll(lv);
			return retlv;
		}
	}
	
	public Book getFirstBook(Volume v) {
		return null;
	}

	//////////////////////
	//Volume
	///////////////////////
	public long insertVolumeIfNotExists(Volume v) {
		Volume oldV = getVolumeById(v.getId());
		if (oldV==null){
			return insertOrUpdateVolume(v);
		}else{
			return 0;
		}
	}

	public long insertOrUpdateVolume(Volume vol, Connection con){		
		PreparedStatement statement=null;
		long ret=-1;
		try{
			statement = con.prepareStatement(insertOrReplaceVolume);	        
			con.setAutoCommit(false);
			statement.setString(1, vol.getId());
			statement.setInt(2, vol.getType());
			statement.setString(3, vol.getName());
			Date d = vol.getUtime();
			if (d==null)
				d=new Date();
			Timestamp ts = new Timestamp(d.getTime());
			statement.setTimestamp(4, ts);
			statement.setString(5, vol.getParentCat());
			statement.setString(6, vol.getAuthor());
			statement.setString(7, vol.getData());
			statement.setInt(8, vol.getBookNum());
            ret = statement.executeUpdate();			
			con.commit();
			return ret;
		}catch (SQLException e ) {
			logger.error("",e);
	        if (con != null) {
	            try {
	                con.rollback();
	            } catch(SQLException excep) {
	            	logger.error("",excep);
	            }
	        }
	        return ret;
	    } finally {
    		SqlUtil.closeResources(null, statement);
	        try {
	        	if (con!=null)
	        		con.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error("",e);
			}	    	
	    }
	}
	
	public long insertOrUpdateVolume(Volume vol){
		Connection con = null;
		try{
			con = dataSource.getConnection();			
			return insertOrUpdateVolume(vol, con);
	    }catch(Exception e){
	    	logger.error("", e);
	    	return -1;
	    }finally {
	    	SqlUtil.closeResources(con, null);
	    }
	}
	
	public final String getVolCountByParentCatSQL = "select count(*) as cnt from " + DBHeader.TABLE_VOL + 
			" where pcat in ";
	public final String getVolCountByNameSQL="select count(*) as cnt from " + DBHeader.TABLE_VOL + 
			" where type=? name like ?";
	public final String getVolCountByAuthorSQL="select count(*) as cnt from " + DBHeader.TABLE_VOL + 
			" where type=? and author like ?";
	public final String getVolCountByParamSQL="select count(*) as cnt from " + DBHeader.TABLE_VOL + 
			" where type=? and (author like ? or name like ?)";

    public long getVCByPCat(List<String> pcatValue){    
		String inSql = SqlUtil.generateInParameters(pcatValue);
    	return SqlUtil.getSingleIntResultSQL(getVolCountByParentCatSQL+inSql, new Object[]{pcatValue}, dataSource);	
    }
    
    public long getVCByName(String name, int type){    	
    	return SqlUtil.getSingleIntResultSQL(getVolCountByNameSQL, new Object[]{type, "%"+name+"%"}, dataSource);
	}
    
    public long getVCByAuthor(String author, int type){  	
    	return SqlUtil.getSingleIntResultSQL(getVolCountByAuthorSQL, new Object[]{type, "%"+author+"%"}, dataSource);
	}
    
    public long getVCLike(String param, int type){
    	return SqlUtil.getSingleIntResultSQL(getVolCountByParamSQL, new Object[]{type, "%"+param+"%", "%"+param+"%"}, dataSource);
    }
   
    public List<Volume> getVolumesByParam(String sql, Object[] param, Connection con, int offset, int limit){
    	return getVolumesByParam(sql, param, con, offset, limit, null);
    }
    public List<Volume> getVolumesByParam(String sql, Object[] param, Connection con, int offset, int limit, String orderby){
    	return (List<Volume>) SqlUtil.getObjectsByParam(sql, param, con, offset, limit, orderby, VolumeJDBCMapper.getInstance(), dataSource);
    }

    public final static String allVolumeDBFields = " id, type, name, utime, data, pcat, author, booknum ";
    //query 1
    public final String getVolByPCatSQL="select" + allVolumeDBFields + "from " + DBHeader.TABLE_VOL 
    		+ " where pcat in ";
    public List<Volume> getVolumesByPCat(List<String> pcatValue, int offset, int limit){
    	String inSql = SqlUtil.generateInParameters(pcatValue);
    	logger.info("getVolumesByPCat sql:"+getVolByPCatSQL+inSql+ ", pcatValue:" + pcatValue);
    	return getVolumesByParam(getVolByPCatSQL+inSql, new Object[]{pcatValue}, null, offset, limit, null);
	}
    //query 2
    public final String getVolLikeSQL="select" + allVolumeDBFields + "from " + DBHeader.TABLE_VOL 
    		+ " where type=? and (author like ? or name like ?)";
    public List<Volume> getVolumesLike(String param, int type, int offset, int limit){
    	return getVolumesByParam(getVolLikeSQL, new Object[]{type, "%"+param+"%", "%"+param+"%"}, null, offset, limit, null);    	
	}
    //query 3
    public final String getVolByIdSQL="select" + allVolumeDBFields + "from " + DBHeader.TABLE_VOL + " where id=? ";
    public Volume getVolumeById(String id){
    	List<Volume> lv = getVolumesByParam(getVolByIdSQL, new String[]{id}, null, 0, 1, null);
    	if (lv.size()==1){
    		return lv.get(0);
    	}else{
    		return null;
    	}
	}
    
    public final String deleteVolByCatSQL= "delete from " + DBHeader.TABLE_VOL + " where pcat = ?";
    private int deleteVolumesByPCat(String pcat){
	  	return SqlUtil.deleteBySQL(deleteVolByCatSQL, pcat, dataSource);
	}
    
    public final String deleteVolByIdSQL= "delete from " + DBHeader.TABLE_VOL + " where id=?";
    private int deleteVolumeById(String id){
	  	return SqlUtil.deleteBySQL(deleteVolByIdSQL, id, dataSource);
	}
    
    public int deleteRecursiveVolumeById(String id){    	
		Volume v = getVolumeById(id);    	
	  	List<Volume> lv = getRecursiveCatDown(v);
	  	lv.add(v);
	  	for (Volume va : lv){
  			//1st cat, it's child can be books, and for book, we need to delete book and pages
  			//remove all books with this cat
	  		List<String> cats = new ArrayList<String>();
	  		cats.add(va.getId());
	  		List<Book> blist = getBooksByCat(cats, -1, 0);
	  		for (Book b : blist){
	  			int pages = deletePagesOfBook(b.getId());
	  			logger.info(pages + " pages deleted. for book:" + b.getId() + "," + b.getName());
	  			deleteBook(b.getId());
	  			logger.info("book:" + b.getId() + ", name:" + b.getName());
	  		}
  		
	  		//now we remove volumes.
	  		int vs = deleteVolumesByPCat(va.getId());
	  		logger.info(vs + " volumes deleted. whose parent volume is:" + va.getId());
	  		
	  		//delete myself
		  	deleteVolumeById(va.getId());
	  		logger.info("volume deleted with id:" + va.getId());
	  	}
	  	return 0;
	}
    
	/////////////////////
	//Book
	///////////////////////
	public long insertBookIfNotExists(Book b) {
		Book oldB = getBookById(b.getId());
		if (oldB==null){
			return insertOrUpdateBook(b);
		}else{
			return 0;
		}				
	}
	
    public long insertOrUpdateBook(Book book){    	
		return insertOrUpdateBook(book, null);
	}
    
	//-1 for failure
	public long insertOrUpdateBook(Book book, Connection outdb){
		Connection con=null;
		int suc=-1;
		PreparedStatement statement = null;
		try{
			if (outdb==null)
				con= dataSource.getConnection();
			else
				con = outdb;
			statement = con.prepareStatement(insertOrReplaceBook);
			statement.setString(1, book.getId());
			statement.setInt(2, book.getType());
			statement.setString(3, book.getName());
			statement.setInt(4, book.getTotalPage());
			statement.setInt(5, book.getLastPage());
			Date d = book.getUtime();
			if (d==null)
				d = new Date();
			Timestamp ts = new Timestamp(d.getTime());
			statement.setTimestamp(6, ts);
			statement.setString(7, book.getData());
			statement.setString(8, book.getCat());
			statement.setInt(9, book.getIndexedPages());
			statement.setString(10, book.getAuthor());
			statement.setInt(11, book.getStatus());
			suc=statement.executeUpdate();
			if (suc==1){
				logger.info("book:" + book.getId() + " is added or updated.");
			}
			return suc;
		}catch (SQLException e ) {
			logger.error("",e);
			return suc;
	    } finally {
	    	SqlUtil.closeResources(null, statement);
    		if (outdb==null){
    			SqlUtil.closeResources(con, null);
    		}	    	
	    }
	}    
    
	public final String getBCByCatSQL= "select count(*) as cnt from " + DBHeader.TABLE_BOOK + " where cat in ";
    public long getBCByCat(List<String> catId){
    	String inSql = SqlUtil.generateInParameters(catId);
    	return SqlUtil.getSingleIntResultSQL(getBCByCatSQL+inSql, new Object[]{catId}, dataSource);
	}
    
    public final String getBCByNameSQL= "select count(*) as cnt from " + DBHeader.TABLE_BOOK + " where name like ? and type=?";
    public long getBCByName(String name, int type){    	
    	return SqlUtil.getSingleIntResultSQL(getBCByNameSQL, new Object[]{"%"+name+"%", type}, dataSource);
	}
    
    private static final String allBookDBFields =" id, type, name, totalpage, lastpage, utime, data, cat, indexpage, author, status ";
    private List<Book> getBooksByParam(String sql, Object[] param, Connection con, String orderBy, int offset, int limit){
    	return (List<Book>) SqlUtil.getObjectsByParam(sql, param, con, offset, limit, orderBy, BookJDBCMapper.getInstance(), dataSource);
    }
    //query 1
    public static final String getBooksByNameSQL="select" + allBookDBFields + "from " + DBHeader.TABLE_BOOK + " where name like ? and type=?";
    public List<Book> getBooksByName(String name, int type, int offset, int limit){	
		return getBooksByParam(getBooksByNameSQL, new Object[]{"%"+name+"%", type}, null, null, offset, limit);
	}
	//query 2
    public static final String getBooksByCatMinTotalPageSQL="select" + allBookDBFields + "from " + DBHeader.TABLE_BOOK + " where cat = ? "
    		+ " and totalPage >= ?";
    public List<Book> getBooksByCatAndMinTotalPage(String catId, int minTotalPage, int offset, int limit){
    	return getBooksByParam(getBooksByCatMinTotalPageSQL, new Object[]{catId, new Integer(minTotalPage)}, null, null, offset, limit); 
    }
    //query 3
    public static final String getBooksByCatSQL="select" + allBookDBFields + "from " + DBHeader.TABLE_BOOK + " where cat in ";
    public List<Book> getBooksByCat(List<String> catId, int offset, int limit){    
    	String inSql = SqlUtil.generateInParameters(catId);
    	return getBooksByParam(getBooksByCatSQL + inSql, new Object[]{catId}, null, null, offset, limit);  
    }
    //query 4
    public static final String getLatestBooksSQL = "select" + allBookDBFields + "from " + DBHeader.TABLE_BOOK + " where type = ?";
    public List<Book> getLatestBooks(int type, int offset, int limit){
    	return getBooksByParam(getLatestBooksSQL, new Object[]{new Integer(type)}, null, " order by utime desc ", offset, limit);
    }
    
    public final String getBooksByIdSQL="select" + allBookDBFields + "from " + DBHeader.TABLE_BOOK + " where id = ?";
    public Book getBookById(String id, Connection con){    	
    	List<Book> blist = getBooksByParam(getBooksByIdSQL, new String[]{id}, con, null, 0, 1);    	
    	if (blist.size()>0){
    		return blist.get(0);
    	}else{
    		return null;
    	}
	}
    public Book getBookById(String id){    	
    	return getBookById(id, null);
	}
    
    public final String deleteBookByIdSQL= "delete from " + DBHeader.TABLE_BOOK + " where id=?";
    public int deleteBook(String id){    	
    	return SqlUtil.deleteBySQL(deleteVolByCatSQL, id, dataSource);
	}

	//the cover either the 1st page or the cover
  	public String getCoverUri(Book b, Connection con){
		String coverUri = b.getCoverUri();
		if (coverUri==null || "".equals(coverUri)){
			//try 1st page
			coverUri = getPageBgUrl(b, 1, con);
		}		
		return coverUri;
  	}
    
    ////////////////////////////////////////////////////
    /// For pages
    ////////////////////////////
    public long insertOrUpdatePage(Page page){
		return insertOrUpdatePage(page, null);
	}
    //-1 for failure
  	public long insertOrUpdatePage(Page page, Connection outdb){
  		Connection con=null;      
		int suc=-1;
		PreparedStatement statement = null;
		try{
			if (outdb==null)
				con= dataSource.getConnection();
			else
				con = outdb;
			statement = con.prepareStatement(insertOrReplacePage);
			statement.setString(1, page.getBookid());
	  		statement.setInt(2, page.getPagenum());
	  		statement.setString(3, page.getData());
	  		Timestamp ts = new Timestamp(new Date().getTime());
	  		statement.setTimestamp(4, ts);
			suc=statement.executeUpdate();
			return suc;
		}catch (SQLException e ) {
			logger.error("",e);
			return suc;
	    } finally {
	    	if (outdb==null)
	    		SqlUtil.closeResources(con, statement);	    	
	    }
  	}
  	
	public long insertPagesIfNotExists(List<Page> pageList) {
		Connection con = null;
		try{
			long count=0;
			con = dataSource.getConnection();
			for (Page p: pageList){
				if (getPage(p.getBookid(), p.getPagenum(), con)==null){
					insertOrUpdatePage(p, con);
					count++;
				}
			}
			return count;
		}catch(Exception e){
			logger.error("", e);
			return -1;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public long insertOrUpdateBookIfNotExistsOrLessComplete(Book b){
		Connection con = null;
		try{
			long count=0;
			con = dataSource.getConnection();
			Book oldBook = getBookById(b.getId(), con);
			if (oldBook==null || oldBook.getIndexedPages()<b.getIndexedPages()){
				insertOrUpdateBook(b, con);
				count++;
			}else{
				logger.info("book with id:" + b.getId() + " index page:" + b.getIndexedPages() + 
						" is less then the old one with:" + oldBook.getIndexedPages() + ", so not updated.");
			}
			return count;
		}catch(Exception e){
			logger.error("", e);
			return -1;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
	
	public long insertOrUpdateBookPagesIfNotExistsOrLessComplete(Book b, List<Page> pageList) {
		Connection con = null;
		try{
			long count=0;
			con = dataSource.getConnection();
			Book oldBook = getBookById(b.getId(), con);
			if (oldBook==null || oldBook.getIndexedPages()<b.getIndexedPages()){
				insertOrUpdateBook(b, con);
				count++;
			}
			for (Page p: pageList){
				if (getPage(p.getBookid(), p.getPagenum(), con)==null){
					insertOrUpdatePage(p, con);
					count++;
				}
			}
			return count;
		}catch(Exception e){
			logger.error("", e);
			return -1;
		}finally{
			SqlUtil.closeResources(con, null);
		}
	}
  	
    private Page getPage(ResultSet cursor){
    	try{
	    	Page b= new Page(
					cursor.getString(1), cursor.getInt(2), 
					cursor.getString(3), cursor.getString(4), false);
	    	return b;
    	}catch(Exception e){
    		logger.error("", e);
    		return null;
    	}
    }
    
    private List<Page> getPageByParam(String sql, String param, Connection db){    	
    	ArrayList<Page> pages = new ArrayList<Page>();
    	
    	Connection con = null;
		PreparedStatement statement=null;
		try{
			if (db==null){
				con = dataSource.getConnection();
			}else{
				con = db;
			}
			statement = con.prepareStatement(sql);
			statement.setString(1, param);
			ResultSet rs= statement.executeQuery();
			while(rs.next()){				
				Page p = getPage(rs);
				pages.add(p);
			}
			if(rs != null && !rs.isClosed()){
				rs.close();
			}
			return pages;
		}catch(Exception e){
			logger.error("",e);
			return pages;
		}finally{			
			SqlUtil.closeResources(null, statement);
			
			if (db==null){
				SqlUtil.closeResources(con, null);
			}
		}
    }
    
    public final String getPagesOfBookSQL="select * from " + DBHeader.TABLE_PAGE + " where id=?";
    public List<Page> getPagesOfBook(String bookId){
    	return getPageByParam(getPagesOfBookSQL, bookId, null);
    }
  	
    public final String getPageOfBookPageNumSQL="select * from " + DBHeader.TABLE_PAGE + " where id=? and pagenum=";
    public Page getPage(String bookId, int pageNum){	  
    	return getPage(bookId, pageNum, null);
    }
    public Page getPage(String bookId, int pageNum, Connection con){	  
    	List<Page> pages = getPageByParam(getPageOfBookPageNumSQL+pageNum, bookId, con);
    	if (pages.size()>0){
    		return pages.get(0);
    	}else{
    		return null;
    	}
    }

    public final String deletePagesOfBookSQL="delete from " + DBHeader.TABLE_PAGE + " where id=?";
  	public int deletePagesOfBook(String bookId){  		
  		return SqlUtil.deleteBySQL(deletePagesOfBookSQL, bookId, dataSource);
	}
  	
	/////////////////////////
	//Session Management
	///////////////////////
	public final String addSessionSQL = "insert into " + DBHeader.TABLE_SESSION + 
			" (id, device, userid, stime) values (?, ?, ?, ?)";
	public String login(String device, String userId, String pass, String stime) {
		if (!"".equals(userId) && null!=userId){
			//authenticate first
			if (!checkUser(userId, pass))
				return RemotePersistManager.LOGIN_FAILED;
		}
		
		String id = device+stime;
		Timestamp d=null;
		try {
			d = new Timestamp((RemotePersistManager.SDF_SERVER_DTZ.parse(stime)).getTime());
		} catch (ParseException e1) {
			logger.error("wrong time format:" + stime, e1);
		}
		
		Connection con = null;
		PreparedStatement statement=null;
		try{
			con = dataSource.getConnection();
			statement = con.prepareStatement(addSessionSQL);
			statement.setString(1, id);
			statement.setString(2, device);
			statement.setString(3,  userId);
			statement.setTimestamp(4, d);
			statement.executeUpdate();
			return id;
		}catch(Exception e){
			logger.error("",e);
			return null;
		}finally{			
			SqlUtil.closeResources(con, statement);
		}
	}
	public final String updateSessionSQL = "update " + DBHeader.TABLE_SESSION + 
			" set etime=? where id=?";
	public boolean logout(String sessionId, String etime) {
		Timestamp d=null;
		try {
			d = new Timestamp((RemotePersistManager.SDF_SERVER_DTZ.parse(etime)).getTime());
		} catch (ParseException e1) {
			logger.error("wrong time format:" + etime, e1);
		}
		
		Connection con = null;
		PreparedStatement statement=null;
		try{
			con = dataSource.getConnection();
			statement = con.prepareStatement(updateSessionSQL);
			statement.setTimestamp(1, d);
			statement.setString(2, sessionId);
			int ret = statement.executeUpdate();	
			if (ret==1){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			logger.error("",e);
			return false;
		}finally{			
			SqlUtil.closeResources(con, statement);
		}
	}

	///////////////////
	//user management
	//////////////////
  	private static final String findUserNumByIdSql = "select count(*) from users where userid=?";
  	private static final String addUserSql = "insert into users (userid, pass, utime) values (?, ?, ?)";
	public String addUser(String userId, String pass) {
		Connection con=null;
		try{
			con = dataSource.getConnection();
			//check existing userId
			long count = SqlUtil.getSingleIntResultSQL(findUserNumByIdSql, new String[]{userId}, con);
			if (count!=0){
				return RemotePersistManager.USER_EXIST;
			}
			//add user
			java.util.Date utilDate = new java.util.Date();
		    java.sql.Timestamp timeStamp = new java.sql.Timestamp(utilDate.getTime());
			int ret=SqlUtil.execUpdateSQLWithParams(con, addUserSql, new Object[]{userId, pass, timeStamp});
			if (ret==1){
				return RemotePersistManager.SIGN_UP_SUCCEED;
			}
		}catch(Exception e){
			logger.error("", e);
			return e.toString();
		}finally{
			SqlUtil.closeResources(con, null);
		}
		
		return RemotePersistManager.FAILED_UNKNOWN;
	}
	
	public boolean removeUser(String userId){
		Connection con=null;
		try{
			con = dataSource.getConnection();
			String sql1 = "delete from users where userid=?";
			int ret=SqlUtil.execUpdateSQLWithParams(con, sql1, new String[]{userId});
			if (ret==1){
				return true;
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		
		return false;
	}
	
	private boolean checkUser(String userId, String pass){
		Connection con=null;
		try{
			con = dataSource.getConnection();
			String sql = "select count(*) from users where userid=? and pass=?";
			long count = SqlUtil.getSingleIntResultSQL(sql, new String[]{userId, pass}, con);
			if (count==1){
				return true;
			}
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return false;
	}
	////////////
	//my reading remote API
	///////////
	public int addMyReadings(String userId, List<String> ids){
		//insert into myreadings values ('uid1','rid1'), ('uid1','rid2')
		if (ids.size()>0){
			String sql = "insert into myreadings values ";
			for (int i=0;i<ids.size();i++){
				String pair = "('"+ userId + "',?)";
				sql = sql + pair;
				if (i<(ids.size()-1)){
					sql = sql + ",";
				}
			}
			logger.info("sql:" + sql);
			Connection con = null;
			try{
				con = dataSource.getConnection();
				String[] params = new String[ids.size()];
				params = ids.toArray(params);
				int ret=SqlUtil.execUpdateSQLWithParams(con, sql, params);
				return ret;
			}catch(Exception e){
				logger.error("", e);
			}finally{
				SqlUtil.closeResources(con, null);
			}
		}
		return 0;
	}
	public int deleteMyReadings(String userId, List<String> ids){
		String sql = "delete from myreadings where userid=? and rid in ";
		if (ids.size()>0){
			sql += SqlUtil.generateInParameters(ids);
			Connection con = null;
			try{
				con = dataSource.getConnection();
				String[] params = new String[ids.size()+1];
				params[0] = userId;
				for (int i=0; i<ids.size(); i++){
					params[i+1]=ids.get(i);
				}
				logger.info(String.format("sql:%s, params are %s.", sql, Arrays.toString(params)));
				int ret=SqlUtil.execUpdateSQLWithParams(con, sql, params);
				return ret;
			}catch(Exception e){
				logger.error("", e);
			}finally{
				SqlUtil.closeResources(con, null);
			}
		}
		return 0;
	}
	public List<Book> getMyBooksLike(String userId, String param, int type, int offset, int limit){
		String sql= "";
		List<Book> books=new ArrayList<Book>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			if (param!=null && !"".equals(param)){
				sql = "select" + allBookDBFields + "from books, myreadings " +
						"where id=rid and userid=? and name like ? and type=?";
				books = getBooksByParam(sql, new Object[]{userId, "%"+param+"%", type}, con, null, offset, limit);
			}else{
				sql = "select" + allBookDBFields + "from books, myreadings " +
						"where id=rid and userid=? and type=?";
				books = getBooksByParam(sql, new Object[]{userId, type}, con, null, offset, limit);
			}
			return books;
		}catch(Exception e){
			logger.error("",e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return books;
    }
	public int getMyBooksCountLike(String userId, String param, int type){
		String sql= null;
		long ret=0;
		Connection con = null;
		try{
			con = dataSource.getConnection();
			if (param!=null && !"".equals(param)){
				sql = "select count(*) from books, myreadings where userid=? and id=rid and name like ? and type=?";
				ret = SqlUtil.getSingleIntResultSQL(sql, new Object[]{userId, "%"+param+"%", type}, con);
			}else{
				sql = "select count(*) from books, myreadings where userid=? and id=rid and type=?";
				ret = SqlUtil.getSingleIntResultSQL(sql, new Object[]{userId, type}, con);
			}
		}catch(Exception e){
			logger.error("",e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return (int) ret;
    }
	
	public List<Volume> getMyVolumesLike(String userId, String param, int type, int offset, int limit) {	
		String sql= null;
		List<Volume> volumes=new ArrayList<Volume>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			if (param!=null && !"".equals(param)){
				sql = "select" + allVolumeDBFields +"from volumes, myreadings where userid=? and id = rid and (name like ? or author like ?) and type=?";
				volumes = getVolumesByParam(sql, new Object[]{userId, "%"+param+"%", "%"+param+"%", type}, con, offset, limit, null);
			}else{
				sql = "select" + allVolumeDBFields +"from volumes, myreadings where userid=? and id = rid and type=?";
				volumes = getVolumesByParam(sql, new Object[]{userId, type}, con, offset, limit, null);
			}
			return volumes;
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return volumes;
	}
	
	public int getMyVolumesCountLike(String userId, String param, int type) {		
		String sql= null;
		long ret=0;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			
			if (param!=null && !"".equals(param)){
				sql="select count(*) from volumes, myreadings where userid=? and id=rid and (name like ? or author like ?) and type=?";
				ret = SqlUtil.getSingleIntResultSQL(sql, new Object[]{userId, "%"+param+"%", "%"+param+"%", type}, con);
			}else{
				sql="select count(*) from volumes, myreadings where userid=? and id=rid";
				ret = SqlUtil.getSingleIntResultSQL(sql, new Object[]{userId, type}, con);
			}
			return (int) ret;
		}catch(Exception e){
			logger.error("", e);
		}finally{
			SqlUtil.closeResources(con, null);
		}
		return 0;
	}

	//judge which ids in the input id list are my reading, used for adding the flag when listing the readings
	public List<String> getMyReadingsIn(String userId, List<String> ids){
		//select * from myreadings where userid='1' and rid in ('1','3')
		return null;
	}
}