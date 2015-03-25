package org.cld.crbook.util.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.crbook.util.XmlCSV;
import org.junit.Test;

import cy.common.xml.XmlWorker;

public class XMLCSVTest {

	private static Logger logger =  LogManager.getLogger(XmlCSV.class);
	
	@Test
	public void genA8Z8() throws FileNotFoundException {
		XmlCSV.genAllCSV("C:\\tmp\\a8z8root", "C:\\tmp\\a8z8root\\a8z8.crcsv", null);
	}
	
	@Test
	public void genMom001() throws FileNotFoundException {
		XmlCSV.genAllCSV("C:\\tmp\\mom001", "C:\\tmp\\mom001\\mom001.dedup.crcsv", 
				"crawl.a8z8.hibernate.cfg.xml");		
	}
	
	@Test
	public void genDMZJ() throws FileNotFoundException {
		XmlCSV.genAllCSV("C:\\tmp\\DMZJ", "C:\\tmp\\DMZJ\\dmzj.crcsv", null);
	}
}
