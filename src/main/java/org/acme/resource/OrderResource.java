package org.acme.resource;

import org.acme.camel.dto.OrderDTO;
import org.apache.camel.ProducerTemplate;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


@Path("orders")
// @Produces(APPLICATION_JSON)
public class OrderResource {

    // @Produce(value = "seda://toQueue")
    private final ProducerTemplate producerTemplate;

    @Inject
    public OrderResource(final ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    // @Produces(MediaType.APPLICATION_JSON)
    public void sendOrder(@RequestBody final OrderDTO orderDTO) {
        System.out.println(" --------> ");
        //producerTemplate.asyncSendBody("seda://toQueue", orderDTO);
    }
}

