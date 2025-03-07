package com.appcenter.timepiece;

import com.appcenter.timepiece.global.security.CorsProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableConfigurationProperties(CorsProperties.class)
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server url")})
@EnableAspectJAutoProxy
@SpringBootApplication
public class TimepieceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimepieceApplication.class, args);
    }

}
