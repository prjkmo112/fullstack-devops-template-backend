package io.github.prjkmo112.commonmysqldb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = { "io.github.prjkmo112.commonmysqldb.repository" })
@EntityScan(basePackages = { "io.github.prjkmo112.commonmysqldb.entity" })
public class DBConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.pool-size}")
    private Integer poolSize;

    @Primary
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(poolSize);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        return new HikariDataSource(config);
    }
}
