package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.service.CustomOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final CustomOAuth2Service customOAuth2Service;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(security -> {
                    security.configurationSource(corsConfigurationSource());
                })
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL).permitAll()
                        .requestMatchers(HttpMethod.GET, PERMIT_USER_GET).hasRole("USER")
                        .requestMatchers(HttpMethod.POST, PERMIT_USER_POST).hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, PERMIT_USER_DELETE).hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, PERMIT_USER_PUT).hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, PERMIT_USER_PATCH).hasRole("USER")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2Service)
                        )
                        .successHandler(oAuth2SuccessHandler))
                .addFilterBefore(new JwtExceptionHandlerFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                );


        return httpSecurity.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    String[] PERMIT_ALL = {
            "/oauth2/**", //oauth2 로그인 서비스 접근
            "/login/**", //oauth2 로그인창
            "/swagger-ui/**", //스웨거 명세
            "/v3/api-docs/**", //스웨거 명세
            "/v1/invitation/{url}" //회원 초대(추가)
    };

    String[] PERMIT_USER_GET = {
            "/v1/members/{memberId}", //회원정보 조회
            "/v1/projects/members/{memberId}", //소속 프로젝트 전체 조회
            "/v1/projects/members/{memberId}/pin", //핀 설정된 프로젝트 조회(+시간표)
            "/v1/projects/members/{memberId}/{keyword}", //유저가 가지고 있는 프로젝트 중 검색
            "/v1/projects/{projectId}/members", //프로젝트에 속해있는 유저 전체 조회
            "/v1/projects/stored", //보관한 프로젝트 목록 조회
            "/v1/projects/{projectId}/schedules", //프로젝트 내 모든 사용자 시간표 조회
            "/v1/projects/{projectId}/members/{memberId}/schedules", //사용자 시간표 조회
            "/v1/projects/invitations" //프로젝트 초대 정보 조회
    };

    String[] PERMIT_USER_POST = {
            "/v1/projects", //프로젝트 생성
            "/v1/projects/{projectId}/invitation", //초대링크 생성
            "/v1/projects/{projectId}/schedules", //시간표 생성
            "/v1/auth/reissue" //리프레시 토큰 재생성
    };

    String[] PERMIT_USER_PUT = {
            "/v1/members/{state}", //상태메시지 설정
            "/v1/projects/members/nickname", //닉네임 재설정
            "/v1/projects/{projectId}", //프로젝트 수정
            "/v1/projects/{projectId}/schedules" //시간표 수정
    };

    String[] PERMIT_USER_PATCH = {
            "/v1/members/storage/{projectId}", //프로젝트 보관-해제
            "/v1/members/pin/{projectId}", //프로젝트 핀 설정-해제
            "/v1/projects/{projectId}/transfer-privilege" //관리자 권한 양도
    };

    String[] PERMIT_USER_DELETE = {
            "/v1/projects/{projectId}", // 프로젝트 삭제
            "/v1/projects/{projectId}/members/{memberId}", //프로젝트에서 회원 강퇴
            "/v1/projects/{projectId}/me", //프로젝트 스스로 나가기
            "/v1/projects/{projectId}/schedules" //시간표 삭제
    };
}