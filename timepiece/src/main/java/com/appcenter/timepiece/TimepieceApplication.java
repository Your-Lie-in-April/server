package com.appcenter.timepiece;

import com.appcenter.timepiece.common.security.CorsProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableConfigurationProperties(CorsProperties.class)
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server url")})
@EnableJpaAuditing
@EnableAspectJAutoProxy
@SpringBootApplication
public class TimepieceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimepieceApplication.class, args);
    }

}
