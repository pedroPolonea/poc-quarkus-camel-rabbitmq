package org.acme.camel.consumer.routers;

import com.rabbitmq.client.ConnectionFactory;
import org.acme.camel.config.RabbitmqExchangeConfig;
import org.acme.camel.consumer.processor.OrderProcessor;
import org.acme.camel.dto.OrderDTO;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderRouter extends RouteBuilder {

    private RabbitmqExchangeConfig rabbitmqExchangeConfig;

    private OrderProcessor orderProcessor;

    @Inject
    public OrderRouter(final OrderProcessor orderProcessor, final RabbitmqExchangeConfig rabbitmqExchangeConfig) {
        this.orderProcessor = orderProcessor;
        this.rabbitmqExchangeConfig = rabbitmqExchangeConfig;
    }

    @Override
    public void configure() {
       // getContext().getRegistry().bind("rabbitConnectionFactory", ConnectionFactory.class);

        /*
     from("rabbitmq://{{rabbitmq.exchanges.order.name}}" +
                "?addresses={{rabbitmq.url}}" +
                "&queue={{rabbitmq.exchanges.order.name}}.queue" +
                "&vhost={{rabbitmq.vhost}}" +
                "&username={{rabbitmq.username}}" +
                "&password={{rabbitmq.password}}" +
                "&exchangeType={{rabbitmq.exchanges.order.type}}" +
                //"&reQueue=true"+
                "&concurrentConsumers=5"+
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
                     //.asyncDelayedRedelivery()
                     .useOriginalMessage()
                     .redeliveryDelay(3000) // wait 5 secs to redeliver and requeue
                     .maximumRedeliveries(-1)

                     .retryWhile(exchange -> (Integer)exchange.getIn().getHeader("CamelRedeliveryCounter") < 3)
                    //.handled(true).transform().constant("Sorry")
                     //.maximumRedeliveryDelay(60*60*1000)
                     //.setHeader(RabbitMQConstants.REQUEUE, constant(true))
                     //.handled(true)

                     .end()
                .process(orderProcessor)
                .log("Finished. Person successfully created in database: ${body}");



        from(URL_QUEUE_ORDER2)
                .routeId("OrderRouterConsumer")
                .unmarshal(new JacksonDataFormat(OrderDTO.class))
                .log("Received Message: ${body}")
                .onException(RuntimeException.class)
                .log("Error for ${body}! Requeue")
                .asyncDelayedRedelivery()
                //.useOriginalMessage()
                .redeliveryDelay(redeliveryDelay) // wait 5 secs to redeliver and requeue
                .maximumRedeliveries(maximumRedeliveries)
                .retryWhile(this::isRetry)
                //.retryWhile(exchange -> (Integer)exchange.getIn().getHeader("CamelRedeliveryCounter") < 3)
                //.handled(true).transform().constant("Sorry")
                //.maximumRedeliveryDelay(60*60*1000)
                //.setHeader(RabbitMQConstants.REQUEUE, constant(true))
                //.handled(true)

                .end()
                .process(orderProcessor)
                .log("Finished. Person successfully created in database: ${body}");
*/


        final RabbitmqExchangeConfig.Exchange order = rabbitmqExchangeConfig.exchanges()
                .exchange()
                .stream()
                .filter(exchange -> exchange.name().equalsIgnoreCase("order"))
                .findFirst().orElseGet(() -> null);

        from(rabbitmqExchangeConfig.getRouter(order))
                .routeId("OrderRouterConsumer")
                .unmarshal(new JacksonDataFormat(OrderDTO.class))
                .log("Received Message: ${body}")
                .onException(RuntimeException.class)
                .log("Error for ${body}! Requeue")
                .asyncDelayedRedelivery()
                .redeliveryDelay(order.redeliveryDelay()) // wait 5 secs to redeliver and requeue
                .maximumRedeliveries(order.maximumRedeliveries())
                .retryWhile(exchange -> isRetry(exchange, order.numberRetries()))
                .end()
                .process(orderProcessor)
                .log("Finished. Person successfully created in database: ${body}");

        from("seda://toQueue")
                .routeId("OrderRouterProducer")
                .log("Marshalling Message: ${body}")
                .marshal(new JacksonDataFormat(OrderDTO.class))
                .to(rabbitmqExchangeConfig.getRouter(order))
                .log("Message successfully sent to queue.");

    }



    private boolean isRetry(final Exchange exchange, final int numberRetries) {
        final Exception exception = exchange.getException();
        if (exception instanceof RuntimeException) {
            if((Integer)exchange.getIn().getHeader("CamelRedeliveryCounter") < numberRetries){
                return true;
            }
        }
        return false;
    };
}
