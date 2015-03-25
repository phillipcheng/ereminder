//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.24 at 07:43:42 AM PST 
//


package org.xml.taskdef;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For each click/link, first do some input assignment, then click the xpath typed nextpage and return a page.		
 * 
 * <p>Java class for ClickStreamType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClickStreamType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="link" type="{}ClickType" maxOccurs="unbounded"/>
 *         &lt;element name="finishCondition" type="{}BinaryBoolOp" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClickStreamType", propOrder = {
    "link",
    "finishCondition"
})
public class ClickStreamType {

    @XmlElement(required = true)
    protected List<ClickType> link;
    protected BinaryBoolOp finishCondition;

    /**
     * Gets the value of the link property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the link property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClickType }
     * 
     * 
     */
    public List<ClickType> getLink() {
        if (link == null) {
            link = new ArrayList<ClickType>();
        }
        return this.link;
    }

    /**
     * Gets the value of the finishCondition property.
     * 
     * @return
     *     possible object is
     *     {@link BinaryBoolOp }
     *     
     */
    public BinaryBoolOp getFinishCondition() {
        return finishCondition;
    }

    /**
     * Sets the value of the finishCondition property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinaryBoolOp }
     *     
     */
    public void setFinishCondition(BinaryBoolOp value) {
        this.finishCondition = value;
    }

}
