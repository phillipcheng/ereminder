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
 * <p>Java class for ClickType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClickType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="input" type="{}AttributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="print" type="{}AttributeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nextpage" type="{}ConditionalNextPage"/>
 *       &lt;/sequence>
 *       &lt;attribute name="pageName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClickType", propOrder = {
    "input",
    "print",
    "nextpage"
})
public class ClickType {

    protected List<AttributeType> input;
    protected List<AttributeType> print;
    @XmlElement(required = true)
    protected ConditionalNextPage nextpage;
    @XmlAttribute(name = "pageName")
    protected String pageName;

    /**
     * Gets the value of the input property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeType }
     * 
     * 
     */
    public List<AttributeType> getInput() {
        if (input == null) {
            input = new ArrayList<AttributeType>();
        }
        return this.input;
    }

    /**
     * Gets the value of the print property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the print property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeType }
     * 
     * 
     */
    public List<AttributeType> getPrint() {
        if (print == null) {
            print = new ArrayList<AttributeType>();
        }
        return this.print;
    }

    /**
     * Gets the value of the nextpage property.
     * 
     * @return
     *     possible object is
     *     {@link ConditionalNextPage }
     *     
     */
    public ConditionalNextPage getNextpage() {
        return nextpage;
    }

    /**
     * Sets the value of the nextpage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionalNextPage }
     *     
     */
    public void setNextpage(ConditionalNextPage value) {
        this.nextpage = value;
    }

    /**
     * Gets the value of the pageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPageName() {
        return pageName;
    }

    /**
     * Sets the value of the pageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPageName(String value) {
        this.pageName = value;
    }

}
