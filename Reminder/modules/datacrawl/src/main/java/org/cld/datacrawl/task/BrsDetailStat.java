package org.cld.datacrawl.task;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.taskmgr.entity.BrokenPage;
import org.cld.taskmgr.entity.TaskStat;

@Entity
@DiscriminatorValue("browse_detail")
public class BrsDetailStat extends TaskStat implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(BrsDetailStat.class);
	
	private transient Set<String> brokenListPage = new HashSet<String>();
	private transient Set<String> brokenDetailedPage = new HashSet<String>();
	private transient Set<String> brokenPromotionPage = new HashSet<String>();
	
	private int pageBrowsed; //clicked the product list page
	private int productBrowsed; //clicked into product details
	private int promoBrowsed; //clicked into promotion details
	private int productScanned; //not clicked into product details
	
	private int productAdded; //
	private int productAddFailed; //
	private int priceAdded; //
	private int priceAddFailed;//
	private int promoteAdded; //
	private int promoteAddFailed;
	
	private int productSkipped; //skipped due to optimization, e.g. by cat
	
	
	public BrsDetailStat(){
		
	}
	
	public BrsDetailStat(String tid){
		super(tid);
	}
	
	
	public void addProductAdded(){
		productAdded++;
	}
	public void addPriceAdded(){
		priceAdded++;
	}	
	public void addPromoteAdded(){
		promoteAdded++;
	}
	public void addProductSkipped(){
		productSkipped++;
	}
	public void addProductAddedFailed(){
		productAddFailed++;
	}
	public void addPriceAddedFailed(){
		priceAddFailed++;
	}
	public void addPromoteAddedFailed(){
		promoteAddFailed++;
	}
	
	public Set<String> getBrokenPromotionPages() {
		return brokenPromotionPage;
	}
	/*
	 * add a broken list page
	 */
	public void addBrokenPromotionPage(String pp){
		try {
			pp = URLDecoder.decode(pp, "UTF-8");
			if (!brokenPromotionPage.contains(pp)){
				this.brokenPromotionPage.add(pp);
			}
		}catch (UnsupportedEncodingException e) {
			logger.error("error add broken promotion page:" + pp, e);
		}
	}
	public void addSuccessPromotionPage(String pp){
		try {
			pp = URLDecoder.decode(pp, "UTF-8");
			if (brokenPromotionPage.contains(pp)){
				brokenPromotionPage.remove(pp);
			}
			promoBrowsed++;
		}catch (UnsupportedEncodingException e) {
			logger.error("error add success promotion page:" + pp, e);
		}
	}
	
	public Set<String> getBrokenListPages() {
		return brokenListPage;
	}
	/*
	 * add a broken list page
	 */
	public void addBrokenListPage(String lp){
		try {
			lp = URLDecoder.decode(lp, "UTF-8");
			if (!brokenListPage.contains(lp)){
				this.brokenListPage.add(lp);
			}
		}catch (UnsupportedEncodingException e) {
			logger.error("error add broken list page:" + lp, e);
		}
	}
	public void addSuccessListPage(String lp){
		try {
			lp = URLDecoder.decode(lp, "UTF-8");
			if (brokenListPage.contains(lp)){
				brokenListPage.remove(lp);
			}
			pageBrowsed++;
		}catch (UnsupportedEncodingException e) {
			logger.error("error add success list page:" + lp, e);
		}
	}
	public int getPageBrowsed() {
		return pageBrowsed;
	}
	
	
	public Set<String> getBrokenDetailedPages() {
		return brokenDetailedPage;
	}	
	/*
	 * add a broken detailed page
	 */
	public void addBrokenDetailedPage(String dp){
		try {
			dp = URLDecoder.decode(dp, "UTF-8");
			if (!brokenDetailedPage.contains(dp)){
				brokenDetailedPage.add(dp);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("error add broken detailed page:" + dp, e);
		}
	}
	public void addSuccessDetailedPage(String dp){
		try {
			dp = URLDecoder.decode(dp, "UTF-8");
			if (brokenDetailedPage.contains(dp)){
				brokenDetailedPage.remove(dp);
			}
			productBrowsed++;
		} catch (UnsupportedEncodingException e) {
			logger.error("error add succss detailed page:" + dp, e);
		}
	}
	
	public void addProductScan(){
		productScanned++;
	}
	
	public int getProductBrowsed() {
		return productBrowsed;
	}

	@Override
	public void add(TaskStat ts){
		BrsDetailStat bs = (BrsDetailStat)ts;
		this.pageBrowsed+=bs.pageBrowsed;
		this.productBrowsed+=bs.productBrowsed;
		this.promoBrowsed+=bs.promoBrowsed;	
		this.productScanned+=bs.productScanned;
		
		this.brokenListPage.addAll(bs.getBrokenListPages());
		this.brokenDetailedPage.addAll(bs.getBrokenDetailedPages());
		this.brokenPromotionPage.addAll(bs.getBrokenPromotionPages());
		
		this.productAdded+=bs.productAdded;
		this.priceAdded+=bs.priceAdded;
		this.promoteAdded+=bs.promoteAdded;
		this.productAddFailed+=bs.productAddFailed;
		this.priceAddFailed+=bs.priceAddFailed;
		this.promoteAddFailed+=bs.promoteAddFailed;
		
		this.productSkipped+=bs.productSkipped;		
	}
	
	@Override
	public List<BrokenPage> getBPL(){
		List<BrokenPage> bplist = new ArrayList<BrokenPage>();
		BrokenPage bp = null;
		Iterator<String> it = null;
		
		it = getBrokenDetailedPage().iterator();		
		while (it.hasNext()){
			String url = it.next();
			bp = new BrokenPage();
			bp.setUrl(url);
			bp.setType(BrokenPage.TYPE_DETAIL);
			bp.setCount(1);
			bplist.add(bp);
		}
		
		it = getBrokenListPage().iterator();		
		while (it.hasNext()){
			String url = it.next();
			bp = new BrokenPage();
			bp.setUrl(url);
			bp.setType(BrokenPage.TYPE_LIST);
			bp.setCount(1);
			bplist.add(bp);
		}
		
		it = getBrokenPromotionPage().iterator();		
		while (it.hasNext()){
			String url = it.next();
			bp = new BrokenPage();
			bp.setUrl(url);
			bp.setType(BrokenPage.TYPE_PROM);
			bp.setCount(1);
			bplist.add(bp);
		}
		return bplist;
	}
	
	public void clear(){
		this.pageBrowsed=0;
		this.productBrowsed=0;		
		this.promoBrowsed=0;
		this.productScanned=0;
		this.brokenListPage.clear();
		this.brokenDetailedPage.clear();
		this.brokenPromotionPage.clear();
		
		this.productAdded=0;
		this.priceAdded=0;
		this.promoteAdded=0;
		this.productAddFailed=0;
		this.priceAddFailed=0;
		this.promoteAddFailed=0;
		
		this.productSkipped=0;
	}
	
	public String toString(){
		return  "\n"
				+ "page_browsed:" + pageBrowsed + "\n"
				+ "product_browsed:" + productBrowsed + "\n" 
				+ "promotion_browsed:" + promoBrowsed + "\n"
				+ "broken_list_page:" + brokenListPage.size() + "\n"
				+ brokenListPage.toString() + "\n"
				+ "broken_detailed_page:" + brokenDetailedPage.size() + "\n"
				+ brokenDetailedPage.toString() + "\n"
				+ "broken_promo_page:" + brokenPromotionPage.size() + "\n"
				+ brokenPromotionPage.toString() + "\n"				
				+ "product_scanned:" + productScanned + "\n"
				
				+ "product_added:" + productAdded + "\n"
				+ "price_added:" + priceAdded + "\n"
				+ "promte_added:" + promoteAdded + "\n"
				+ "product_added_failed:" + productAddFailed + "\n"
				+ "price_added_failed:" + priceAddFailed + "\n"
				+ "promte_added_failed:" + promoteAddFailed + "\n"
				
				+ "product_skipped:" + productSkipped + "\n"
				;
				
	}
	
	public boolean hasBroken(){
		return (brokenListPage.size()>0 || brokenDetailedPage.size()>0 
				|| brokenPromotionPage.size()>0);
	}

	public Set<String> getBrokenListPage() {
		return brokenListPage;
	}

	public void setBrokenListPage(Set<String> brokenListPage) {
		this.brokenListPage = brokenListPage;
	}

	public Set<String> getBrokenDetailedPage() {
		return brokenDetailedPage;
	}

	public void setBrokenDetailedPage(Set<String> brokenDetailedPage) {
		this.brokenDetailedPage = brokenDetailedPage;
	}

	public Set<String> getBrokenPromotionPage() {
		return brokenPromotionPage;
	}

	public void setBrokenPromotionPage(Set<String> brokenPromotionPage) {
		this.brokenPromotionPage = brokenPromotionPage;
	}

	public int getBrokenListPageNum() {
		return brokenListPage.size();
	}


	public int getBrokenDetailedPageNum() {
		return brokenDetailedPage.size();
	}


	public int getBrokenPromoPageNum() {
		return brokenPromotionPage.size();
	}


	public int getPromoBrowsed() {
		return promoBrowsed;
	}

	public void setPromoBrowsed(int promoBrowsed) {
		this.promoBrowsed = promoBrowsed;
	}

	public int getProductScanned() {
		return productScanned;
	}

	public void setProductScanned(int productScanned) {
		this.productScanned = productScanned;
	}

	public int getProductAdded() {
		return productAdded;
	}

	public void setProductAdded(int productAdded) {
		this.productAdded = productAdded;
	}

	public int getProductAddFailed() {
		return productAddFailed;
	}

	public void setProductAddFailed(int productAddFailed) {
		this.productAddFailed = productAddFailed;
	}

	public int getPriceAdded() {
		return priceAdded;
	}

	public void setPriceAdded(int priceAdded) {
		this.priceAdded = priceAdded;
	}

	public int getPriceAddFailed() {
		return priceAddFailed;
	}

	public void setPriceAddFailed(int priceAddFailed) {
		this.priceAddFailed = priceAddFailed;
	}

	public int getPromoteAdded() {
		return promoteAdded;
	}

	public void setPromoteAdded(int promoteAdded) {
		this.promoteAdded = promoteAdded;
	}

	public int getPromoteAddFailed() {
		return promoteAddFailed;
	}

	public void setPromoteAddFailed(int promoteAddFailed) {
		this.promoteAddFailed = promoteAddFailed;
	}

	public int getProductSkipped() {
		return productSkipped;
	}

	public void setProductSkipped(int productSkipped) {
		this.productSkipped = productSkipped;
	}

	public void setPageBrowsed(int pageBrowsed) {
		this.pageBrowsed = pageBrowsed;
	}

	public void setProductBrowsed(int productBrowsed) {
		this.productBrowsed = productBrowsed;
	}	
}
