//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.28 at 02:10:17 PM PST 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * redirect to the specified clickstream depends on the landing condition/page
 * 
 * <p>Java class for RedirectType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RedirectType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="landing" type="{}ValueType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="clickstream" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RedirectType", propOrder = {
    "landing"
})
public class RedirectType {

    @XmlElement(required = true)
    protected ValueType landing;
    @XmlAttribute(name = "clickstream")
    protected String clickstream;

    /**
     * Gets the value of the landing property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getLanding() {
        return landing;
    }

    /**
     * Sets the value of the landing property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setLanding(ValueType value) {
        this.landing = value;
    }

    /**
     * Gets the value of the clickstream property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClickstream() {
        return clickstream;
    }

    /**
     * Sets the value of the clickstream property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClickstream(String value) {
        this.clickstream = value;
    }

}
