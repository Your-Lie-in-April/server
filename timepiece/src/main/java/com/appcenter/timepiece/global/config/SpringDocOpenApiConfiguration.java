package com.appcenter.timepiece.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "default generated url")})
@Configuration
public class SpringDocOpenApiConfiguration {
    @Bean
    public OpenAPI openAPI() {
        // API 요청헤더에 인증정보 포함
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("JWT");

        // Security 스키마 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        return new OpenAPI()
                // Security 인증 컴포넌트 설정
                .components(new Components().addSecuritySchemes("JWT", bearerAuth))
                // API 마다 Security 인증 컴포넌트 설정
                .addSecurityItem(securityRequirement)
                .info(info());
    }


    private Info info() {
        return new Info()
                .title("Timepiece 프로젝트 API Document")
                .version("v0.1")
                .description("Timepiece 프로젝트의 API 명세서입니다.");
    }

}