//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.11 at 11:04:17 AM PDT 
//


package org.xml.taskdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TasksType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TasksType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loginInfo" type="{}LoginType" minOccurs="0"/>
 *         &lt;element name="skipUrl" maxOccurs="unbounded" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CatTask" type="{}BrowseCatType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="PrdTask" type="{}BrowseDetailType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="invokeTask" type="{}TaskInvokeType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="storeId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rootVolume" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="maxThread" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="productType" default="book">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="book"/>
 *             &lt;enumeration value="default"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TasksType", propOrder = {
    "loginInfo",
    "skipUrl",
    "catTask",
    "prdTask",
    "invokeTask"
})
public class TasksType {

    protected LoginType loginInfo;
    protected List<String> skipUrl;
    @XmlElement(name = "CatTask")
    protected List<BrowseCatType> catTask;
    @XmlElement(name = "PrdTask")
    protected List<BrowseDetailType> prdTask;
    protected List<TaskInvokeType> invokeTask;
    @XmlAttribute(name = "storeId", required = true)
    protected String storeId;
    @XmlAttribute(name = "rootVolume", required = true)
    protected String rootVolume;
    @XmlAttribute(name = "maxThread")
    protected Integer maxThread;
    @XmlAttribute(name = "productType")
    protected String productType;

    /**
     * Gets the value of the loginInfo property.
     * 
     * @return
     *     possible object is
     *     {@link LoginType }
     *     
     */
    public LoginType getLoginInfo() {
        return loginInfo;
    }

    /**
     * Sets the value of the loginInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoginType }
     *     
     */
    public void setLoginInfo(LoginType value) {
        this.loginInfo = value;
    }

    /**
     * Gets the value of the skipUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the skipUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkipUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSkipUrl() {
        if (skipUrl == null) {
            skipUrl = new ArrayList<String>();
        }
        return this.skipUrl;
    }

    /**
     * Gets the value of the catTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the catTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCatTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BrowseCatType }
     * 
     * 
     */
    public List<BrowseCatType> getCatTask() {
        if (catTask == null) {
            catTask = new ArrayList<BrowseCatType>();
        }
        return this.catTask;
    }

    /**
     * Gets the value of the prdTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prdTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrdTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BrowseDetailType }
     * 
     * 
     */
    public List<BrowseDetailType> getPrdTask() {
        if (prdTask == null) {
            prdTask = new ArrayList<BrowseDetailType>();
        }
        return this.prdTask;
    }

    /**
     * Gets the value of the invokeTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the invokeTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInvokeTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskInvokeType }
     * 
     * 
     */
    public List<TaskInvokeType> getInvokeTask() {
        if (invokeTask == null) {
            invokeTask = new ArrayList<TaskInvokeType>();
        }
        return this.invokeTask;
    }

    /**
     * Gets the value of the storeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * Sets the value of the storeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreId(String value) {
        this.storeId = value;
    }

    /**
     * Gets the value of the rootVolume property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRootVolume() {
        return rootVolume;
    }

    /**
     * Sets the value of the rootVolume property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRootVolume(String value) {
        this.rootVolume = value;
    }

    /**
     * Gets the value of the maxThread property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMaxThread() {
        if (maxThread == null) {
            return  0;
        } else {
            return maxThread;
        }
    }

    /**
     * Sets the value of the maxThread property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxThread(Integer value) {
        this.maxThread = value;
    }

    /**
     * Gets the value of the productType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductType() {
        if (productType == null) {
            return "book";
        } else {
            return productType;
        }
    }

    /**
     * Sets the value of the productType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductType(String value) {
        this.productType = value;
    }

}
