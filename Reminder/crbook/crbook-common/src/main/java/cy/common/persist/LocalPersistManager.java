package cy.common.persist;

import java.util.*;

import cy.common.entity.Book;
import cy.common.entity.Reading;
import cy.common.entity.Volume;
import cy.common.entity.Page;

public interface LocalPersistManager {
	//////////////////////
	//Volume, First part same as remote persist manager
	///////////////////////
	//by id
	public Volume getVolumeById(String id);
	//get volumes
	public List<Volume> getVolumesByPCat(String[] pcatValue, int offset, int limit);
    public List<Volume> getVolumesLike(String param, int offset, int limit);
    //get count
	public long getVCByPCat(String[] pcatValue);
    public long getVCLike(String param);
	//update
	public long insertOrUpdateVolume(Volume vol);
	//
	public String getReadingDisplayName(Reading r);
    public int deleteRecursiveVolumeById(String id);    
	public Book getFirstBook(Volume v);	
	
	/////////////////////
	//Book, First part same as remote persist manager
	///////////////////////
    //by id
    public Book getBookById(String id);
	//get books
	public List<Book> getBooksByName(String name, int offset, int limit);
	public List<Book> getBooksByCat(String[] catId, int offset, int limit);
	//get count
	public long getBCByCat(String[] catId);
    public long getBCByName(String name);
    //update
	public long insertOrUpdateBook(Book book);
	//
	public void createBookAndPages(Book b, List<Page> pages);
	public void createBookAndPageUrls(Book b, List<String> pageUris);	
	
	////////////////////////////////////////////////////
	/// For pages, First part same as remote persist manager
	////////////////////////////
	public long insertOrUpdatePage(Page page);
	
	public Page getPage(String bookid, int pageNum);
	
	public List<Page> getPagesOfBook(String bookId);
	//
	public String getPageBgUrl(Book b, int pageNum);
  	public int deletePagesOfBook(String bookId);
    public int deleteBook(String id);
	
	////////////////
	/// reading
	public long insertOrUpdateIfNew(Reading r);
	//////////////////
    
    ////////////////
	//For my reading
	////////////////
	public int addMyReadings(List<String> ids);
	//judge which ids in the input id list are my reading, used for adding the flag when listing the readings
	public List<String> getMyReadingsIn(List<String> ids);
	public List<Reading> getMyReadingsLike(String param, int offset, int limit);
	public int deleteMyReadings(List<String> ids);
    
}
