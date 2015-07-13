//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.13 at 06:52:06 AM PDT 
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
 * <p>Java class for SubListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SubListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="itemList" type="{}ValueType"/>
 *         &lt;element name="userAttribute" type="{}AttributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="taskInvokes" type="{}TaskInvokeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="lastPageCondition" type="{}BinaryBoolOp" minOccurs="0"/>
 *         &lt;element name="lastScreenCondition" type="{}BinaryBoolOp" minOccurs="0"/>
 *         &lt;element name="name" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="itemFullUrl" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="itemFullUrlClicks" type="{}ClickStreamType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nextPage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isLeaf" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="lastItem" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nextScreen" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubListType", propOrder = {
    "itemList",
    "userAttribute",
    "taskInvokes",
    "lastPageCondition",
    "lastScreenCondition",
    "name",
    "itemFullUrl",
    "itemFullUrlClicks"
})
public class SubListType {

    @XmlElement(required = true)
    protected ValueType itemList;
    protected List<AttributeType> userAttribute;
    protected List<TaskInvokeType> taskInvokes;
    protected BinaryBoolOp lastPageCondition;
    protected BinaryBoolOp lastScreenCondition;
    protected ValueType name;
    protected ValueType itemFullUrl;
    protected ClickStreamType itemFullUrlClicks;
    @XmlAttribute(name = "nextPage")
    protected String nextPage;
    @XmlAttribute(name = "isLeaf")
    protected Boolean isLeaf;
    @XmlAttribute(name = "lastItem")
    protected String lastItem;
    @XmlAttribute(name = "nextScreen")
    protected String nextScreen;

    /**
     * Gets the value of the itemList property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getItemList() {
        return itemList;
    }

    /**
     * Sets the value of the itemList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setItemList(ValueType value) {
        this.itemList = value;
    }

    /**
     * Gets the value of the userAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeType }
     * 
     * 
     */
    public List<AttributeType> getUserAttribute() {
        if (userAttribute == null) {
            userAttribute = new ArrayList<AttributeType>();
        }
        return this.userAttribute;
    }

    /**
     * Gets the value of the taskInvokes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskInvokes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskInvokes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskInvokeType }
     * 
     * 
     */
    public List<TaskInvokeType> getTaskInvokes() {
        if (taskInvokes == null) {
            taskInvokes = new ArrayList<TaskInvokeType>();
        }
        return this.taskInvokes;
    }

    /**
     * Gets the value of the lastPageCondition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getLastPageCondition() {
        return lastPageCondition;
    }

    /**
     * Sets the value of the lastPageCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setLastPageCondition(BinaryBoolOp value) {
        this.lastPageCondition = value;
    }

    /**
     * Gets the value of the lastScreenCondition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getLastScreenCondition() {
        return lastScreenCondition;
    }

    /**
     * Sets the value of the lastScreenCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setLastScreenCondition(BinaryBoolOp value) {
        this.lastScreenCondition = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setName(ValueType value) {
        this.name = value;
    }

    /**
     * Gets the value of the itemFullUrl property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getItemFullUrl() {
        return itemFullUrl;
    }

    /**
     * Sets the value of the itemFullUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setItemFullUrl(ValueType value) {
        this.itemFullUrl = value;
    }

    /**
     * Gets the value of the itemFullUrlClicks property.
     * 
     * @return
     *     possible object is
     *     {@link ClickStreamType }
     *     
     */
    public ClickStreamType getItemFullUrlClicks() {
        return itemFullUrlClicks;
    }

    /**
     * Sets the value of the itemFullUrlClicks property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClickStreamType }
     *     
     */
    public void setItemFullUrlClicks(ClickStreamType value) {
        this.itemFullUrlClicks = value;
    }

    /**
     * Gets the value of the nextPage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextPage() {
        return nextPage;
    }

    /**
     * Sets the value of the nextPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextPage(String value) {
        this.nextPage = value;
    }

    /**
     * Gets the value of the isLeaf property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsLeaf() {
        if (isLeaf == null) {
            return true;
        } else {
            return isLeaf;
        }
    }

    /**
     * Sets the value of the isLeaf property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsLeaf(Boolean value) {
        this.isLeaf = value;
    }

    /**
     * Gets the value of the lastItem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastItem() {
        return lastItem;
    }

    /**
     * Sets the value of the lastItem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastItem(String value) {
        this.lastItem = value;
    }

    /**
     * Gets the value of the nextScreen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNextScreen() {
        return nextScreen;
    }

    /**
     * Sets the value of the nextScreen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNextScreen(String value) {
        this.nextScreen = value;
    }

}
