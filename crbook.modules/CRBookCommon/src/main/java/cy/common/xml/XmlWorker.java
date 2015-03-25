package cy.common.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cy.common.entity.Book;
import cy.common.entity.Volume;
import cy.common.entity.Page;
import cy.common.persist.RemotePersistManager;


public class XmlWorker {
	
	public static final String BOOK_SUFFIX_1="crbook";
	public static final String[] BOOK_SUFFIXS = {BOOK_SUFFIX_1}; 
	public static final String VOL_SUFFIX_1="crvol";
	public static final String[] VOL_SUFFIXS = {VOL_SUFFIX_1};
	public static final String CSV_SUFFIX_1="crcsv";
	public static final String[] CSV_SUFFIXS = {CSV_SUFFIX_1};
	public static final String ZIP_SUFFIX_1="zip";
	
	/**
	 * 
	 * @param b, input
	 * @param pl, input
	 * @param f, output
	 */
	public static void writeBookXml(Book b, List<Page> pl, File f) throws Exception{		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		Element eleBook = doc.createElement(Book.BOOK_KEY);
		//Book maps to eleBook
		eleBook.setAttribute(Book.ID_KEY, b.getId());
		eleBook.setAttribute(Book.NAME_KEY, b.getName());
		eleBook.setAttribute(Book.TotalPage_KEY, b.getTotalPage()+"");
		eleBook.setAttribute(Book.DATA_KEY, b.getData());
		eleBook.setAttribute(Book.CAT_KEY, b.getCat());
		eleBook.setAttribute(Book.AUTHOR_KEY, b.getAuthor());
		eleBook.setAttribute(Book.UTIME_KEY, RemotePersistManager.SDF_SERVER_DTZ.format(b.getUtime()));
		
		doc.appendChild(eleBook);
		
		for (int i=0; i<pl.size(); i++){
			Page p = pl.get(i);
			Element elePage = doc.createElement(Page.PAGE_KEY);
			//elePage.setAttribute(PAGE_BOOKID, p.getBookid());
			//elePage.setAttribute(PAGE_BOOKNAME, p.getBookName());
			elePage.setAttribute(Page.PAGENUM_KEY, p.getPagenum()+"");
			elePage.setAttribute(Page.DATA_KEY, p.getData());
			elePage.setAttribute(Page.UTIME_KEY, p.getUtime());
			eleBook.appendChild(elePage);
		}
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String fname = f.getPath();
		FileOutputStream fos = new FileOutputStream(fname);
		//new StreamResult(f): setSystemId(f.toURI().toASCIIString());, not i like
		StreamResult result = new StreamResult(fos);		
		transformer.transform(source, result);		
		fos.close();
	}
	
	/**
	 * 
	 * @param c, input
	 * @param f, output
	 */
	public static void writeVolumeXml(Volume c, File f) throws Exception{		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document doc = docBuilder.newDocument();
		Element eleCat = doc.createElement(Volume.VOLUME_KEY);
		//Category maps to eleCat
		eleCat.setAttribute(Volume.ID_KEY, c.getId());
		eleCat.setAttribute(Volume.TYPE_KEY, c.getType()+"");
		eleCat.setAttribute(Volume.NAME_KEY, c.getName());
		eleCat.setAttribute(Volume.DATA_KEY, c.getData());
		eleCat.setAttribute(Volume.CAT_KEY, c.getParentCat());
		eleCat.setAttribute(Volume.AUTHOR_KEY, c.getAuthor());

		doc.appendChild(eleCat);
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		String fname = f.getPath();
		//new StreamResult(f): setSystemId(f.toURI().toASCIIString());, not i like
		FileOutputStream fos = new FileOutputStream(fname);
		StreamResult result = new StreamResult(fos);
		transformer.transform(source, result);
		fos.close();
	}
	
	
	
	private static void readBookXml(Book b, List<Page> pl, Document doc) throws Exception {
		doc.getDocumentElement().normalize();
		NodeList booklist = doc.getElementsByTagName(Book.BOOK_KEY);
		for (int i=0; i<booklist.getLength(); i++){
			Element eleBook = (Element) booklist.item(i);
			//eleBook maps to Book
			String id = eleBook.getAttribute(Book.ID_KEY);
			int type = Integer.parseInt(eleBook.getAttribute(Book.TYPE_KEY));
			String name = eleBook.getAttribute(Book.NAME_KEY);
			int totalPage = Integer.parseInt(eleBook.getAttribute(Book.TotalPage_KEY));
			String bookData = eleBook.getAttribute(Book.DATA_KEY);
			String bookCat = eleBook.getAttribute(Book.CAT_KEY);
			String author = eleBook.getAttribute(Book.AUTHOR_KEY);
			b.init(id, type, name, totalPage, 1, null, bookData, bookCat, 0, 0, 0, author, 0, true);
			NodeList pagelist = eleBook.getElementsByTagName(Page.PAGE_KEY);
			for (int j=0; j<pagelist.getLength(); j++){
				Element elePage = (Element)pagelist.item(j);
				int pagenum = Integer.parseInt(elePage.getAttribute(Page.PAGENUM_KEY));
				String data = elePage.getAttribute(Page.DATA_KEY);
				String pageUtime = elePage.getAttribute(Page.UTIME_KEY);
				Page p = new Page(id, pagenum, data, pageUtime, true);
				//optimize
				p.dataToJSON();
				pl.add(p);
			}
		}	 
	}
	
	/**
	 * 
	 * @param b, output
	 * @param pl, output, passing empty list, i will add page to it.
	 * @param xml, input
	 */
	public static void readBookXml(Book b, List<Page> pl, File f) throws Exception {
			 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		
		readBookXml(b, pl, doc);
	}
	
	public static void readBookXml(Book b, List<Page> pl, InputStream is) throws Exception {
		 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		
		readBookXml(b, pl, doc);
	}
	
	
	private static void readVolume(Volume c, Document doc){
		doc.getDocumentElement().normalize();
		NodeList catlist = doc.getElementsByTagName(Volume.VOLUME_KEY);
		for (int i=0; i<catlist.getLength(); i++){
			Element eleCat = (Element) catlist.item(i);
			//eleBook maps to Book
			String id = eleCat.getAttribute(Volume.ID_KEY);
			int type = Integer.parseInt(eleCat.getAttribute(Volume.TYPE_KEY));
			String name = eleCat.getAttribute(Volume.NAME_KEY);
			String data = eleCat.getAttribute(Volume.DATA_KEY);
			String pcat = eleCat.getAttribute(Volume.CAT_KEY);
			String author = eleCat.getAttribute(Volume.AUTHOR_KEY);
			c.init(id, type, name, null, data, pcat, author, 0, true);
		}	
	}
	
	/**
	 * 
	 * @param c, output
	 * @param xml, input
	 */
	public static void readVolumeXml(Volume c, InputStream is) throws Exception {			 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		
		readVolume(c, doc);
	}
	
	/**
	 * 
	 * @param c, output
	 * @param xml, input
	 */
	public static void readVolumeXml(Volume c, File f) throws Exception {			 
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		
		readVolume(c, doc);
	}
}
