//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.04 at 05:59:49 PM PST 
//


package org.xml.fixml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LegPriceUnitOfMeasure_enum_t.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LegPriceUnitOfMeasure_enum_t">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Bbl"/>
 *     &lt;enumeration value="Bcf"/>
 *     &lt;enumeration value="Bu"/>
 *     &lt;enumeration value="lbs"/>
 *     &lt;enumeration value="Gal"/>
 *     &lt;enumeration value="MMbbl"/>
 *     &lt;enumeration value="MMBtu"/>
 *     &lt;enumeration value="MWh"/>
 *     &lt;enumeration value="oz_tr"/>
 *     &lt;enumeration value="t"/>
 *     &lt;enumeration value="tn"/>
 *     &lt;enumeration value="USD"/>
 *     &lt;enumeration value="Alw"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LegPriceUnitOfMeasure_enum_t")
@XmlEnum
public enum LegPriceUnitOfMeasureEnumT {

    @XmlEnumValue("Bbl")
    BBL("Bbl"),
    @XmlEnumValue("Bcf")
    BCF("Bcf"),
    @XmlEnumValue("Bu")
    BU("Bu"),
    @XmlEnumValue("lbs")
    LBS("lbs"),
    @XmlEnumValue("Gal")
    GAL("Gal"),
    @XmlEnumValue("MMbbl")
    M_MBBL("MMbbl"),
    @XmlEnumValue("MMBtu")
    MM_BTU("MMBtu"),
    @XmlEnumValue("MWh")
    M_WH("MWh"),
    @XmlEnumValue("oz_tr")
    OZ_TR("oz_tr"),
    @XmlEnumValue("t")
    T("t"),
    @XmlEnumValue("tn")
    TN("tn"),
    USD("USD"),
    @XmlEnumValue("Alw")
    ALW("Alw");
    private final String value;

    LegPriceUnitOfMeasureEnumT(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LegPriceUnitOfMeasureEnumT fromValue(String v) {
        for (LegPriceUnitOfMeasureEnumT c: LegPriceUnitOfMeasureEnumT.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
