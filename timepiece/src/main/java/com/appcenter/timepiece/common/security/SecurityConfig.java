package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.exception.MemberAccessDeniedHandler;
import com.appcenter.timepiece.common.exception.MemberEntryPointHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;


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
                        .requestMatchers("/v1/oauth2/login/getGoogleAuthUrl").permitAll()
                        .requestMatchers("/v1/oauth2/login/google").permitAll()
                        .requestMatchers("/v1/oauth2/reissue").permitAll()
                        .requestMatchers("/v1/oauth2/test").hasRole("USER")
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(new MemberAccessDeniedHandler())
                        .authenticationEntryPoint(new MemberEntryPointHandler())
                )

                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);




        return httpSecurity.build();
    }


}
