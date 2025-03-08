package com.appcenter.timepiece.global.security;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private final List<String> allowedOrigins;
    private final List<String> allowedMethods;
    private final List<String> allowedHeaders;
    private final Long maxAge;
    private final Boolean allowCredentials;

    public CorsProperties(List<String> allowedOrigins, List<String> allowedMethods,
                          List<String> allowedHeaders, Long maxAge, Boolean allowCredentials) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
        this.maxAge = maxAge;
        this.allowCredentials = allowCredentials;
    }

}