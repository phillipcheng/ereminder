package org.cld.util.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TestXPath {
	public static final Logger logger = LogManager.getLogger(TestXPath.class);
	
	@Test
	public void genUrls() throws Exception {
		File file = new File("C:\\Kibot\\1min.url");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File("C:\\Kibot\\1min_download.html"));

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "//table[1]/tr[position()>1]/td[3]/a/@href";
		NodeList nl = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		for (int i=0; i<nl.getLength(); i++){
			Node n = nl.item(i);
			bw.write(String.format("%s", n.getNodeValue()));
			bw.write("\n");
		}
		bw.close();
	}
}
