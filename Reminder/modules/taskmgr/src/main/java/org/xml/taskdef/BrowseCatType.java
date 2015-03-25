//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.24 at 07:43:42 AM PST 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrowseCatType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrowseCatType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="baseBrowseTask" type="{}BrowseTaskType"/>
 *         &lt;element name="subItemList" type="{}SubListType"/>
 *         &lt;element name="totalPageNum" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="itemPerPage" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="totalItemNum" type="{}ValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isLeaf" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="splitTaskType" type="{}SplitTaskByType" default="page" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowseCatType", propOrder = {
    "baseBrowseTask",
    "subItemList",
    "totalPageNum",
    "itemPerPage",
    "totalItemNum"
})
public class BrowseCatType {

    @XmlElement(required = true)
    protected BrowseTaskType baseBrowseTask;
    @XmlElement(required = true)
    protected SubListType subItemList;
    protected ValueType totalPageNum;
    protected ValueType itemPerPage;
    protected ValueType totalItemNum;
    @XmlAttribute(name = "isLeaf", required = true)
    protected boolean isLeaf;
    @XmlAttribute(name = "splitTaskType")
    protected SplitTaskByType splitTaskType;

    /**
     * Gets the value of the baseBrowseTask property.
     * 
     * @return
     *     possible object is
     *     {@link BrowseTaskType }
     *     
     */
    public BrowseTaskType getBaseBrowseTask() {
        return baseBrowseTask;
    }

    /**
     * Sets the value of the baseBrowseTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrowseTaskType }
     *     
     */
    public void setBaseBrowseTask(BrowseTaskType value) {
        this.baseBrowseTask = value;
    }

    /**
     * Gets the value of the subItemList property.
     * 
     * @return
     *     possible object is
     *     {@link SubListType }
     *     
     */
    public SubListType getSubItemList() {
        return subItemList;
    }

    /**
     * Sets the value of the subItemList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubListType }
     *     
     */
    public void setSubItemList(SubListType value) {
        this.subItemList = value;
    }

    /**
     * Gets the value of the totalPageNum property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getTotalPageNum() {
        return totalPageNum;
    }

    /**
     * Sets the value of the totalPageNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setTotalPageNum(ValueType value) {
        this.totalPageNum = value;
    }

    /**
     * Gets the value of the itemPerPage property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getItemPerPage() {
        return itemPerPage;
    }

    /**
     * Sets the value of the itemPerPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setItemPerPage(ValueType value) {
        this.itemPerPage = value;
    }

    /**
     * Gets the value of the totalItemNum property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getTotalItemNum() {
        return totalItemNum;
    }

    /**
     * Sets the value of the totalItemNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setTotalItemNum(ValueType value) {
        this.totalItemNum = value;
    }

    /**
     * Gets the value of the isLeaf property.
     * 
     */
    public boolean isIsLeaf() {
        return isLeaf;
    }

    /**
     * Sets the value of the isLeaf property.
     * 
     */
    public void setIsLeaf(boolean value) {
        this.isLeaf = value;
    }

    /**
     * Gets the value of the splitTaskType property.
     * 
     * @return
     *     possible object is
     *     {@link SplitTaskByType }
     *     
     */
    public SplitTaskByType getSplitTaskType() {
        if (splitTaskType == null) {
            return SplitTaskByType.PAGE;
        } else {
            return splitTaskType;
        }
    }

    /**
     * Sets the value of the splitTaskType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SplitTaskByType }
     *     
     */
    public void setSplitTaskType(SplitTaskByType value) {
        this.splitTaskType = value;
    }

}
