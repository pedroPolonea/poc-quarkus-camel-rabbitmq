package org.acme.camel.consumer.routers;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

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



        from("seda://toQueue")
                .routeId("OrderRouterProducer")
                .log("Marshalling Message: ${body}")
                .marshal(new JacksonDataFormat(OrderDTO.class))
                .to(URL_QUEUE_ORDER)
                .log("Message successfully sent to queue.");

 */



    }


}
