package org.acme.resource;

import org.acme.camel.config.RabbitmqExchangeConfig;
import org.acme.camel.dto.OrderDTO;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("orders")
public class OrderResource {
    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    private final ProducerTemplate producerTemplate;

    @Inject
    private RabbitmqExchangeConfig rabbitmqExchangeConfig;

    @Inject
    public OrderResource(final ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @POST
    @Consumes
    public void sendOrder(@RequestBody OrderDTO orderDTO) {
        System.out.println(" --------> ");
        producerTemplate.asyncSendBody("seda://toQueue", orderDTO);
    }

    @GET
    @Consumes(MediaType.WILDCARD)
    public void sendOrder() {

        LOG.info(" --> "+ rabbitmqExchangeConfig.getRouter("order"));


        System.out.println(" --------> ");
       // producerTemplate.asyncSendBody("seda://toQueue", "");
    }
}

