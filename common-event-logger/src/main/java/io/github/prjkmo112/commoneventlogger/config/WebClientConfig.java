package io.github.prjkmo112.commoneventlogger.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${event-log.client.max-connection:500}")
    private int maxConnection;

    @Value("${event-log.client.timeout}")
    private int timeout;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("event-logger-pool")
                .maxConnections(maxConnection)
                .pendingAcquireTimeout(Duration.ofMillis(10 * 1000))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .followRedirect(false)
                .responseTimeout(Duration.ofMillis(timeout));

        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
