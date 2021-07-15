package org.acme.camel.consumer.processor;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import org.acme.camel.dto.OrderDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderProcessor implements Processor {
    private static final Logger log = LoggerFactory.getLogger(OrderProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        final var personMessage = exchange.getIn().getBody(OrderDTO.class);
        log.info("M=process, personMessage={} ", personMessage);
    }
}
