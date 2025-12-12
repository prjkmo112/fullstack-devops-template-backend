package io.github.prjkmo112.commonembeddeddata;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "maxmind.enabled", havingValue = "true", matchIfMissing = false)
public class MaxMindConfig {
    @Value("classpath:GeoLite2-City.mmdb")
    private Resource dbResource;

    @Bean
    public DatabaseReader geoIpDatabaseReader() throws IOException {
        try (InputStream inputStream = dbResource.getInputStream()) {
            return new DatabaseReader.Builder(inputStream).build();
        }
    }
}
