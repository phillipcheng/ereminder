//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.10 at 09:27:35 AM PDT 
//


package org.xml.taskdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrowseTaskType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrowseTaskType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sampleUrl" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="param" type="{}ParamType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="startUrl" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="cachePage" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="userAttribute" type="{}AttributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pageVerify" type="{}BinaryBoolOp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="idUrlMapping" type="{}RegExpType" minOccurs="0"/>
 *         &lt;element name="idUrlMappingFirstPage" type="{}RegExpType" minOccurs="0"/>
 *         &lt;element name="itemName" type="{}ValueType" minOccurs="0"/>
 *         &lt;element name="csvtransform" type="{}CsvTransformType" minOccurs="0"/>
 *         &lt;element name="nextTask" type="{}ConditionalNextTask" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="taskName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isStart" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="rerunInterim" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="enableJS" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="skipJS" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dsm">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="hdfs"/>
 *             &lt;enumeration value="hbase"/>
 *             &lt;enumeration value="hibernate"/>
 *             &lt;enumeration value="nothing"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="dsmHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="mbMemoryNeeded" type="{http://www.w3.org/2001/XMLSchema}int" default="1024" />
 *       &lt;attribute name="taskNumPerJob" type="{http://www.w3.org/2001/XMLSchema}int" default="10" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrowseTaskType", propOrder = {
    "sampleUrl",
    "param",
    "startUrl",
    "cachePage",
    "userAttribute",
    "pageVerify",
    "idUrlMapping",
    "idUrlMappingFirstPage",
    "itemName",
    "csvtransform",
    "nextTask"
})
public class BrowseTaskType {

    protected List<String> sampleUrl;
    protected List<ParamType> param;
    protected ValueType startUrl;
    protected ValueType cachePage;
    protected List<AttributeType> userAttribute;
    protected List<BinaryBoolOp> pageVerify;
    protected RegExpType idUrlMapping;
    protected RegExpType idUrlMappingFirstPage;
    protected ValueType itemName;
    protected CsvTransformType csvtransform;
    protected ConditionalNextTask nextTask;
    @XmlAttribute(name = "taskName")
    protected String taskName;
    @XmlAttribute(name = "isStart")
    protected Boolean isStart;
    @XmlAttribute(name = "rerunInterim")
    protected Integer rerunInterim;
    @XmlAttribute(name = "enableJS")
    protected Boolean enableJS;
    @XmlAttribute(name = "skipJS")
    protected String skipJS;
    @XmlAttribute(name = "dsm")
    protected String dsm;
    @XmlAttribute(name = "dsmHeader")
    protected Boolean dsmHeader;
    @XmlAttribute(name = "mbMemoryNeeded")
    protected Integer mbMemoryNeeded;
    @XmlAttribute(name = "taskNumPerJob")
    protected Integer taskNumPerJob;

    /**
     * Gets the value of the sampleUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sampleUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSampleUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSampleUrl() {
        if (sampleUrl == null) {
            sampleUrl = new ArrayList<String>();
        }
        return this.sampleUrl;
    }

    /**
     * Gets the value of the param property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamType }
     * 
     * 
     */
    public List<ParamType> getParam() {
        if (param == null) {
            param = new ArrayList<ParamType>();
        }
        return this.param;
    }

    /**
     * Gets the value of the startUrl property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getStartUrl() {
        return startUrl;
    }

    /**
     * Sets the value of the startUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setStartUrl(ValueType value) {
        this.startUrl = value;
    }

    /**
     * Gets the value of the cachePage property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getCachePage() {
        return cachePage;
    }

    /**
     * Sets the value of the cachePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setCachePage(ValueType value) {
        this.cachePage = value;
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
     * Gets the value of the pageVerify property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pageVerify property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPageVerify().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BinaryBoolOp }
     * 
     * 
     */
    public List<BinaryBoolOp> getPageVerify() {
        if (pageVerify == null) {
            pageVerify = new ArrayList<BinaryBoolOp>();
        }
        return this.pageVerify;
    }

    /**
     * Gets the value of the idUrlMapping property.
     * 
     * @return
     *     possible object is
     *     {@link RegExpType }
     *     
     */
    public RegExpType getIdUrlMapping() {
        return idUrlMapping;
    }

    /**
     * Sets the value of the idUrlMapping property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegExpType }
     *     
     */
    public void setIdUrlMapping(RegExpType value) {
        this.idUrlMapping = value;
    }

    /**
     * Gets the value of the idUrlMappingFirstPage property.
     * 
     * @return
     *     possible object is
     *     {@link RegExpType }
     *     
     */
    public RegExpType getIdUrlMappingFirstPage() {
        return idUrlMappingFirstPage;
    }

    /**
     * Sets the value of the idUrlMappingFirstPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegExpType }
     *     
     */
    public void setIdUrlMappingFirstPage(RegExpType value) {
        this.idUrlMappingFirstPage = value;
    }

    /**
     * Gets the value of the itemName property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getItemName() {
        return itemName;
    }

    /**
     * Sets the value of the itemName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setItemName(ValueType value) {
        this.itemName = value;
    }

    /**
     * Gets the value of the csvtransform property.
     * 
     * @return
     *     possible object is
     *     {@link CsvTransformType }
     *     
     */
    public CsvTransformType getCsvtransform() {
        return csvtransform;
    }

    /**
     * Sets the value of the csvtransform property.
     * 
     * @param value
     *     allowed object is
     *     {@link CsvTransformType }
     *     
     */
    public void setCsvtransform(CsvTransformType value) {
        this.csvtransform = value;
    }

    /**
     * Gets the value of the nextTask property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionalNextTask }
     *     
     */
    public ConditionalNextTask getNextTask() {
        return nextTask;
    }

    /**
     * Sets the value of the nextTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionalNextTask }
     *     
     */
    public void setNextTask(ConditionalNextTask value) {
        this.nextTask = value;
    }

    /**
     * Gets the value of the taskName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets the value of the taskName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaskName(String value) {
        this.taskName = value;
    }

    /**
     * Gets the value of the isStart property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsStart() {
        if (isStart == null) {
            return true;
        } else {
            return isStart;
        }
    }

    /**
     * Sets the value of the isStart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsStart(Boolean value) {
        this.isStart = value;
    }

    /**
     * Gets the value of the rerunInterim property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRerunInterim() {
        return rerunInterim;
    }

    /**
     * Sets the value of the rerunInterim property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRerunInterim(Integer value) {
        this.rerunInterim = value;
    }

    /**
     * Gets the value of the enableJS property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnableJS() {
        if (enableJS == null) {
            return false;
        } else {
            return enableJS;
        }
    }

    /**
     * Sets the value of the enableJS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnableJS(Boolean value) {
        this.enableJS = value;
    }

    /**
     * Gets the value of the skipJS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkipJS() {
        return skipJS;
    }

    /**
     * Sets the value of the skipJS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkipJS(String value) {
        this.skipJS = value;
    }

    /**
     * Gets the value of the dsm property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDsm() {
        return dsm;
    }

    /**
     * Sets the value of the dsm property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDsm(String value) {
        this.dsm = value;
    }

    /**
     * Gets the value of the dsmHeader property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDsmHeader() {
        if (dsmHeader == null) {
            return false;
        } else {
            return dsmHeader;
        }
    }

    /**
     * Sets the value of the dsmHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDsmHeader(Boolean value) {
        this.dsmHeader = value;
    }

    /**
     * Gets the value of the mbMemoryNeeded property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMbMemoryNeeded() {
        if (mbMemoryNeeded == null) {
            return  1024;
        } else {
            return mbMemoryNeeded;
        }
    }

    /**
     * Sets the value of the mbMemoryNeeded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMbMemoryNeeded(Integer value) {
        this.mbMemoryNeeded = value;
    }

    /**
     * Gets the value of the taskNumPerJob property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getTaskNumPerJob() {
        if (taskNumPerJob == null) {
            return  10;
        } else {
            return taskNumPerJob;
        }
    }

    /**
     * Sets the value of the taskNumPerJob property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTaskNumPerJob(Integer value) {
        this.taskNumPerJob = value;
    }

}
