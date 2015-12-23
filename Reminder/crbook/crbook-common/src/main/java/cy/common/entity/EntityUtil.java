package cy.common.entity;


public class EntityUtil {
	
	
	public static int getNextPage(Book b, int pageNum){
		//next page
		if (pageNum<b.getTotalPage()){
			return pageNum+1;        			
		}else{
			//set to 1st page
			return 1;
		}	  
	}
	
	public static int getPreviousPage(Book b, int pagenum){
		//previous page
		if (pagenum>1){
			return pagenum-1;
		}else{
			//set to last page
			return b.getTotalPage();
		}   
	}

}
