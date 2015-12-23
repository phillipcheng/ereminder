package org.cld.datacrawl;

import org.cld.util.entity.Product;


public class ProductConf {
	
	public static String productEntityImpl_Key="entity.impl";
	public static String productHandlerImpl_Key="handler.impl";
	
	private String name;
	private String entityImpl;//entity class name
	private String handlerClassName;//handler class name
	private Class<Product> productClass;
	private ProductHandler prdHandler; //thread unsafe handler instance

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getEntityImpl() {
		return entityImpl;
	}

	public void setEntityImpl(String entityImpl) {
		this.entityImpl = entityImpl;
	}

	public Class<Product> getProductClass() {
		return productClass;
	}

	public void setProductClass(Class<Product> productClass) {
		this.productClass = productClass;
	}

	public String toString(){
		return "name:" + name + "\n" +
				"entityImpl:" + entityImpl + "\n" +
				"productClass:" + productClass + "\n" + 
				"productHandler:" + prdHandler;
	}

	public ProductHandler getPrdHandler() {
		return prdHandler;
	}

	public void setPrdHandler(ProductHandler prdHandler) {
		this.prdHandler = prdHandler;
	}

	public String getHandlerClassName() {
		return handlerClassName;
	}

	public void setHandlerClassName(String handlerClassName) {
		this.handlerClassName = handlerClassName;
	}
	

}
