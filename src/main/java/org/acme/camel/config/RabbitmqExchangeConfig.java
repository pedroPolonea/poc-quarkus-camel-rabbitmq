package org.acme.camel.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Set;

@ConfigMapping(prefix = "rabbitmq")
public interface RabbitmqExchangeConfig {
    String url();
    String vhost();
    String username();
    String password();
    Exchanges exchanges();

    interface Exchanges {
        Set<Exchange> exchange();
    }

    interface Exchange {
        String name();
        String type();
        @WithDefault("false")
        boolean autoDelete();
        @WithDefault("3")
        int numberConsumers();
        @WithDefault("3")
        int numberRetries();
        @WithDefault("3000")
        int redeliveryDelay();
        @WithDefault("-1")
        int maximumRedeliveries();

    }

    default String getRouter(final Exchange exchange){
        final String ROUTER = """
                        rabbitmq://%5$s
                        ?addresses=%1$s
                        &queue=%5$s.queue
                        &vhost=%4$s
                        &username=%2$s
                        &password=%3$s
                        &exchangeType=%6$s
                        &concurrentConsumers=%7$s
                        &autoDelete=%8$s
                        &deadLetterExchange=%5$s
                        &deadLetterExchangeType=%6$s
                        &deadLetterQueue=%5$s.dlq
                        &deadLetterRoutingKey=%5$s.dlq
                        &autoAck=false
                    """.replaceAll("\n|\s", "");

        return exchanges().exchange()
                .stream()
                .filter( ex -> ex.equals(exchange))
                .findFirst()
                .map(ex -> {
                    return String.format(ROUTER,
                            url(),
                            username(),
                            password(),
                            vhost(),
                            ex.name(),
                            ex.type(),
                            ex.numberConsumers(),
                            ex.autoDelete()
                            );
                }).orElseGet(() -> null);
    }

    default String getRouter(final String exchange){
        final String ROUTER = """
                        rabbitmq://%5$s
                        ?addresses=%1$s
                        &queue=%5$s.queue
                        &vhost=%4$s
                        &username=%2$s
                        &password=%3$s
                        &exchangeType=%6$s
                        &concurrentConsumers=%7$s
                        &autoDelete=%8$s
                        &deadLetterExchange=%5$s
                        &deadLetterExchangeType=%6$s
                        &deadLetterQueue=%5$s.dlq
                        &deadLetterRoutingKey=%5$s.dlq
                        &autoAck=false
                    """.replaceAll("\n|\s", "");

        return exchanges().exchange()
                .stream()
                .filter( ex -> ex.name().equalsIgnoreCase(exchange))
                .findFirst()
                .map(ex -> {
                    return String.format(ROUTER,
                            url(),
                            username(),
                            password(),
                            vhost(),
                            ex.name(),
                            ex.type(),
                            ex.numberConsumers(),
                            ex.autoDelete()
                    );
                }).orElseGet(() -> null);
    }
}
