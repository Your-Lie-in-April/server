package com.appcenter.timepiece.global.security;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.repository.MemberRepository;
import com.appcenter.timepiece.global.redis.RefreshToken;
import com.appcenter.timepiece.global.redis.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.front-redirect-url.host}")
    private String frontHost;

    @Value("${spring.front-redirect-url.port}")
    private int frontPort;

    @Value("${spring.front-redirect-url.scheme}")
    private String frontScheme;

    @Value("${spring.front-redirect-url.path}")
    private String frontPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Member member = memberRepository.findByEmail(oAuth2User.getName()).get();

        setTokenAtCookie(request, response, member);
    }

    private void setTokenAtCookie(HttpServletRequest request, HttpServletResponse response, Member member) throws IOException {
        Long memberId = member.getId();
        String email = member.getEmail();

        String accessToken = jwtProvider.createAccessToken(memberId, email, List.of(Role.ROLE_USER.getRole()));
        String refreshToken = jwtProvider.createRefreshToken(memberId, email, List.of(Role.ROLE_USER.getRole()));

        jwtProvider.setCookie(response, accessToken, refreshToken);
        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));

        String uri = createURI().toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI() {
        return UriComponentsBuilder.newInstance()
                .scheme(frontScheme)
                .host(frontHost)
                .port(frontPort)
                .path(frontPath)
                .build()
                .toUri();
    }
}