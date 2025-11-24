package io.github.prjkmo112.commoneventlogger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BlockingWebClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder.build();
    }
}
