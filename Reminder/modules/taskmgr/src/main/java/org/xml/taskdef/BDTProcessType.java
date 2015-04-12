//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.11 at 11:04:17 AM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BDTProcessType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BDTProcessType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="inline"/>
 *     &lt;enumeration value="genbpt"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BDTProcessType")
@XmlEnum
public enum BDTProcessType {

    @XmlEnumValue("inline")
    INLINE("inline"),
    @XmlEnumValue("genbpt")
    GENBPT("genbpt");
    private final String value;

    BDTProcessType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BDTProcessType fromValue(String v) {
        for (BDTProcessType c: BDTProcessType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
