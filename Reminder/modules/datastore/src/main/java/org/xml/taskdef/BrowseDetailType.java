//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.06 at 03:54:32 PM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrowseDetailType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrowseDetailType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="baseBrowseTask" type="{}BrowseTaskType"/>
 *         &lt;element name="firstPageClickStream" type="{}ClickStreamType" minOccurs="0"/>
 *         &lt;element name="totalPage" type="{}ValueType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nextPage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tryPattern" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="lastPageCondition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="monitorPrice" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="processType" type="{}BDTProcessType" default="inline" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowseDetailType", propOrder = {
    "baseBrowseTask",
    "firstPageClickStream",
    "totalPage"
})
public class BrowseDetailType {

    @XmlElement(required = true)
    protected BrowseTaskType baseBrowseTask;
    protected ClickStreamType firstPageClickStream;
    protected ValueType totalPage;
    @XmlAttribute(name = "nextPage")
    protected String nextPage;
    @XmlAttribute(name = "tryPattern")
    protected Boolean tryPattern;
    @XmlAttribute(name = "lastPageCondition")
    protected String lastPageCondition;
    @XmlAttribute(name = "monitorPrice")
    protected Boolean monitorPrice;
    @XmlAttribute(name = "processType")
    protected BDTProcessType processType;

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
     * Gets the value of the firstPageClickStream property.
     * 
     * @return
     *     possible object is
     *     {@link ClickStreamType }
     *     
     */
    public ClickStreamType getFirstPageClickStream() {
        return firstPageClickStream;
    }

    /**
     * Sets the value of the firstPageClickStream property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClickStreamType }
     *     
     */
    public void setFirstPageClickStream(ClickStreamType value) {
        this.firstPageClickStream = value;
    }

    /**
     * Gets the value of the totalPage property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getTotalPage() {
        return totalPage;
    }

    /**
     * Sets the value of the totalPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setTotalPage(ValueType value) {
        this.totalPage = value;
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
     * Gets the value of the tryPattern property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isTryPattern() {
        if (tryPattern == null) {
            return true;
        } else {
            return tryPattern;
        }
    }

    /**
     * Sets the value of the tryPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTryPattern(Boolean value) {
        this.tryPattern = value;
    }

    /**
     * Gets the value of the lastPageCondition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastPageCondition() {
        return lastPageCondition;
    }

    /**
     * Sets the value of the lastPageCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastPageCondition(String value) {
        this.lastPageCondition = value;
    }

    /**
     * Gets the value of the monitorPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isMonitorPrice() {
        if (monitorPrice == null) {
            return false;
        } else {
            return monitorPrice;
        }
    }

    /**
     * Sets the value of the monitorPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMonitorPrice(Boolean value) {
        this.monitorPrice = value;
    }

    /**
     * Gets the value of the processType property.
     * 
     * @return
     *     possible object is
     *     {@link BDTProcessType }
     *     
     */
    public BDTProcessType getProcessType() {
        if (processType == null) {
            return BDTProcessType.INLINE;
        } else {
            return processType;
        }
    }

    /**
     * Sets the value of the processType property.
     * 
     * @param value
     *     allowed object is
     *     {@link BDTProcessType }
     *     
     */
    public void setProcessType(BDTProcessType value) {
        this.processType = value;
    }

}
