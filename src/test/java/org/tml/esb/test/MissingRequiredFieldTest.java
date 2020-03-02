package org.tml.esb.test;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MissingRequiredFieldTest extends CamelSpringTestSupport {

	@Produce(uri = "cxf:bean:smsEndpoint")
	protected ProducerTemplate inputEndpoint;
	
	
	@EndpointInject(uri = "mock:sqlComponent:{{sql.insertNewSMS}}")
	protected MockEndpoint outputEndpoint;
	

	@Test
	public void testSuccessCamelRoute() throws Exception {
		
		String msg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:esb=\"http://esb.tml.org/\">\n" +
"   <soapenv:Header/>\n" +
"   <soapenv:Body>\n" +
"      <esb:insert>\n" +
"         <messageId>76589065</messageId>\n" +
"         <phoneNumber>9878987876</phoneNumber>\n" +
"         <ruleName>ESB_Test</ruleName>\n" +
"		  <messageText></messageText>\n" +	
"      </esb:insert>\n" +
"   </soapenv:Body>\n" +
"</soapenv:Envelope>";
		
		
        String result = template.requestBody("cxf:bean:smsEndpoint?dataFormat=MESSAGE", msg,String.class);

       
        
		// Expected Output
    String expectedMsg =  "Header parameter is missing or null";
		
        assertEquals(expectedMsg, result);		
		
	}
	
	
	
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from("jetty:http://0.0.0.0:8086/sms")
						.to(outputEndpoint);
			}
		};
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("spring/camel-context-test.xml");
	}

}

