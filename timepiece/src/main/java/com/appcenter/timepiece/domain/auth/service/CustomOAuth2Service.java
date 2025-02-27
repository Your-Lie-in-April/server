package com.appcenter.timepiece.domain.auth.service;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.repository.MemberRepository;
import com.appcenter.timepiece.global.security.OAuth2Attributes;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Attributes attributes = OAuth2Attributes.of(provider, originAttributes);
        Member member = saveOrUpdate(attributes);
        List<? extends GrantedAuthority> authorities = getAuthorities(member);
        return new DefaultOAuth2User(authorities, attributes.getAttributes(), attributes.getNameAttributesKey());
    }

    private Member saveOrUpdate(OAuth2Attributes authAttributes) {
        Optional<Member> member = memberRepository.findByEmailAndProvider(authAttributes.getEmail(),
                authAttributes.getProvider());
        Member returnMember;

        if (member.isPresent()) {
            returnMember = member.get().updateMember(authAttributes.getName(), authAttributes.getProfileImageUrl());
        } else {
            returnMember = new Member(authAttributes.getProvider(), authAttributes.getName(),
                    authAttributes.getEmail(), "", authAttributes.getProfileImageUrl(), List.of("ROLE_USER"));
        }

        return memberRepository.save(returnMember);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Member member){
        List<String> roles = member.getRole();
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }
}