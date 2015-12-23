package org.cld.fixml;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.xml.NamespaceFilter;
import org.xml.fixml.FIXML;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class FixmlUtil {
	private static Logger logger =  LogManager.getLogger(FixmlUtil.class);
	
	public static JAXBElement<FIXML> unmarshal(String input){
		try {
			//Prepare JAXB objects
			JAXBContext jc = JAXBContext.newInstance("org.xml.fixml");
			Unmarshaller u = jc.createUnmarshaller();
			//Create an XMLReader to use with our filter
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(true);
			XMLReader reader = saxParserFactory.newSAXParser().getXMLReader();		
			InputSource is = new InputSource(new ByteArrayInputStream(input.getBytes("utf-8")));
			SAXSource source = new SAXSource(reader, is);
	
			//Do unmarshalling
			return u.unmarshal(source, FIXML.class);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public static JAXBElement<FIXML> unmarshal(Unmarshaller u, XMLReader reader, String input){
		try {
			InputSource is = new InputSource(new ByteArrayInputStream(input.getBytes("utf-8")));
			SAXSource source = new SAXSource(reader, is);
			//Do unmarshalling
			return u.unmarshal(source, FIXML.class);
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
	
	public static String marshal(FIXML fixml){
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FIXML.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter sw = new StringWriter();
			
			jaxbMarshaller.marshal(fixml, sw);
			String payload = sw.toString();
			return payload;
		}catch(Exception e){
			logger.error("", e);
			return null;
		}
	}
}
