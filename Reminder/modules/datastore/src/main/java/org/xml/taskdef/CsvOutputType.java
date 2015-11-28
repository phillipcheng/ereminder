//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.28 at 02:10:17 PM PST 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CsvOutputType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CsvOutputType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="byId"/>
 *     &lt;enumeration value="byJobSingle"/>
 *     &lt;enumeration value="byJobMulti"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CsvOutputType")
@XmlEnum
public enum CsvOutputType {

    @XmlEnumValue("byId")
    BY_ID("byId"),
    @XmlEnumValue("byJobSingle")
    BY_JOB_SINGLE("byJobSingle"),
    @XmlEnumValue("byJobMulti")
    BY_JOB_MULTI("byJobMulti");
    private final String value;

    CsvOutputType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CsvOutputType fromValue(String v) {
        for (CsvOutputType c: CsvOutputType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
