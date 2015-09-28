//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.27 at 01:21:12 PM PDT 
//


package org.xml.taskdef;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.xml.taskdef package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Tasks_QNAME = new QName("", "Tasks");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.xml.taskdef
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValueType }
     * 
     */
    public ValueType createValueType() {
        return new ValueType();
    }

    /**
     * Create an instance of {@link TasksType }
     * 
     */
    public TasksType createTasksType() {
        return new TasksType();
    }

    /**
     * Create an instance of {@link RegExpType }
     * 
     */
    public RegExpType createRegExpType() {
        return new RegExpType();
    }

    /**
     * Create an instance of {@link ConditionalNextTask }
     * 
     */
    public ConditionalNextTask createConditionalNextTask() {
        return new ConditionalNextTask();
    }

    /**
     * Create an instance of {@link ParamType }
     * 
     */
    public ParamType createParamType() {
        return new ParamType();
    }

    /**
     * Create an instance of {@link AttributeType }
     * 
     */
    public AttributeType createAttributeType() {
        return new AttributeType();
    }

    /**
     * Create an instance of {@link TokenType }
     * 
     */
    public TokenType createTokenType() {
        return new TokenType();
    }

    /**
     * Create an instance of {@link RedirectType }
     * 
     */
    public RedirectType createRedirectType() {
        return new RedirectType();
    }

    /**
     * Create an instance of {@link ConditionalNextPage }
     * 
     */
    public ConditionalNextPage createConditionalNextPage() {
        return new ConditionalNextPage();
    }

    /**
     * Create an instance of {@link ClickStreamType }
     * 
     */
    public ClickStreamType createClickStreamType() {
        return new ClickStreamType();
    }

    /**
     * Create an instance of {@link BrowseDetailType }
     * 
     */
    public BrowseDetailType createBrowseDetailType() {
        return new BrowseDetailType();
    }

    /**
     * Create an instance of {@link LoginType }
     * 
     */
    public LoginType createLoginType() {
        return new LoginType();
    }

    /**
     * Create an instance of {@link ParamValueType }
     * 
     */
    public ParamValueType createParamValueType() {
        return new ParamValueType();
    }

    /**
     * Create an instance of {@link BrowseTaskType }
     * 
     */
    public BrowseTaskType createBrowseTaskType() {
        return new BrowseTaskType();
    }

    /**
     * Create an instance of {@link TaskInvokeType }
     * 
     */
    public TaskInvokeType createTaskInvokeType() {
        return new TaskInvokeType();
    }

    /**
     * Create an instance of {@link BrowseCatType }
     * 
     */
    public BrowseCatType createBrowseCatType() {
        return new BrowseCatType();
    }

    /**
     * Create an instance of {@link ClickType }
     * 
     */
    public ClickType createClickType() {
        return new ClickType();
    }

    /**
     * Create an instance of {@link CsvTransformType }
     * 
     */
    public CsvTransformType createCsvTransformType() {
        return new CsvTransformType();
    }

    /**
     * Create an instance of {@link SubListType }
     * 
     */
    public SubListType createSubListType() {
        return new SubListType();
    }

    /**
     * Create an instance of {@link TransformOp }
     * 
     */
    public TransformOp createTransformOp() {
        return new TransformOp();
    }

    /**
     * Create an instance of {@link BinaryBoolOp }
     * 
     */
    public BinaryBoolOp createBinaryBoolOp() {
        return new BinaryBoolOp();
    }

    /**
     * Create an instance of {@link CredentialType }
     * 
     */
    public CredentialType createCredentialType() {
        return new CredentialType();
    }

    /**
     * Create an instance of {@link ValueType.StrPreprocess }
     * 
     */
    public ValueType.StrPreprocess createValueTypeStrPreprocess() {
        return new ValueType.StrPreprocess();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TasksType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Tasks")
    public JAXBElement<TasksType> createTasks(TasksType value) {
        return new JAXBElement<TasksType>(_Tasks_QNAME, TasksType.class, null, value);
    }

}
