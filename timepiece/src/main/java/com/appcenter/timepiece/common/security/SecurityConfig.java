package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.ex.MyAccessDeniedHandler;
import com.appcenter.timepiece.common.ex.MyAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.httpBasic((basic)->
                basic.disable());

        httpSecurity.csrf(CsrfConfigurer::disable);

        httpSecurity.sessionManagement((sessionManagement) ->
                sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeHttpRequests(authorize -> authorize.requestMatchers("/user/**").authenticated()
                .requestMatchers("/v1/oauth2/**").permitAll()
                .requestMatchers("/admin/**")
                .hasAnyRole("ADMIN").anyRequest().permitAll());

        return httpSecurity.build();
    }

}
