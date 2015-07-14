//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.13 at 02:35:38 PM PDT 
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
 * <p>Java class for TaskInvokeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskInvokeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="param" type="{}ParamValueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="myTaskName" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="toCallTaskName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskInvokeType", propOrder = {
    "param"
})
public class TaskInvokeType {

    protected List<ParamValueType> param;
    @XmlAttribute(name = "myTaskName")
    @XmlSchemaType(name = "anySimpleType")
    protected String myTaskName;
    @XmlAttribute(name = "toCallTaskName")
    protected String toCallTaskName;

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
     * {@link ParamValueType }
     * 
     * 
     */
    public List<ParamValueType> getParam() {
        if (param == null) {
            param = new ArrayList<ParamValueType>();
        }
        return this.param;
    }

    /**
     * Gets the value of the myTaskName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyTaskName() {
        return myTaskName;
    }

    /**
     * Sets the value of the myTaskName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyTaskName(String value) {
        this.myTaskName = value;
    }

    /**
     * Gets the value of the toCallTaskName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToCallTaskName() {
        return toCallTaskName;
    }

    /**
     * Sets the value of the toCallTaskName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToCallTaskName(String value) {
        this.toCallTaskName = value;
    }

}
