//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.12.22 at 03:44:23 AM PST 
//


package org.xml.fixml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TimeUnit_enum_t.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TimeUnit_enum_t">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="Min"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="D"/>
 *     &lt;enumeration value="Wk"/>
 *     &lt;enumeration value="Mo"/>
 *     &lt;enumeration value="Yr"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TimeUnit_enum_t")
@XmlEnum
public enum TimeUnitEnumT {

    H("H"),
    @XmlEnumValue("Min")
    MIN("Min"),
    S("S"),
    D("D"),
    @XmlEnumValue("Wk")
    WK("Wk"),
    @XmlEnumValue("Mo")
    MO("Mo"),
    @XmlEnumValue("Yr")
    YR("Yr");
    private final String value;

    TimeUnitEnumT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TimeUnitEnumT fromValue(String v) {
        for (TimeUnitEnumT c: TimeUnitEnumT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
