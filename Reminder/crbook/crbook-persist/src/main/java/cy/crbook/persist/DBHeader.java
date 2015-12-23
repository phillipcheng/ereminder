package cy.crbook.persist;

public class DBHeader {
	
	public static final String DATABASE_NAME = "CRBooks";
	
	public static final String TABLE_PAGE = "boards";
	public static final String TABLE_BOOK = "books";
	public static final String TABLE_VOL = "volumes";
	public static final String TABLE_SESSION = "sessions";
	public static final String TABLE_USER = "users";
	public static final String TABLE_MYREADINGS = "myreadings";
	public static final String TABLE_SITECONF = "siteconf";
	
	//common
	public static final String COL_ID="id";
	public static final String COL_NAME="name";
	public static final String COL_UTIME = "utime"; //updated time
	public static final String COL_DATA = "data";
	public static final String COL_SIZE = "size";
	
	//session
	public static final String COL_DEVICE="device";
	public static final String COL_STIME = "stime";//start time
	public static final String COL_ETIME = "etime";//end time
	
	//book
	public static final String COL_CAT = "cat";
	public static final String COL_TOTALPAGE="totalpage";
	public static final String COL_LASTPAGE="lastpage";
	public static final String COL_READ="readNum";
	public static final String COL_CACHED="cached";
	public static final String COL_INDEXPAGE="indexpage";
	
	//page
	public static final String COL_PAGENUM="pagenum";
	
	//volume
	public static final String COL_PARENTCAT="pcat";
	public static final String COL_AUTHOR="author";
	public static final String COL_BOOKNUM="booknum";
	
	//user
	public static final String COL_USER="userid";
	public static final String COL_PASSHASH="pass";
	
	//my reading
	public static final String COL_RID="rid"; //reading id
	
	//site conf
	
	//drop table
	public static final String PAGE_TABLE_DROP="drop table " + TABLE_PAGE + ";";
	public static final String BOOK_TABLE_DROP="drop table " + TABLE_BOOK + ";";
	public static final String VOL_TABLE_DROP="drop table " + TABLE_VOL + ";";
	public static final String SESSION_TABLE_DROP="drop table " + TABLE_SESSION + ";";
	public static final String USER_TABLE_DROP="drop table " + TABLE_USER + ";";
	public static final String MYREADINGS_TABLE_DROP="drop table " + TABLE_MYREADINGS + ";";
	
	//create table 
	public static final String PAGE_TABLE_CREATE =
	"CREATE TABLE boards (id TEXT, pagenum INTEGER, data TEXT, utime TIMESTAMP, primary key(id(100), pagenum));";
	
	public static final String BOOK_TABLE_CREATE =
	"CREATE TABLE books (id TEXT, name TEXT, type integer, totalpage INTEGER, lastpage INTEGER, utime TIMESTAMP, data MEDIUMTEXT, cat TEXT, indexpage integer, author TEXT, status integer, primary key(id(100)));";

	public static final String VOL_TABLE_CREATE =
	"CREATE TABLE volumes (id TEXT, name TEXT, type integer, utime TIMESTAMP, data TEXT, pcat TEXT, author TEXT, booknum INTEGER, primary key(id(100)));";
	
	public static final String SESSION_TABLE_CREATE =
	"CREATE TABLE sessions (id TEXT, device TEXT, userid TEXT, stime TIMESTAMP, etime TIMESTAMP, primary key(id(100)));";
	
	public static final String USER_TABLE_CREATE = 
	"create table users (userid text, pass text, utime TIMESTAMP, primary key(userid(100)));";
	
	public static final String MYREADING_TABLE_CREATE = 
	"create table myreadings (userid text, rid text, primary key(userid(100), rid(100)));";
	
	//drop index
	public static final String BOOK_NAME_INDEX_DROP="drop index book_name_idx on " + TABLE_BOOK + ";";
	public static final String BOOK_CAT_INDEX_DROP="drop index book_cat_idx on " + TABLE_BOOK + ";";
	public static final String VOL_NAME_INDEX_DROP="drop index vol_name_idx on " + TABLE_VOL + ";";
	public static final String VOL_PCAT_INDEX_DROP="drop index vol_pcat_idx on " + TABLE_VOL + ";";
	public static final String VOL_AUTHOR_INDEX_DROP="drop index vol_author_idx on " + TABLE_VOL + ";";
	
	
	//create index
	public static final String BOOK_NAME_INDEX_CREATE=
			"create index book_name_idx on books (name(100));";
	public static final String BOOK_CAT_INDEX_CREATE=
			"create index book_cat_idx on books (cat(100));";
	public static final String BOOK_AUTHOR_INDEX_CREATE=
			"create index book_author_idx on books (author(100));";
	public static final String BOOK_STATUS_INDEX_CREATE=
			"create index book_status_idx on books (status);";
	public static final String BOOK_UTIME_INDEX_CREATE=
			"create index book_utime_idx on books (utime);";
	public static final String BOOK_TYPE_INDEX_CREATE=
			"create index book_type_idx on books (type);";
	
	public static final String VOL_NAME_INDEX_CREATE=
			"create index vol_name_idx on volumes (name(100));";
	public static final String VOL_PCAT_INDEX_CREATE=
			"create index vol_pcat_idx on volumes (pcat(100));";
	public static final String VOL_AUTHOR_INDEX_CREATE=
			"create index vol_author_idx on volumes (author(100));";
	public static final String VOL_TYPE_INDEX_CREATE=
			"create index vol_type_idx on volumes (type);";

}

