package org.acme.camel.consumer.routers;

import org.acme.camel.dto.OrderDTO;
import org.acme.camel.consumer.processor.OrderProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderRoute extends RouteBuilder {
    private OrderProcessor orderProcessor;

    @Inject
    public OrderRoute(final OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void configure() {
     from("rabbitmq://order?addresses={{rabbitmq.url}}" +
                "&queue={{rabbitmq.queue}}" +
                "&vhost={{rabbitmq.vhost}}" +
                "&username={{rabbitmq.username}}" +
                "&password={{rabbitmq.password}}" +
                "&exchangeType=topic" +
                "&autoDelete=false")
                .routeId("OrderRouterConsumer")
                 .unmarshal(new JacksonDataFormat(OrderDTO.class))
                .log("Received Message: ${body}")
                .process(orderProcessor)
                .log("Finished. Person successfully created in database: ${body}");


    }
}
