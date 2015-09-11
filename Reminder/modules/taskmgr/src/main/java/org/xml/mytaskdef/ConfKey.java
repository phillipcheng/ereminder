package org.xml.mytaskdef;

public class ConfKey {
	//these are system attribute names defined in the schema, can be used in value expression
	
	//predefined page name
	public static final String PRD_LIST_NextPage="nextPage";
	public static final String CURRENT_PAGE="currentPage";
	public static final String START_PAGE="startPage";//the prd task start page
	public static final String LIST_PAGE="listPage"; //for bct and bdt
	//predefined attribute name
	public static final String TOTAL_PAGENUM="totalPageNum";
	public static final String CURRENT_PAGENUM="pagenum";
	public static final String PRD_NEXTPAGE="nextPage";
	
	//parameters
	public static final String PARAM_PRE="[";
	public static final String PARAM_POST="]";
	
	//idurlmapping key attribute name
	public static final String ID_KEY="id";
	public static final String PAGE_NUM_KEY="pageNum";
}
