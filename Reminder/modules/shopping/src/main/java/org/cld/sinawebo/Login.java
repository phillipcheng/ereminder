package org.cld.sinawebo;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.codec.binary.Base64;
import org.cld.util.JsonUtil;
import org.json.JSONObject;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

public class Login {
	private static Logger logger =  LogManager.getLogger(Login.class);
	
    public static String ServerUrl = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su=&rsakt=mod&client=ssologin.js(v1.4.11)&_=1379834957683";
    public static String LoginUrl = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)";
    
    public static String KEY_USERNAME = "username"; //from 
    public static String KEY_PASSWORD = "password";
    public static String KEY_SERVERTIME = "servertime";
    public static String KEY_NONCE = "nonce";
    public static String KEY_RSAKV = "rsakv";
    public static String KEY_PUBKEY = "pubkey";
    
    /**
     * 
     * @param password, RSA key modulus
     * @param servertime
     * @param nonce
     * @param pubkey
     * @return
     * @throws Exception 
     */
    public String getPassword(String password, long servertime, String nonce, String pubkey) throws Exception{
    	BigInteger rsaPublickeyModulus = new BigInteger(pubkey, 16);
    	BigInteger rsaPublickeyExponent = new BigInteger("010001", 16);//65537
    	KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    	RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(rsaPublickeyModulus, rsaPublickeyExponent);
    	RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(pubKeySpec);//创建公钥
    	logger.info("public key:" + key.toString());
    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.ENCRYPT_MODE, key);
    	String message = servertime + '\t' + nonce + '\n' + password; //拼接明文js加密文件中得到
    	byte[] cipherData = cipher.doFinal(message.getBytes());//加密
    	return Hex.encodeHexString(cipherData);//将加密信息转换为16进制。
    }
    
        
    public JSONObject getServerParams(WebClient wc) throws IOException{
    	logger.debug("Getting server time and nonce...");
    	WebRequest req = new WebRequest(new URL(ServerUrl), HttpMethod.GET);
    	String rsp = wc.getPage(req).getWebResponse().getContentAsString();
    	logger.debug("rsp text:" + rsp);
    	JSONObject jobj = JsonUtil.getJsonDataFromSingleParameterFunctionCall(rsp);
    	return jobj;
    }
    
    private String getRedirectUrl(String input){
    	Pattern p = Pattern.compile("location\\.replace\\([\\'\"](.*?)[\\'\"]\\)");
    	Matcher m = p.matcher(input);
    	if (m.find()){
    		return m.group(1);
    	}else{
    		return null;
    	}
    }
    
	public void login(WebClient wc, Map<String, String> params) throws Exception{
		JSONObject serverParams = getServerParams(wc);//登陆的第一步
		long servertime = serverParams.getLong(KEY_SERVERTIME);
		String nonce = serverParams.getString(KEY_NONCE);
		String pubkey = serverParams.getString(KEY_PUBKEY);
		String rsakv = serverParams.getString(KEY_RSAKV);
		String userNameOrg = params.get(KEY_USERNAME);
		String passwordOrg = params.get(KEY_PASSWORD);
		
		String urlEncodedUserName = URLEncoder.encode(userNameOrg);
		String base64EncodedUserName = new String(Base64.encodeBase64(urlEncodedUserName.getBytes()));
		logger.debug("userName:" + base64EncodedUserName);
		String encryptedPassword = getPassword(passwordOrg, servertime, nonce, pubkey);
		logger.debug("password:" + encryptedPassword);
		
		WebRequest postReq = new WebRequest(new URL(LoginUrl), HttpMethod.POST);
		NameValuePair[] data= {
				new NameValuePair("entry", "weibo"),
				new NameValuePair("gateway", "1"),
				new NameValuePair("from", ""),
				new NameValuePair("savestate", "7"),
				new NameValuePair("userticket", "1"),
				new NameValuePair("ssosimplelogin", "1"),
				new NameValuePair("vsnf", "1"),
				new NameValuePair("vsnval", ""),
				new NameValuePair("su", base64EncodedUserName),
				new NameValuePair("service", "miniblog"),
				new NameValuePair("servertime", servertime+""),
				new NameValuePair("nonce", nonce),
				new NameValuePair("pwencode", "rsa2"),
				new NameValuePair("sp", encryptedPassword),
				new NameValuePair("encoding", "UTF-8"),
				new NameValuePair("prelt", "115"),
				new NameValuePair("rsakv", rsakv),
				new NameValuePair("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"),
				new NameValuePair("returntype", "META"),
		};
		postReq.setRequestParameters(Arrays.asList(data));
		postReq.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:24.0) Gecko/20100101 Firefox/24.0");
        String rsp = wc.getPage(postReq).getWebResponse().getContentAsString(); //登陆的第二步——解析新浪微博的登录过程中3
        logger.debug(String.format("rsp:%s", rsp));
        
        String redirectLoginUrl = getRedirectUrl(rsp);//解析重定位结果
        
        WebRequest getRedirectLoginReq = new WebRequest(new URL(redirectLoginUrl), HttpMethod.GET);
        
        String finalResult = wc.getPage(getRedirectLoginReq).getWebResponse().getContentAsString();
        
        logger.debug("finalResult: " + finalResult);
	}

}
