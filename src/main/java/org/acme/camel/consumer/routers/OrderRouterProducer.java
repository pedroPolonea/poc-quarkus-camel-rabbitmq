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
                .to("rabbitmq://order?addresses={{rabbitmq.url}}" +
                        "&queue={{rabbitmq.queue}}" +
                        "&vhost={{rabbitmq.vhost}}" +
                        "&username={{rabbitmq.username}}" +
                        "&password={{rabbitmq.password}}" +
                        "&exchangeType=topic" +
                        "&autoDelete=false")
                .log("Message successfully sent to queue.");


    }
}
