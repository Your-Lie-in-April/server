package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.exception.JwtExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/members/**").authenticated()
                        .requestMatchers("/v1/oauth2/login-page/google").permitAll()
                        .requestMatchers("/v1/oauth2/login/google").permitAll()
                        .requestMatchers("/v1/oauth2/reissue").hasRole("USER")
                        .requestMatchers("/v1/oauth2/test").hasRole("USER")
                        .requestMatchers("/v1/oauth2/test1","/","/swagger-ui/**", "/v3/api-docs/**").permitAll()

                )
                .addFilterBefore(new JwtExceptionHandlerFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                );

        return httpSecurity.build();
    }

}
