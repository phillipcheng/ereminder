package org.cld.webconf.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.webconf.XmlToHtml;
import org.junit.Test;
import org.xml.taskdef.BrowseCatType;
import org.xml.taskdef.BrowseTaskType;
import org.xml.taskdef.TasksType;

public class TestGenHtml {
	private static Logger logger = LogManager.getLogger(TestGenHtml.class);
	
	@Test
	public void test1() {
		TasksType tt = new TasksType();
		tt.setStoreId("999999");
		tt.setRootVolume("xrs123");
		BrowseCatType bct = new BrowseCatType();
		bct.setIsLeaf(false);
		BrowseTaskType btt = new BrowseTaskType();
		btt.setEnableJS(false);
		bct.setBaseBrowseTask(btt);
		tt.getCatTask().add(bct);
		
		String html = XmlToHtml.genHtmlComplexSimpleContent(tt, "Tasks");
		
		logger.info(html);
	}
	
	@Test
	public void test2() throws Exception {
		String fileName = "C:\\mydoc\\learn\\CS\\projects\\maven.1401660827862\\Reminder\\modules\\shopping\\src\\test\\resources\\a8z8.xml";
		JAXBContext jc = JAXBContext.newInstance("org.xml.taskdef");
		Unmarshaller u = jc.createUnmarshaller();
		Source source = new StreamSource(new FileInputStream(new File(fileName)));
		JAXBElement<TasksType> root = u.unmarshal(source,TasksType.class);
		TasksType tt = root.getValue();
		
		String html = XmlToHtml.genHtmlComplexSimpleContent(tt, "Tasks");
		
		logger.info(html);
	}
	

}
