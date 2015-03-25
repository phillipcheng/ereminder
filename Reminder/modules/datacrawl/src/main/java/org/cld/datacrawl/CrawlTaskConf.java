package org.cld.datacrawl;


import org.cld.datacrawl.mgr.IProductAnalyze;
import org.cld.datacrawl.mgr.IProductListAnalyze;
import org.cld.datacrawl.mgr.ICategoryAnalyze;
import org.cld.datacrawl.mgr.IListAnalyze;
import org.cld.datacrawl.pagea.ProductAnalyzeInf;
import org.cld.datacrawl.pagea.ProductListAnalyzeInf;
import org.cld.datacrawl.pagea.CategoryAnalyzeInf;
import org.cld.datacrawl.pagea.ListAnalyzeInf;
import org.cld.datacrawl.pagea.PromAnalyzeInf;

//this class must be stateless
public class CrawlTaskConf {
	
	public static final String catImpl_Key="category.impl"; //pagea.CategoryAnalyzeInf
	public static final String listImpl_Key="list.impl";//ListAnalyzeInf
	public static final String productListImpl_Key="product.list.impl";//ProductListAnalyzeInf
	public static final String promDetailImpl_Key="prom.detail.impl";//PromAnalyzeInf
	public static final String productDetailImpl_Key = "product.detail.impl";//ProductAnalyzeInf
	public static final String productType_Key="product.type";
	public static final String maxPagesPerTask_Key="max.pages.pertask";
	public static final String forcePageLimit_Key="force.page.limit";
	public static final String skipUrls_Key="skipUrls";
	
	private String name; //for example: general
	private String catImpl; //the implementation class name for CategoryAnalyzeInf of this site
	private String listImpl;//
	private String productListImpl;//
	private String productDetailImpl;//
	private String promDetailImpl;
	
	private CategoryAnalyzeInf caInf;//the stateless custom implementation of the page analyze
	private ListAnalyzeInf laInf;
	private PromAnalyzeInf paInf;
	private ProductListAnalyzeInf blaInf;
	private ProductAnalyzeInf baInf;
	
	private ICategoryAnalyze ca;//system implemented analyzer manager
	private IListAnalyze la;
	private IProductListAnalyze bla;
	private IProductAnalyze ba;
	
	public String toString(){
		String str;
		if (caInf != null && laInf != null && paInf != null && blaInf!= null){
			str = "\n" + System.identityHashCode(this) + "\n" + 
					"caInf:" + caInf.toString() + "\n" + 
					"laInf:" + laInf.toString() + "\n" + 
					"paInf:" + paInf.toString() + "\n" + 
					"blaInf:" + blaInf.toString();
		}else{
			 str = "\n" + System.identityHashCode(this) + "\n" + "null" + "\n";
		}
		
		if (baInf != null){
			str +="baInf:" + baInf.toString() + "\n";
		}
		
		if (ca!=null)
			str+="ca:" + ca.toString() + "\n";
		if (la!=null)
			str+="la:" + la.toString() + "\n";
		if (bla!=null)
			str+="bla:" + bla.toString() + "\n";
		if (ba!=null)
			str+="ba:" + ba.toString() + "\n";
		
		return str;
	}
	
	public ICategoryAnalyze getCa() {
		return ca;
	}
	public void setCa(ICategoryAnalyze ca) {
		this.ca = ca;
	}
	public IListAnalyze getLa() {
		return la;
	}
	public void setLa(IListAnalyze la) {
		this.la = la;
	}
	public IProductListAnalyze getBla() {
		return bla;
	}
	public void setBla(IProductListAnalyze bla) {
		this.bla = bla;
	}
	
	public CategoryAnalyzeInf getCaInf() {
		return caInf;
	}
	public void setCaInf(CategoryAnalyzeInf caInf) {
		this.caInf = caInf;
	}
	public ListAnalyzeInf getLaInf() {
		return laInf;
	}
	public void setLaInf(ListAnalyzeInf laInf) {
		this.laInf = laInf;
	}
	public PromAnalyzeInf getPaInf() {
		return paInf;
	}
	public void setPaInf(PromAnalyzeInf paInf) {
		this.paInf = paInf;
	}
	public ProductListAnalyzeInf getBlaInf() {
		return blaInf;
	}
	public void setBlaInf(ProductListAnalyzeInf blaInf) {
		this.blaInf = blaInf;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCatImpl() {
		return catImpl;
	}
	public void setCatImpl(String catImpl) {
		this.catImpl = catImpl;
	}
	public String getListImpl() {
		return listImpl;
	}
	public void setListImpl(String listImpl) {
		this.listImpl = listImpl;
	}
	public String getProductListImpl() {
		return productListImpl;
	}
	public void setProductListImpl(String productListImpl) {
		this.productListImpl = productListImpl;
	}
	public String getPromDetailImpl() {
		return promDetailImpl;
	}
	public void setPromDetailImpl(String promDetailImpl) {
		this.promDetailImpl = promDetailImpl;
	}
	public String getProductDetailImpl() {
		return productDetailImpl;
	}
	public void setProductDetailImpl(String productDetailImpl) {
		this.productDetailImpl = productDetailImpl;
	}
	public ProductAnalyzeInf getBaInf() {
		return baInf;
	}
	public void setBaInf(ProductAnalyzeInf baInf) {
		this.baInf = baInf;
	}
	public IProductAnalyze getBa() {
		return ba;
	}
	public void setBa(IProductAnalyze ba) {
		this.ba = ba;
	}
}
