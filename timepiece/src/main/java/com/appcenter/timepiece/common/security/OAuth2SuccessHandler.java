package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.dto.TokenDto;
import com.appcenter.timepiece.repository.MemberRepository;
import com.appcenter.timepiece.service.CustomOAuth2UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        log.info("[onAuthenticationSuccess] 회원가입 로직 시작");
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        Optional<Member> member = memberRepository.findByEmail(oAuth2User.getAttributes().get("email").toString());
        List<String> role = new ArrayList<>();

        role.add("ROLE_USER");

        if(!member.isPresent()){

            Member registerMember = Member.builder()
                    .role(role)
                    .provider("Google")
                    .nickname(String.valueOf(oAuth2User.getAttributes().get("name")))
                    .profileImageUrl(String.valueOf(oAuth2User.getAttributes().get("picture")))
                    .state("")
                    .email(String.valueOf(oAuth2User.getAttributes().get("email")))
                    .build();

            memberRepository.save(registerMember);

        }

        Token token = tokenService.generateToken(oAuth2User.getAttributes().get("email").toString(), "USER");
        log.info("{}", token);

        writeTokenResponse(response, token);
    }

    private void writeTokenResponse(HttpServletResponse response, Token token)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        response.addHeader("Access", token.getAccessToken());
        response.addHeader("Refresh", token.getRefreshToken());

        log.info("access token: {}", token.getAccessToken());
        log.info("Refresh token: {}", token.getRefreshToken());

        response.setContentType("application/json;charset=UTF-8");

        var writer = response.getWriter();
        writer.println(objectMapper.writeValueAsString(token));
        writer.flush();
    }
}
