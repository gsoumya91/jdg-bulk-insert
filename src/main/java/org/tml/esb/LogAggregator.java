package org.tml.esb;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static net.logstash.logback.argument.StructuredArguments.*;

public class LogAggregator {

	 private static final Logger logger = LoggerFactory.getLogger(LogAggregator.class);

	  public void sourceIp(String message, Exchange exchange){
	    String sourceIp = exchange.getIn().getHeader("X-Forwarded-For", String.class);
	    logger.info("source-ip", value("source-ip",message + sourceIp));
	  }

	  public void request(String message, Exchange exchange){
		  logger.info("request", value("request",message + "\n" + exchange.getIn().getBody(String.class)));
	  }

	  public void response(String message, Exchange exchange) {
		  logger.info("response", value("response",message + "\n" + exchange.getIn().getBody(String.class)));
	  }
}
