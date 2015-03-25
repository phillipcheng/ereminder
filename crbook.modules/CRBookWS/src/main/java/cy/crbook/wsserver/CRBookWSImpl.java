package cy.crbook.wsserver;

import java.util.List;

import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;



import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cld.util.StringUtil;

import cy.common.entity.Book;
import cy.common.entity.BookPages;
import cy.common.entity.Page;
import cy.common.entity.Volume;
import cy.common.util.WSUtil;
import cy.crbook.persist.JDBCPersistService;

@WebService(endpointInterface = "cy.crbook.wsserver.CRBookWS", serviceName = "CRBook")
public class CRBookWSImpl implements CRBookWS{

	public Logger logger = LogManager.getLogger(CRBookWSImpl.class);
	
	@Context
	private ServletContext context; 
	
	private JDBCPersistService pService;
	
	public CRBookWSImpl(){
		
	}
	
	public CRBookWSImpl(JDBCPersistService pService){
		this.pService = pService;
	}
	public void setDataSource(JDBCPersistService pService){
		this.pService = pService;
	}
	public JDBCPersistService getDataSource(){
		return pService;
	}
	
	/////////////
	//volumes
	/////////////
	@Override
	public List<Volume> getVolumes(String method, String param, int offset,
			int limit, String userId, int type) {
		param=WSUtil.convertFromEmptyParam(param);
		userId=WSUtil.convertFromEmptyParam(userId);
		
		if (CRBookWS.METHOD_NAME_BYCAT.equals(method)){
			List<String> catList = StringUtil.fromStringList(param);
			return pService.getVolumesByPCat(catList, offset, limit);
		}
		
		if (CRBookWS.METHOD_NAME_BYLIKE.equals(method)){
			if ("".equals(userId)||userId==null){
				return pService.getVolumesLike(param, type, offset, limit);
			}else{
				return pService.getMyVolumesLike(userId, param, type, offset, limit);
			}
		}
		
		logger.error("unknow method:" + method);		
		return null;
	}
	
	@Override
	public long getVolumesCount(String method, String param, String userId, int type) {
		param=WSUtil.convertFromEmptyParam(param);
		userId=WSUtil.convertFromEmptyParam(userId);
		
		if (CRBookWS.METHOD_NAME_BYCAT.equals(method)){
			List<String> catList = StringUtil.fromStringList(param);
			return pService.getVCByPCat(catList);
		}
		
		if (CRBookWS.METHOD_NAME_BYLIKE.equals(method)){
			if ("".equals(userId)||userId==null){
				return pService.getVCLike(param, type);
			}else{
				return pService.getMyVolumesCountLike(userId, param, type);
			}
		}
		
		logger.error("unknow method:" + method);		
		return 0;
	}
	
	@Override
	public Volume getVolumeById(String id) {
		return pService.getVolumeById(id);
	}
	
	@Override
	public long addVolume(Volume v){
		WSUtil.fixUpReadingDate(v);
		return pService.insertVolumeIfNotExists(v);
	}
	
	//////////////
	//Books
	/////////////
	@Override
	public List<Book> getBooks(String method, String param, int offset,
			int limit, String userId, int type) {
		param=WSUtil.convertFromEmptyParam(param);
		userId=WSUtil.convertFromEmptyParam(userId);
		
		if (CRBookWS.METHOD_NAME_BYNAME.equals(method)){
			if ("".equals(userId)||userId==null){
				return pService.getBooksByName(param, type, offset, limit);
			}else{
				return pService.getMyBooksLike(userId, param, type, offset, limit);
			}
		}
		if (CRBookWS.METHOD_NAME_BYCAT.equals(method)){
			List<String> catList = StringUtil.fromStringList(param);
			return pService.getBooksByCat(catList, offset, limit);
		}
		logger.error("unknow method:" + method);	
		return null;
	}
	
	@Override
	public long getBooksCount(String method, String param, String userId, int type) {
		param=WSUtil.convertFromEmptyParam(param);
		userId=WSUtil.convertFromEmptyParam(userId);
		
		if (CRBookWS.METHOD_NAME_BYNAME.equals(method)){
			if ("".equals(userId)||userId==null){
				return pService.getBCByName(param, type);
			}else{
				return pService.getMyBooksCountLike(userId, param, type);
			}
		}
		if (CRBookWS.METHOD_NAME_BYCAT.equals(method)){
			List<String> catList = StringUtil.fromStringList(param);
			return pService.getBCByCat(catList);
		}
		logger.error("unknow method:" + method);	
		return 0;
	}
	
	@Override
	public Book getBookById(String id) {
		return pService.getBookById(id);
	}
	
	@Override
	public long addBook(Book b) {
		logger.debug("add book get:" + b.toString());
		WSUtil.fixUpReadingDate(b);
		return pService.insertOrUpdateBookIfNotExistsOrLessComplete(b);
	}
	
	///////////////////
	//Pages
	/////////////////
	@Override
	public long addPages(List<Page> pageList) {
		return pService.insertPagesIfNotExists(pageList);
	}
	
	@Override
	public long addBookPages(BookPages bps) {
		return pService.insertOrUpdateBookPagesIfNotExistsOrLessComplete(bps.getBook(), bps.getPages());
	}

	///////////
	//session
	//////////
	@Override
	public String login(String device, String userId, String pass, String stime) {
		pass=WSUtil.convertFromEmptyParam(pass);
		userId=WSUtil.convertFromEmptyParam(userId);
		return pService.login(device, userId, pass, stime);
	}
	
	@Override
	public boolean logout(String sessionId, String etime) {
		return pService.logout(sessionId, etime);
	}
	
	///////////
	//Users
	//////////
	@Override
	public String signup(String userId, String pass){
		pass=WSUtil.convertFromEmptyParam(pass);
		userId=WSUtil.convertFromEmptyParam(userId);
		return pService.addUser(userId, pass);
	}
	
	////////
	//My Readings
	////////
	@Override
	public int addMyReadings(String userId, String rids){
		List<String> ridList = StringUtil.fromStringList(rids);
		logger.info("input rids:" + rids);
		logger.info("rid list:" + ridList);
		return pService.addMyReadings(userId, ridList);
	}
	
	@Override
	public int deleteMyReadings(String userId, String rids){
		List<String> ridList = StringUtil.fromStringList(rids);
		logger.info("input rids:" + rids);
		logger.info("rid list:" + ridList);
		return pService.deleteMyReadings(userId, ridList);
	}
}
