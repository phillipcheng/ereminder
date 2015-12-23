
package cy.common.wsclient.test;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import cy.common.entity.Book;
import cy.crbook.wsserver.CRBookWS;

public class SoapClient {
	public static Logger logger = LogManager.getLogger(SoapClient.class);
    private static final QName SERVICE_NAME 
        = new QName("http://wsserver.crbook.cy/", "CRBookWS");
    private static final QName PORT_NAME 
        = new QName("http://wsserver.crbook.cy/", "CRBookWSPort");



    @Test
    public void test1(){
        Service service = Service.create(SERVICE_NAME);
        // Endpoint Address
        //String endpointAddress = "http://localhost:9000/helloWorld";
        // If web service deployed on Tomcat (either standalone or embedded)
        // as described in sample README, endpoint should be changed to:
        String endpointAddress = "http://ec2-54-187-167-132.us-west-2.compute.amazonaws.com:8080/crbookws/services/crbook";

        // Add a port to the Service
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
        
        CRBookWS hw = service.getPort(CRBookWS.class);

        Book b = hw.getBookById("1001ye-19281");
        
        logger.info("book:" + b);
    }

}
