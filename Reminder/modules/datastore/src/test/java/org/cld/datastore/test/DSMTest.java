package org.cld.datastore.test;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.datastore.api.DataStoreManager;
import org.cld.datastore.entity.Category;
import org.cld.datastore.entity.CrawledItemId;
import org.cld.datastore.entity.Price;
import org.cld.datastore.entity.Product;

public class DSMTest {
	
	private static Logger logger = LogManager.getLogger(DSMTest.class);
	
	public void generalTest(DataStoreManager manager){
		//1 store, 1 product, 4 price, 1 promotion

		String catId = "1";
		String productId = "abc";
		String storeId = "dd"; // e.g.: dangdang
		String dataSource1 = "dangdang";
		String productTitle = "test book title";
		double originalPrice = 100d;
		double price = 90d;
		String promotionId = "100minus10";
		String promotionTitle = "100 to 90";

		CrawledItemId cid = new CrawledItemId(catId, storeId, new Date());
		Category c = new Category(cid, "default");
		manager.addCrawledItem(c, null, null);
		
		CrawledItemId pid = new CrawledItemId(productId, storeId, new Date());
		Product product = new Product(pid, "default", productTitle, originalPrice, price);
		manager.addCrawledItem(product, null, null);

		Product p = (Product) manager.getCrawledItem(productId, storeId, Product.class);
		assertTrue(p.getOriginalPrice()==product.getOriginalPrice());

		Price priceHist = new Price(productId, new Date(), storeId, price, promotionId);
		manager.addPrice(priceHist);

		int timeLapse=500;
		for (int i = 0; i < 3; i++) {
			Price newPrice = new Price(productId, new Date(
					System.currentTimeMillis() + timeLapse * i), storeId, price + i, promotionId);
			manager.addPrice(newPrice);
			try {
				Thread.sleep(timeLapse);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Price latestPrice = manager.getLatestPrice(productId, storeId);
		logger.info(latestPrice);
	}
}
