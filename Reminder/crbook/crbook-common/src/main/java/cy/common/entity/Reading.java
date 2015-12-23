package cy.common.entity;

import java.util.Date;


public interface Reading extends IJSON, Comparable<Reading>{
	
	public static int STATE_ONLINE=0;
	public static int STATE_OFFLINE=1;
	public static int STATE_BOTH=2;
	//whether this reading is stored locally or only online
	public int getState();
	public void setState(int state);
	
	public static int TYPE_PIC=1;
	public static int TYPE_NOVEL=2;
	public static int TYPE_MIX=0;
	
	public int getType();
	public void setType(int type);
	
	//not stored
	public String getFullPath();
	
	public String getName();
	public void setName(String bookName);
	
	public String getCoverUri();
	public void setCoverUri(String coverUri);
	
	public String getCat();
	public String getId();
	
	public Date getUtime();
	public void setUtime(Date d);
	public String getStrUtime();
	
	public String getData();
	
	public String getAuthor();

	
}
