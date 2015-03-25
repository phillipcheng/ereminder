package cy.crbook.wsserver;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cy.common.entity.Book;
import cy.common.entity.BookPages;
import cy.common.entity.Page;
import cy.common.entity.Reading;
import cy.common.entity.Volume;

@WebService
@Path("/crbookrs")
@Produces("application/json")
@Consumes("application/json")
public interface CRBookWS {
	
	public static final String METHOD_NAME_BYNAME="name";
	public static final String METHOD_NAME_BYAUTHOR="author";
	public static final String METHOD_NAME_BYCAT="cat";
	public static final String METHOD_NAME_BYLIKE="like";
	
	
	//////////////////////
	//Volume
	///////////////////////    
    @GET
   	@Path("/volumes/{method}/{param}/{offset}/{limit}/{userId}/{type}")
    public List<Volume> getVolumes(@PathParam("method")String method, @PathParam("param")String param, 
    		@PathParam("offset")int offset, @PathParam("limit")int limit, @PathParam("userId")String userId,
    		@PathParam("type")int type);
    
    @GET
   	@Path("/volumesCount/{method}/{param}/{userId}/{type}")
    public long getVolumesCount(@PathParam("method")String method, 
    		@PathParam("param")String param, @PathParam("userId")String userId, 
    		@PathParam("type")int type);
    
    @GET
   	@Path("/volumes/{Id}")
    public Volume getVolumeById(@PathParam("Id")String id);
    
    @PUT
    @Path("/volumes/")
    public long addVolume(Volume v);
    
	/////////////////////
	//Book
	///////////////////////
    
    @GET
   	@Path("/books/{method}/{param}/{offset}/{limit}/{userId}/{type}")
    public List<Book> getBooks(@PathParam("method")String method, @PathParam("param")String param, 
    		@PathParam("offset")int offset, @PathParam("limit")int limit, @PathParam("userId")String userId, 
    		@PathParam("type")int type);
    
    @GET
   	@Path("/booksCount/{method}/{param}/{userId}/{type}")
    public long getBooksCount(@PathParam("method")String method, 
    		@PathParam("param")String param, @PathParam("userId")String userId, 
    		@PathParam("type")int type);
    
    @GET
   	@Path("/books/{Id}")
    public Book getBookById(@PathParam("Id")String id);
    
    @PUT
    @Path("/books/")
    public long addBook(Book b);
    
	/////////////////////
	//Page
	///////////////////////
    @PUT
    @Path("/pages/")
    public long addPages(List<Page> pageList);
    
    @PUT
    @Path("/bookpages/")
    public long addBookPages(BookPages bps);

	///////////////
	//Sessions
	//////////
	@GET
	@Path("/login/{device}/{userid}/{pass}/{timestamp}")
	public String login(@PathParam("device")String device, @PathParam("userid")String userId, 
			@PathParam("pass")String pass, @PathParam("timestamp")String timestamp);
	
	@GET
	@Path("/logout/{sessionId}/{timestamp}")
	public boolean logout(@PathParam("sessionId")String sessionId, @PathParam("timestamp")String timestamp);
	
	///////////
	//Users
	///////////
	@GET
	@Path("/signup/{userId}/{pass}")
	public String signup(@PathParam("userId")String userId, @PathParam("pass")String pass);
	
	////////////
	//My Readings
	///////////
	@PUT
	@Path("/myreadings/add/")
	@Consumes("application/x-www-form-urlencoded")
	public int addMyReadings(@FormParam("userId")String userId, @FormParam("rids")String rids);
	
	@PUT
	@Path("/myreadings/delete/")
	@Consumes("application/x-www-form-urlencoded")
	public int deleteMyReadings(@FormParam("userId")String userId, @FormParam("rids")String rids);
	
}
