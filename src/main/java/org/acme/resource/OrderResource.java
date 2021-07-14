package org.acme.resource;

import org.acme.camel.dto.OrderDTO;
import org.apache.camel.ProducerTemplate;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("orders")
@Produces(APPLICATION_JSON)
public class OrderResource {

    private final ProducerTemplate producerTemplate;

    @Inject
    public OrderResource(final ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response produce(final OrderDTO orderDTO) {
        producerTemplate.asyncSendBody("timer:fooo", orderDTO);
        return Response.accepted().build();
    }
}

