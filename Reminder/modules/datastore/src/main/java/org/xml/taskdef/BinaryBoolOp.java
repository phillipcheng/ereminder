//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.13 at 02:43:53 PM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BinaryBoolOp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BinaryBoolOp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="lhs" type="{}ValueType"/>
 *         &lt;element name="rhs" type="{}ValueType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="operator" use="required" type="{}OpType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryBoolOp", propOrder = {
    "lhs",
    "rhs"
})
public class BinaryBoolOp {

    @XmlElement(required = true)
    protected ValueType lhs;
    @XmlElement(required = true)
    protected ValueType rhs;
    @XmlAttribute(name = "operator", required = true)
    protected OpType operator;

    /**
     * Gets the value of the lhs property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getLhs() {
        return lhs;
    }

    /**
     * Sets the value of the lhs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setLhs(ValueType value) {
        this.lhs = value;
    }

    /**
     * Gets the value of the rhs property.
     * 
     * @return
     *     possible object is
     *     {@link ValueType }
     *     
     */
    public ValueType getRhs() {
        return rhs;
    }

    /**
     * Sets the value of the rhs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValueType }
     *     
     */
    public void setRhs(ValueType value) {
        this.rhs = value;
    }

    /**
     * Gets the value of the operator property.
     * 
     * @return
     *     possible object is
     *     {@link OpType }
     *     
     */
    public OpType getOperator() {
        return operator;
    }

    /**
     * Sets the value of the operator property.
     * 
     * @param value
     *     allowed object is
     *     {@link OpType }
     *     
     */
    public void setOperator(OpType value) {
        this.operator = value;
    }

}
