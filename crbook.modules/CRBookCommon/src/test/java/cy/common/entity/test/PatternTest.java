package cy.common.entity.test;

import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cld.util.PatternResult;
import org.junit.Test;

public class PatternTest {
	private static Logger logger =  LogManager.getLogger(PatternTest.class);
	@Test
	public void myTests(){
		String imageUrl, nextUrl, guessUrl;
		imageUrl ="http://imgfast.dmzj.com/h/%E9%BB%91%E5%AD%90%E7%AF%AE%E7%90%83/%E7%AC%AC249%E8%AF%9D/0%20%285%29%20%E5%89%AF%E6%9C%AC.jpg";
		nextUrl = "http://imgfast.dmzj.com/h/%E9%BB%91%E5%AD%90%E7%AF%AE%E7%90%83/%E7%AC%AC249%E8%AF%9D/0%20%286%29%20%E5%89%AF%E6%9C%AC.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
		imageUrl ="http://imgfast.dmzj.com/g/%E6%80%AA%E4%BD%93%E7%9C%9F%E4%B9%A6%20%E9%9B%B6/%E7%AC%AC11%E8%AF%9D/img009%E5%89%AF%E6%9C%AC.jpg";
		nextUrl = "http://imgfast.dmzj.com/g/%E6%80%AA%E4%BD%93%E7%9C%9F%E4%B9%A6%20%E9%9B%B6/%E7%AC%AC11%E8%AF%9D/img010%E5%89%AF%E6%9C%AC.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
		imageUrl ="http://imgfast.dmzj.com/c/Cswordandcornett/%5B15%5D/005%20%E8%A4%87%E8%A3%BD.jpg";
		nextUrl = "http://imgfast.dmzj.com/c/Cswordandcornett/%5B15%5D/006%20%E8%A4%87%E8%A3%BD.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
		imageUrl ="http://imgfast0.dmzj.com/d/DI%5BE%5DCE/%E7%AC%AC22%E8%AF%9D/009_.jpg";
		nextUrl = "http://imgfast0.dmzj.com/d/DI%5BE%5DCE/%E7%AC%AC22%E8%AF%9D/010_.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
		imageUrl ="http://imgfast.dmzj.com/c/CODE_BREAKER/code93/Scan-100621-0009%E5%89%AF%E6%9C%AC.jpg";
		nextUrl = "http://imgfast.dmzj.com/c/CODE_BREAKER/code93/Scan-100621-0010%E5%89%AF%E6%9C%AC.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
	
		imageUrl ="http://imgfast0.dmzj.com/z/%E8%91%AC%E4%BB%AA%E5%B1%8B%E9%87%8C%E5%BE%B7%E9%B2%81/13/9.jpg";
		nextUrl = "http://imgfast0.dmzj.com/z/%E8%91%AC%E4%BB%AA%E5%B1%8B%E9%87%8C%E5%BE%B7%E9%B2%81/13/10.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));		
		
		//why match 091 not 05
		imageUrl ="http://imgfast.dmzj.com/k/%E5%88%BB%E5%8D%B0/%E7%AC%AC23%E8%AF%9D_1382011899/TabooTattoo05_091%20%E5%89%AF%E6%9C%AC.jpg";
		nextUrl = "http://imgfast.dmzj.com/k/%E5%88%BB%E5%8D%B0/%E7%AC%AC23%E8%AF%9D_1382011899/TabooTattoo05_092%20%E5%89%AF%E6%9C%AC.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		//why match 098 not 09
		imageUrl ="http://imgfast.dmzj.com/n/%E9%80%86%E8%BD%AC%E7%9B%91%E7%9D%A3/%E7%AC%AC82%E8%AF%9D/GIANT%20KILLING09_098%20%E5%89%AF%E6%9C%AC.jpg";
		nextUrl = "http://imgfast.dmzj.com/n/%E9%80%86%E8%BD%AC%E7%9B%91%E7%9D%A3/%E7%AC%AC82%E8%AF%9D/GIANT%20KILLING09_099%20%E5%89%AF%E6%9C%AC.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
//		//why match 09 not 121
//		imageUrl ="http://imgfast.dmzj.com/n/%E9%80%86%E8%BD%AC%E7%9B%91%E7%9D%A3/%E7%AC%AC83%E8%AF%9D/GIANT%20KILLING09_121%20%E5%89%AF%E6%9C%AC.jpg";
//		nextUrl = "http://imgfast.dmzj.com/n/%E9%80%86%E8%BD%AC%E7%9B%91%E7%9D%A3/%E7%AC%AC83%E8%AF%9D/GIANT%20KILLING09_122%20%E5%89%AF%E6%9C%AC.jpg";
//		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl, 121), 122);
//		logger.info("guess url:" + guessUrl);
//		assertTrue(nextUrl.equals(guessUrl));
		
		imageUrl ="http://imgfast0.dmzj.com/g/%E8%AF%A1%E8%BE%A9%E5%AD%A6%E6%B4%BE%20%E5%9B%9B%E8%B0%B7%E5%89%8D%E8%BE%88%E7%9A%84%E6%80%AA%E8%B0%88/%E7%AC%AC16%E8%AF%9D/030.jpg";
		nextUrl = "http://imgfast0.dmzj.com/g/%E8%AF%A1%E8%BE%A9%E5%AD%A6%E6%B4%BE%20%E5%9B%9B%E8%B0%B7%E5%89%8D%E8%BE%88%E7%9A%84%E6%80%AA%E8%B0%88/%E7%AC%AC16%E8%AF%9D/031.jpg";
		guessUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1);
		logger.info("guess url:" + guessUrl);
		assertTrue(nextUrl.equals(guessUrl));
		
	}
		
	@Test
	public void mytest(){
		String imageUrl ="http://imgfast0.dmzj.com/j/%E6%9C%BA%E5%99%A8%E7%8C%AB/vol01/009.jpg";
		String nextUrl = "http://imgfast0.dmzj.com/j/%E6%9C%BA%E5%99%A8%E7%8C%AB/vol01/010.jpg";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1)));
		
		
		imageUrl ="http://imgfast0.dmzj.com/j/%E6%9C%BA%E5%99%A8%E7%8C%AB/vol01/099.jpg";
		nextUrl = "http://imgfast0.dmzj.com/j/%E6%9C%BA%E5%99%A8%E7%8C%AB/vol01/101.jpg";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 2)));
	}
	//
	@Test
	public void mytest1(){
		String imageUrl ="http://imgfast.dmzj.com/b/%E5%AE%9D%E8%B4%9D%E6%88%91%E7%88%B1%E4%BD%A0/Vol_01/1.JPG";
		String nextUrl = "http://imgfast.dmzj.com/b/%E5%AE%9D%E8%B4%9D%E6%88%91%E7%88%B1%E4%BD%A0/Vol_01/2.JPG";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1)));
	}
	
	@Test
	public void mytest2(){
		String imageUrl ="http://imgfast0.dmzj.com/g/Get_backers/Vol_01/001002.jpg";
		String nextUrl = "http://imgfast0.dmzj.com/g/Get_backers/Vol_01/010011.jpg";
		String guessedUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 9);
		logger.info(guessedUrl);
		assertTrue(nextUrl.equals(guessedUrl));
	}
	
	@Test
	public void mytest3(){
		String imageUrl ="http://imgfast0.dmzj.com/g/Get_backers/Vol_01/0102.jpg";
		String nextUrl = "http://imgfast0.dmzj.com/g/Get_backers/Vol_01/1011.jpg";
		String guessedUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 9);
		logger.info(guessedUrl);
		assertTrue(nextUrl.equals(guessedUrl));
	}
	
	//decreasing order
	@Test
	public void mytest4(){
		String imageUrl ="http://imgfast.dmzj.com/m/minami-ke/Vol_01/085.jpg";
		String nextUrl = "http://imgfast.dmzj.com/m/minami-ke/Vol_01/084.jpg";
		String guessedUrl = PatternResult.guessUrl(PatternResult.findPattern(imageUrl), -1);
		logger.info(guessedUrl);
		assertTrue(nextUrl.equals(guessedUrl));
	}
	//_001.jpg
	@Test
	public void mytest5(){
		String imageUrl ="http://imgfast.dmzj.com/h/%E8%8A%B1%E6%A0%B7%E7%94%B7%E5%AD%90/vol_14/FJNJ14_001.jpg";
		String nextUrl = "http://imgfast.dmzj.com/h/%E8%8A%B1%E6%A0%B7%E7%94%B7%E5%AD%90/vol_14/FJNJ14_002.jpg";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1)));
	}
	
	//0001.jpg
	@Test
	public void mytest6(){
		String imageUrl ="http://imgfast.dmzj.com/j/%E7%BB%93%E7%95%8C%E5%B8%88/%E7%BB%93%E7%95%8C%E5%B8%88_%E7%AC%AC210%E8%AF%9D/%E7%BB%93%E7%95%8C%E5%B8%88_CH210_0005.png";
		String nextUrl = "http://imgfast.dmzj.com/j/%E7%BB%93%E7%95%8C%E5%B8%88/%E7%BB%93%E7%95%8C%E5%B8%88_%E7%AC%AC210%E8%AF%9D/%E7%BB%93%E7%95%8C%E5%B8%88_CH210_0006.png";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1)));
	}
	
	@Test
	public void mytest7(){
		String imageUrl ="http://imgfast.dmzj.com/l/Loveless/8/a%20%281%29.jpg";
		String nextUrl = "http://imgfast.dmzj.com/l/Loveless/8/a%20%282%29.jpg";
		assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl), 1)));
	}
	
	@Test
	public void mytest8(){
		String imageUrl ="http://imgfast.dmzj.com/j/%E7%BB%93%E7%95%8C%E5%B8%88/%E7%BB%93%E7%95%8C%E5%B8%88_%E7%AC%AC213%E8%AF%9D/%E7%BB%93%E7%95%8C%E5%B8%88_CH213_0001.jpg";
		String nextUrl = "http://imgfast.dmzj.com/j/%E7%BB%93%E7%95%8C%E5%B8%88/%E7%BB%93%E7%95%8C%E5%B8%88_%E7%AC%AC213%E8%AF%9D/%E7%BB%93%E7%95%8C%E5%B8%88_CH213_0102.jpg";
		//assertTrue(nextUrl.equals(PatternResult.guessUrl(PatternResult.findPattern(imageUrl, 1), 1)));
	}
	
	
	@Test
	public void testTwoUrl1(){
		String imageUrl1 ="http://images.dmzj.com/f/%E5%A4%8D%E4%BB%87%E8%80%85V5/%E7%AC%AC18%E5%8D%B7/Avengers%20v5%20018-002-FearlessCHS.jpg";
		String imageUrl2 ="http://images.dmzj.com/f/%E5%A4%8D%E4%BB%87%E8%80%85V5/%E7%AC%AC18%E5%8D%B7/Avengers%20v5%20018-003-FearlessCHS.jpg";
		String nextUrl="http://images.dmzj.com/f/%E5%A4%8D%E4%BB%87%E8%80%85V5/%E7%AC%AC18%E5%8D%B7/Avengers%20v5%20018-004-FearlessCHS.jpg";
		String url=PatternResult.guessUrl(PatternResult.findPattern(imageUrl1, imageUrl2), 2);
		logger.info(url);
		assertTrue(nextUrl.equals(url));
	}
	
	@Test
	public void testTwoUrl2(){
		String imageUrl1 ="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC03%E8%AF%9D_1369662048/%C3%84l%C3%AE%C3%84%C3%A9-%C3%AEN%C3%A9%C2%A6%C3%ABR01_133.jpg";
		String imageUrl2 ="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC03%E8%AF%9D_1369662048/%C3%84l%C3%AE%C3%84%C3%A9-%C3%AEN%C3%A9%C2%A6%C3%ABR01_134.jpg";
		String nextUrl="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC03%E8%AF%9D_1369662048/%C3%84l%C3%AE%C3%84%C3%A9-%C3%AEN%C3%A9%C2%A6%C3%ABR01_135.jpg";
		String url=PatternResult.guessUrl(PatternResult.findPattern(imageUrl1, imageUrl2), 2);
		logger.info(url);
		assertTrue(nextUrl.equals(url));
	}
	
		
	@Test
	public void testTwoUrl3(){
		String imageUrl1 ="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC08%E8%AF%9D_1371054039/02_152.jpg";
		String imageUrl2 ="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC08%E8%AF%9D_1371054039/02_153.jpg";
		String nextUrl="http://images.dmzj.com/s/%E5%9B%9B%E6%9C%88%E6%98%AF%E4%BD%A0%E7%9A%84%E8%B0%8E%E8%A8%80/%E7%AC%AC08%E8%AF%9D_1371054039/02_154.jpg";
		String url=PatternResult.guessUrl(PatternResult.findPattern(imageUrl1, imageUrl2), 2);
		logger.info(url);
		assertTrue(nextUrl.equals(url));
	}
	
}
