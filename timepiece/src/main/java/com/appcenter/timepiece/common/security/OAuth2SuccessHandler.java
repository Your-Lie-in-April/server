package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.redis.RefreshToken;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        System.out.println(oAuth2User.getName());
        Member member = memberRepository.findByOauth2Id(oAuth2User.getName()).get();

        redirectToken(request, response, member);
    }

    private void redirectToken(HttpServletRequest request, HttpServletResponse response, Member member) throws IOException {

        Long memberId = member.getId();
        String email = member.getEmail();

        String accessToken = jwtProvider.createAccessToken(memberId, email, List.of(Role.ROLE_USER.getRole()));
        String refreshToken = jwtProvider.createRefreshToken(memberId, email, List.of(Role.ROLE_USER.getRole()));

        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));

        String uri = createURI(accessToken, refreshToken, memberId).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String accessToken, String refreshToken, Long memberId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("member_id", String.valueOf(memberId));
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/")
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}