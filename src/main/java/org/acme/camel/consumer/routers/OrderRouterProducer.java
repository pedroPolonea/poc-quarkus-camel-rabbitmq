package org.acme.camel.consumer.routers;

import org.acme.camel.consumer.processor.OrderProcessor;
import org.acme.camel.dto.OrderDTO;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderRouterProducer extends RouteBuilder {

    @Override
    public void configure() {
/*
        from("timer:fooo?period=10000")
                .routeId("orderProducer")
                .setBody().constant("{\"id\":1}")
            .log("Message to be sent: ${body}")
            .to("rabbitmq://order?addresses={{rabbitmq.url}}" +
                    "&queue={{rabbitmq.queue}}" +
                    "&vhost={{rabbitmq.vhost}}" +
                    "&username={{rabbitmq.username}}" +
                    "&password={{rabbitmq.password}}" +
                    "&exchangeType=topic" +
                    "&autoDelete=false");

*/

        from("seda://toQueue")
                .routeId("OrderRouterProducer")
                .log("Marshalling Message: ${body}")
                .marshal(new JacksonDataFormat(OrderDTO.class))
                .to("rabbitmq://{{rabbitmq.exchanges.order.name}}" +
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
                .log("Message successfully sent to queue.");


    }
}
