//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.01.29 at 11:32:12 AM PST 
//


package org.xml.taskdef;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VarType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VarType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="int"/>
 *     &lt;enumeration value="float"/>
 *     &lt;enumeration value="xpath"/>
 *     &lt;enumeration value="date"/>
 *     &lt;enumeration value="page"/>
 *     &lt;enumeration value="list"/>
 *     &lt;enumeration value="array"/>
 *     &lt;enumeration value="pagelist"/>
 *     &lt;enumeration value="regexp"/>
 *     &lt;enumeration value="expression"/>
 *     &lt;enumeration value="externalList"/>
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="file"/>
 *     &lt;enumeration value="htmlElement"/>
 *     &lt;enumeration value="url"/>
 *     &lt;enumeration value="object"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VarType")
@XmlEnum
public enum VarType {

    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("int")
    INT("int"),
    @XmlEnumValue("float")
    FLOAT("float"),
    @XmlEnumValue("xpath")
    XPATH("xpath"),
    @XmlEnumValue("date")
    DATE("date"),
    @XmlEnumValue("page")
    PAGE("page"),
    @XmlEnumValue("list")
    LIST("list"),
    @XmlEnumValue("array")
    ARRAY("array"),
    @XmlEnumValue("pagelist")
    PAGELIST("pagelist"),
    @XmlEnumValue("regexp")
    REGEXP("regexp"),
    @XmlEnumValue("expression")
    EXPRESSION("expression"),
    @XmlEnumValue("externalList")
    EXTERNAL_LIST("externalList"),
    @XmlEnumValue("boolean")
    BOOLEAN("boolean"),
    @XmlEnumValue("file")
    FILE("file"),
    @XmlEnumValue("htmlElement")
    HTML_ELEMENT("htmlElement"),
    @XmlEnumValue("url")
    URL("url"),
    @XmlEnumValue("object")
    OBJECT("object");
    private final String value;

    VarType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VarType fromValue(String v) {
        for (VarType c: VarType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
