//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.04 at 10:34:23 PM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ScopeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ScopeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="const"/>
 *     &lt;enumeration value="param"/>
 *     &lt;enumeration value="attribute"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ScopeType")
@XmlEnum
public enum ScopeType {

    @XmlEnumValue("const")
    CONST("const"),
    @XmlEnumValue("param")
    PARAM("param"),
    @XmlEnumValue("attribute")
    ATTRIBUTE("attribute");
    private final String value;

    ScopeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ScopeType fromValue(String v) {
        for (ScopeType c: ScopeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
