package io.github.prjkmo112.mainapi;

import io.github.prjkmo112.commonmysqldb.DBConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@Import({ DBConfig.class })
@ComponentScan(basePackages = {
        "io.github.prjkmo112.mainapi",
        "io.github.prjkmo112.commoneventlogger",
        "io.github.prjkmo112.commonembeddeddata"
})
@EnableWebSecurity
@EnableWebMvc
public class MainApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApiApplication.class, args);
    }

}
