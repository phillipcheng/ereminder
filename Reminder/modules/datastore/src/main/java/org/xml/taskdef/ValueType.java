//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.14 at 05:57:21 PM PDT 
//


package org.xml.taskdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValueType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="strPreprocess" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="trimPre" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="trimPost" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="sampleUrl" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pageVerify" type="{}BinaryBoolOp" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="fromScope" type="{}ScopeType" />
 *       &lt;attribute name="fromType" type="{}VarType" />
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="toType" type="{}VarType" />
 *       &lt;attribute name="toEntryType" type="{}VarType" default="string" />
 *       &lt;attribute name="toDirectory" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="basePage" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="enableJS" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="format" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueType", propOrder = {
    "strPreprocess",
    "sampleUrl",
    "pageVerify"
})
public class ValueType {

    protected ValueType.StrPreprocess strPreprocess;
    protected List<String> sampleUrl;
    protected List<BinaryBoolOp> pageVerify;
    @XmlAttribute(name = "fromScope")
    protected ScopeType fromScope;
    @XmlAttribute(name = "fromType")
    protected VarType fromType;
    @XmlAttribute(name = "value", required = true)
    protected String value;
    @XmlAttribute(name = "toType")
    protected VarType toType;
    @XmlAttribute(name = "toEntryType")
    protected VarType toEntryType;
    @XmlAttribute(name = "toDirectory")
    @XmlSchemaType(name = "anySimpleType")
    protected String toDirectory;
    @XmlAttribute(name = "basePage")
    protected String basePage;
    @XmlAttribute(name = "enableJS")
    protected Boolean enableJS;
    @XmlAttribute(name = "format")
    protected String format;

    /**
     * Gets the value of the strPreprocess property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType.StrPreprocess }
     *     
     */
    public ValueType.StrPreprocess getStrPreprocess() {
        return strPreprocess;
    }

    /**
     * Sets the value of the strPreprocess property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType.StrPreprocess }
     *     
     */
    public void setStrPreprocess(ValueType.StrPreprocess value) {
        this.strPreprocess = value;
    }

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
     * Gets the value of the fromScope property.
     * 
     * @return
     *     possible object is
     *     {@link ScopeType }
     *     
     */
    public ScopeType getFromScope() {
        return fromScope;
    }

    /**
     * Sets the value of the fromScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScopeType }
     *     
     */
    public void setFromScope(ScopeType value) {
        this.fromScope = value;
    }

    /**
     * Gets the value of the fromType property.
     * 
     * @return
     *     possible object is
     *     {@link VarType }
     *     
     */
    public VarType getFromType() {
        return fromType;
    }

    /**
     * Sets the value of the fromType property.
     * 
     * @param value
     *     allowed object is
     *     {@link VarType }
     *     
     */
    public void setFromType(VarType value) {
        this.fromType = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the toType property.
     * 
     * @return
     *     possible object is
     *     {@link VarType }
     *     
     */
    public VarType getToType() {
        return toType;
    }

    /**
     * Sets the value of the toType property.
     * 
     * @param value
     *     allowed object is
     *     {@link VarType }
     *     
     */
    public void setToType(VarType value) {
        this.toType = value;
    }

    /**
     * Gets the value of the toEntryType property.
     * 
     * @return
     *     possible object is
     *     {@link VarType }
     *     
     */
    public VarType getToEntryType() {
        if (toEntryType == null) {
            return VarType.STRING;
        } else {
            return toEntryType;
        }
    }

    /**
     * Sets the value of the toEntryType property.
     * 
     * @param value
     *     allowed object is
     *     {@link VarType }
     *     
     */
    public void setToEntryType(VarType value) {
        this.toEntryType = value;
    }

    /**
     * Gets the value of the toDirectory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToDirectory() {
        return toDirectory;
    }

    /**
     * Sets the value of the toDirectory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToDirectory(String value) {
        this.toDirectory = value;
    }

    /**
     * Gets the value of the basePage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasePage() {
        return basePage;
    }

    /**
     * Sets the value of the basePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasePage(String value) {
        this.basePage = value;
    }

    /**
     * Gets the value of the enableJS property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isEnableJS() {
        return enableJS;
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
     * Gets the value of the format property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the value of the format property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormat(String value) {
        this.format = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="trimPre" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="trimPost" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class StrPreprocess {

        @XmlAttribute(name = "trimPre")
        protected String trimPre;
        @XmlAttribute(name = "trimPost")
        protected String trimPost;

        /**
         * Gets the value of the trimPre property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTrimPre() {
            return trimPre;
        }

        /**
         * Sets the value of the trimPre property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTrimPre(String value) {
            this.trimPre = value;
        }

        /**
         * Gets the value of the trimPost property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTrimPost() {
            return trimPost;
        }

        /**
         * Sets the value of the trimPost property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTrimPost(String value) {
            this.trimPost = value;
        }

    }

}
