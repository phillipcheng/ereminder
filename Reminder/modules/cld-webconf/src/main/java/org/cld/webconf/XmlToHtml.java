package org.cld.webconf;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XmlToHtml {
	
	private static Logger logger = LogManager.getLogger("cld.jsp");
	public static final String ELE_NAME_KEY="--name--";
	public static final String ELE_VALUE_KEY="--value--";
	
	//@name in .tg for attribute, #name in .tg for simple content
	
	public static final String ComplexContentHeader=
			"<div class=\"XMLComplexContent\" onclick=\"selectelm(this, event);\" ondblclick=\"selectelm(this, event);\">"
			+ "<fieldset class=\"--name--\">"
			+ "<legend>- --name--</legend>"
			+ "<input type=\"hidden\" value=\"--name--\" name=\".tg\">";
	public static final String ComplexContentFooter = 
			"<input type=\"hidden\" value=\"/--name--\" name=\".tg\">"
			+ "</fieldset></div>";
	public static final String AttributeText=
			"<span class=\"XMLAttribute\" onclick=\"selectelm(this, event);\" ondblclick=\"selectelm(this, event);\">"
			+ "<span class=\"@--name--\">"
			+ "<label>--name--: </label>"
			+ "<input type=\"hidden\" value=\"@--name--\" name=\".tg\">"
			+ "<div class=\"cy-textarea-container\">"
			+ "<textarea oninput=\"textAreaAutoSize(this)\" name=\"@--name--\" value=\"--value--\" onfocus=\"focusGained(this)\" onchange=\"textChanged(this)\">--value--</textarea>"
			+ "<div class=\"cy-textarea-size\"></div>"
			+ "</div>" //auto size text area
			+ "</span><br></span>";
	public static final String AttributeOptionTextPre=
			"<span class=\"XMLAttribute\" onclick=\"selectelm(this, event);\" ondblclick=\"selectelm(this, event);\">"
			+ "<span class=\"@--name--\">"
			+ "<label>--name--: </label>"
			+ "<input type=\"hidden\" value=\"@--name--\" name=\".tg\">"
			+ "<select class=\"@--name--\" name=\"@--name--\" onfocus=\"focusGained(this)\" onchange=\"noUndefined(this)\">";
	public static final String AttributeOptionTextPost=
			"</select>"
			+ "</span><br></span>";
	public static final String SimpleType=
			"<span class=\"XMLSimpleType\" onclick=\"selectelm(this, event);\" ondblclick=\"selectelm(this, event);\">"
			+ "<span class=\"--name--\">"
			+ "<label>--name--: </label>"
			+ "<input type=\"hidden\" value=\"#--name--\" name=\".tg\">"
			+ "<div class=\"cy-textarea-container\">"
			+ "<textarea oninput=\"textAreaAutoSize(this)\" name=\"#--name--\" value=\"--value--\" onfocus=\"focusGained(this)\" onchange=\"textChanged(this)\">--value--</textarea>"
			+ "<div class=\"cy-textarea-size\"></div>"
			+ "</div>" //auto size text area
			+ "</span><br></span>";
	
	private static String getOption(String value, boolean isSelected){
		if (isSelected){
			return String.format("<option value=\"%s\" selected>%s</option>", value, value);
		}else{
			return String.format("<option value=\"%s\">%s</option>", value, value);
		}
		
	}
	
	public static Object getValue(Field f, Object obj){
		Object val=null;
		f.setAccessible(true);
		try {
			val= f.get(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return val;
	}
	
	public static String genHtmlAttr(String name, Object value){
		String output="";
		if (value.getClass().isEnum()){//for enumeration
			output= AttributeOptionTextPre;
			for (Object obj:value.getClass().getEnumConstants()){
				boolean selected = obj.toString().equals(value.toString());
				output +=getOption(obj.toString().toLowerCase(), selected);//to lower case for value, TODO
			}
			output += AttributeOptionTextPost;
		}else{
			output = AttributeText.replace(ELE_VALUE_KEY, 
					StringEscapeUtils.escapeHtml(StringEscapeUtils.unescapeXml(value.toString())));
		}
		return output.replace(ELE_NAME_KEY, name);
	}
	
	public static String genHtmlSimpleType(String name, String value){
		String output = SimpleType.replace(ELE_NAME_KEY, name);
		return output.replace(ELE_VALUE_KEY, value);
	}

	//generate the html for complex content schema based on clazz, data from obj
	public static String genHtmlComplexSimpleContent(Object obj, String rootName){
		StringWriter sw = new StringWriter();
		Class clazz = obj.getClass();
		if (clazz.isAnnotationPresent(XmlType.class)){
			//generate html for complex type
			XmlType annoXmlType = (XmlType) clazz.getAnnotation(XmlType.class);
			String name = annoXmlType.name();
			String[] preorder = annoXmlType.propOrder();
			
			Field[] fields = clazz.getDeclaredFields();
			List<Field> attrFields = new ArrayList<Field>();
			List<Field> eleFields = new ArrayList<Field>();
			List<Field> simpleFields = new ArrayList<Field>();
			
			for (int i=0; i<fields.length; i++){
				Field f = fields[i];
				if (f.isAnnotationPresent(XmlAttribute.class)){
					attrFields.add(f);
				}else {
					eleFields.add(f);
				}
			}
			//gen me element header
			String meEleHeader = ComplexContentHeader.replace(ELE_NAME_KEY, rootName);
			sw.write(meEleHeader);
			//gen attribute first
			for (Field f:attrFields){
				Object fv = null;
				try {
					fv = getValue(f,obj);
				} catch (Exception e) {
					logger.error("", e);
				}
				if (fv!=null){
					String attrHtml = genHtmlAttr(f.getName(), fv);
					sw.write(attrHtml);
				}
			}
			//gen member element
			for (Field f:eleFields){
				XmlElement xele = f.getAnnotation(XmlElement.class);
				String eleName="";
				if (xele!=null){
					eleName = xele.name();
				}else{
					eleName = f.getName();
				}
				if ("##default".equals(eleName)){
					eleName = f.getName();
				}
				Object fv = null;
				try {
					fv = getValue(f,obj);
				} catch (Exception e) {
					logger.error("",e);
				}
				if (fv!=null){
					if (fv instanceof List){
						List fvlist = (List)fv;
						for (Object fvone: fvlist){
							String eleHtml = genHtmlComplexSimpleContent(fvone, eleName);
							sw.write(eleHtml);
						}
					}else{
						String eleHtml = genHtmlComplexSimpleContent(fv, eleName);
						sw.write(eleHtml);
					}
				}
			}
			//gen me element footer
			String meEleFooter = ComplexContentFooter.replace(ELE_NAME_KEY, rootName);
			sw.write(meEleFooter);
		}else{
			//for simple type
			String simpleTypeHtml = genHtmlSimpleType(rootName, (String)obj);
			sw.write(simpleTypeHtml);
		}
		return sw.toString();
	}

}
