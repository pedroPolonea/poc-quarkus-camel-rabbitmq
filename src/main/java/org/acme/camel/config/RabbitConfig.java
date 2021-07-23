package org.acme.camel.config;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@ApplicationScoped
public class RabbitConfig {

        @Inject
        private RabbitmqExchangeConfig rabbitmqExchangeConfig;

        @Produces
        public ConnectionFactory rabbitConnectionFactory(){
                final ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setVirtualHost(rabbitmqExchangeConfig.vhost());
                connectionFactory.setUsername(rabbitmqExchangeConfig.username());
                connectionFactory.setPassword(rabbitmqExchangeConfig.password());
                try {
                        connectionFactory.setUri(rabbitmqExchangeConfig.url());
                } catch (URISyntaxException e) {
                        e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                } catch (KeyManagementException e) {
                        e.printStackTrace();
                }

                return connectionFactory;
        }
}
