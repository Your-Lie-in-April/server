package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    private final JwtProvider jwtProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, originAttributes);
        saveOrUpdate(attributes);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOAuth2User(authorities, originAttributes, "email");
    }

    private Member saveOrUpdate(OAuthAttributes authAttributes) {
        Optional<Member> member = memberRepository.findByEmail(authAttributes.getEmail());
        Member returnMember;
        if (member.isEmpty()) {
            returnMember = new Member("Google", authAttributes.getName(),
                    authAttributes.getEmail(), "", authAttributes.getProfileImageUrl(), List.of("ROLE_USER"));
        } else {
            returnMember = member.get().updateMember(authAttributes.getName(), authAttributes.getProfileImageUrl());
        }

        return memberRepository.save(returnMember);
    }
}