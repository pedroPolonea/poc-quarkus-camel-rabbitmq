package org.acme.camel.consumer.routers;

import org.acme.camel.consumer.processor.OrderProcessor;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderRoute2 extends RouteBuilder {


    public OrderRoute2() {

    }

    @Override
    public void configure() {

        from("timer:fooo?period=10000")
                .routeId("orderProducer")
                .setBody().constant("{\"foo\":\"bar\"}")
            .log("Message to be sent: ${body}")
            .to("rabbitmq://order?addresses={{rabbitmq.url}}" +
                    "&queue={{rabbitmq.queue}}" +
                    "&vhost={{rabbitmq.vhost}}" +
                    "&username={{rabbitmq.username}}" +
                    "&password={{rabbitmq.password}}" +
                    "&exchangeType=topic" +
                    "&autoDelete=false");
    }
}
