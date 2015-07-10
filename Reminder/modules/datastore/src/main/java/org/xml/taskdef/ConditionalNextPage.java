//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.09 at 08:49:35 PM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * find the next page according to result of condition. eval the value part, if not defined means the current page, if defined (xpath), follow it, then lookup the click whose pagename equals to the nextpage's name, if found, click control goes there, if not, finished.
 * 
 * 
 * <p>Java class for ConditionalNextPage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionalNextPage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="condition" type="{}BinaryBoolOp" minOccurs="0"/>
 *         &lt;element name="failNextPage" type="{}AttributeType" minOccurs="0"/>
 *         &lt;element name="successNextPage" type="{}AttributeType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="waitTime" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionalNextPage", propOrder = {
    "condition",
    "failNextPage",
    "successNextPage"
})
public class ConditionalNextPage {

    protected BinaryBoolOp condition;
    protected AttributeType failNextPage;
    @XmlElement(required = true)
    protected AttributeType successNextPage;
    @XmlAttribute(name = "waitTime")
    protected Integer waitTime;

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setCondition(BinaryBoolOp value) {
        this.condition = value;
    }

    /**
     * Gets the value of the failNextPage property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeType }
     *     
     */
    public AttributeType getFailNextPage() {
        return failNextPage;
    }

    /**
     * Sets the value of the failNextPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeType }
     *     
     */
    public void setFailNextPage(AttributeType value) {
        this.failNextPage = value;
    }

    /**
     * Gets the value of the successNextPage property.
     * 
     * @return
     *     possible object is
     *     {@link AttributeType }
     *     
     */
    public AttributeType getSuccessNextPage() {
        return successNextPage;
    }

    /**
     * Sets the value of the successNextPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeType }
     *     
     */
    public void setSuccessNextPage(AttributeType value) {
        this.successNextPage = value;
    }

    /**
     * Gets the value of the waitTime property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getWaitTime() {
        return waitTime;
    }

    /**
     * Sets the value of the waitTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setWaitTime(Integer value) {
        this.waitTime = value;
    }

}
