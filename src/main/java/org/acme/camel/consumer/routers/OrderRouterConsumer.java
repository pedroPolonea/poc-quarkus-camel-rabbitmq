package org.acme.camel.consumer.routers;

import org.acme.camel.consumer.processor.OrderProcessor;
import org.acme.camel.dto.OrderDTO;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.rabbitmq.RabbitMQConstants;

import javax.enterprise.context.ApplicationScoped;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.Collections;
import java.util.Map;

@ApplicationScoped
public class OrderRouterConsumer extends RouteBuilder {
    private OrderProcessor orderProcessor;

    @Inject
    public OrderRouterConsumer(final OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void configure() {
     from("rabbitmq://{{rabbitmq.exchanges.order.name}}" +
                "?addresses={{rabbitmq.url}}" +
                "&queue={{rabbitmq.exchanges.order.name}}.queue" +
                "&vhost={{rabbitmq.vhost}}" +
                "&username={{rabbitmq.username}}" +
                "&password={{rabbitmq.password}}" +
                "&exchangeType={{rabbitmq.exchanges.order.type}}" +
                //"&reQueue=true"+
                "&autoDelete={{rabbitmq.exchanges.order.auto-delete}}" +
                "&arg.queue.x-message-ttl=20000"+
                "&deadLetterExchange={{rabbitmq.exchanges.order.name}}"+
                "&deadLetterExchangeType={{rabbitmq.exchanges.order.type}}"+
                "&deadLetterQueue={{rabbitmq.exchanges.order.name}}.dlq"+
                "&deadLetterRoutingKey={{rabbitmq.exchanges.order.name}}.dlq"+
                "&autoAck=false")
                .routeId("OrderRouterConsumer")
                 .unmarshal(new JacksonDataFormat(OrderDTO.class))
                .log("Received Message: ${body}")
             .onException(RuntimeException.class)
             .log("Error for ${body}! Requeue")
             .asyncDelayedRedelivery()
             //.useOriginalMessage()
             .redeliveryDelay(5000) // wait 5 secs to redeliver and requeue
             .maximumRedeliveries(3)
             .end()
                .process(orderProcessor)
                .log("Finished. Person successfully created in database: ${body}");


    }

    @Produces
    public Map<String, Object> bindArgsBuilder() {
        return Collections.singletonMap("foo", "bar");
    }
}
