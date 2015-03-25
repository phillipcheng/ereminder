package cy.common.persist;

import java.util.*;

import org.cld.util.SafeSimpleDateFormat;

import cy.common.entity.Book;
import cy.common.entity.Volume;
import cy.common.entity.Page;

public interface RemotePersistManager {
	
	//this is the sdf stored in the server database
	public static final String DATE_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ssZ";
	public static final SafeSimpleDateFormat SDF_SERVER_DTZ = new SafeSimpleDateFormat(DATE_TIME_FORMAT);
	
	public static final String LOGIN_FAILED="LoginFailed";
	public static final String SIGN_UP_SUCCEED="SignupSucceed";
	public static final String USER_EXIST="UserExist";
	public static final String FAILED_UNKNOWN="Unknownly Failed";
	//////////////////////
	//Volume
	//////////////////////
	//by id
    public Volume getVolumeById(String id);
    //get volumes
    public List<Volume> getVolumesByPCat(List<String> pcatValue, int offset, int limit);
    public List<Volume> getVolumesLike(String param, int type, int offset, int limit);
    //get count
    public long getVCByPCat(List<String> pcat);
    public long getVCLike(String param, int type);
    //insert
    public long insertVolumeIfNotExists(Volume v);
    
	/////////////////////
	//Book
	/////////////////////
    //by id
    public Book getBookById(String id);
    //get books
    public List<Book> getBooksByName(String name, int type, int offset, int limit);
    public List<Book> getBooksByCat(List<String> catId, int offset, int limit);
    public List<Book> getLatestBooks(int type, int offset, int limit);
    //get count
    public long getBCByCat(List<String> catId);
    public long getBCByName(String name, int type);
    //insert
    public long insertBookIfNotExists(Book b);
    
    /////////////
    // Pages
    //////////////
    //insert
    public long insertPagesIfNotExists(List<Page> pageList);
    
    //////////////
    //Sessions
    /////////////
    public String login(String device, String userId, String pass, String stime);
    public boolean logout(String sessionId, String etime);
    
    ///////////
    //Users
    //////////
    public String addUser(String userId, String pass);
    public boolean removeUser(String userId);
    
    ////////////////
	//My reading
	////////////////
	public int addMyReadings(String userId, List<String> ids);
	public int deleteMyReadings(String userId, List<String> ids);
	//search
	public List<Volume> getMyVolumesLike(String userId, String param, int type, int offset, int limit);
	public int getMyVolumesCountLike(String userId, String param, int type);
	public List<Book> getMyBooksLike(String userId, String param, int type, int offset, int limit);
	public int getMyBooksCountLike(String userId, String param, int type);
	//check: ids in the input id list are my reading, used for adding the flag when listing the readings
	public List<String> getMyReadingsIn(String userId, List<String> ids);
  	
}
