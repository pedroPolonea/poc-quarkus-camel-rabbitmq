package org.acme.camel.consumer.routers;

import org.acme.camel.consumer.processor.OrderProcessor;
import org.acme.camel.dto.OrderDTO;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class OrderRouterConsumer extends RouteBuilder {

    protected static final String  URL_QUEUE_ORDER = """
                    rabbitmq://{{rabbitmq.exchanges.order.name}}
                    ?addresses={{rabbitmq.url}}
                    &queue={{rabbitmq.exchanges.order.name}}.queue
                    &vhost={{rabbitmq.vhost}}
                    &username={{rabbitmq.username}}
                    &password={{rabbitmq.password}}
                    &exchangeType={{rabbitmq.exchanges.order.type}}
                    &concurrentConsumers={{rabbitmq.exchanges.order.number-consumers}}
                    &autoDelete={{rabbitmq.exchanges.order.auto-delete}}
                    &deadLetterExchange={{rabbitmq.exchanges.order.name}}
                    &deadLetterExchangeType={{rabbitmq.exchanges.order.type}}
                    &deadLetterQueue={{rabbitmq.exchanges.order.name}}.dlq
                    &deadLetterRoutingKey={{rabbitmq.exchanges.order.name}}.dlq
                    &autoAck=false
                """.replaceAll("\n|\s", "");

    @ConfigProperty(name = "{rabbitmq.exchanges.order.number-consumers}", defaultValue = "3")
    private static int numberRetries;

    @ConfigProperty(name = "{rabbitmq.exchanges.order.redelivery-delay}", defaultValue = "5000")
    private static int redeliveryDelay;

    @ConfigProperty(name = "{rabbitmq.exchanges.order.maximum-redeliveries}", defaultValue = "-1")
    private static int maximumRedeliveries;



    private OrderProcessor orderProcessor;

    @Inject
    public OrderRouterConsumer(final OrderProcessor orderProcessor) {
        this.orderProcessor = orderProcessor;
    }

    @Override
    public void configure() {
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

*/

        from(URL_QUEUE_ORDER)
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

    }

    private boolean isRetry(final Exchange exchange) {
        final Exception exception = exchange.getException();
        if (exception instanceof RuntimeException) {
            if((Integer)exchange.getIn().getHeader("CamelRedeliveryCounter") < numberRetries){
                return true;
            }
        }
        return false;
    };
}
