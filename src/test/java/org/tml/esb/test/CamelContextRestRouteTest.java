package org.tml.esb.test;

import java.util.HashMap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelContextRestRouteTest extends CamelSpringTestSupport{
	
	
	@Produce(uri = "direct:restRoute")
	protected ProducerTemplate inputEndpoint;
	
	
	@EndpointInject(uri = "mock:sqlComponent:{{sql.insertNewSMS}}")
	protected MockEndpoint outputEndpoint;

	HashMap<String, Object> hmap = new HashMap<String, Object>();
	
	@Test 
	public void missingHeaderParams() throws Exception
	{          		 		
		String body= "{\"phoneNumber\":\"8767980967\",\"ruleName\":\"ESB_Test\",\"messageText\":''}";
		String result = (String) template.requestBody("direct:restRoute", body, String.class);
		
		assertEquals("{\"error\":\"Header parameter is missing or null\"}", result);
		assertMockEndpointsSatisfied(); 
		
	}
	
	@Test 
	public void testSimulateConnectionErrorUsingMock() throws Exception
	{          
		outputEndpoint.whenAnyExchangeReceived(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	                exchange.setException(new Exception("Simulated connection error"));
	            }
	        }); 		
		String body= "{\"phoneNumber\":\"8767980967\",\"ruleName\":\"ESB_Test\",\"messageText\":\"Hi\"}";
		String result = (String) template.requestBody("direct:restRoute", body, String.class);
		
		assertEquals("{\"error\":\"Exception occurred. Please check logs.\"}", result);
		assertMockEndpointsSatisfied(); 
		
	}
	
	
	@Test
	public void correctBody() throws Exception {
		String body= "{\"phoneNumber\":\"8767980967\",\"ruleName\":\"ESB_Test\",\"messageText\":\"Hi\"}";
		
		String result = template.requestBody("direct:restRoute", body, String.class);
		
		String response= "{\"success\":\"1 Row Inserted\"}";
		
		assertEquals(response, result);
		assertMockEndpointsSatisfied();
	}

	
	
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			public void configure() throws Exception {
				from("jetty:http://localhost:8086/api/sms").to(outputEndpoint);
			}
		};
	}

	@Override
	protected ClassPathXmlApplicationContext createApplicationContext() {
		return new ClassPathXmlApplicationContext("spring/camel-context-test.xml");
	}

}
