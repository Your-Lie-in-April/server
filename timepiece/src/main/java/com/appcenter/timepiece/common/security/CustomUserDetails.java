package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomUserDetails implements OAuth2User, UserDetails {
    private Long id;
    private String email;
    private List<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    private CustomUserDetails(Long id, String email, List<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public static CustomUserDetails from(Member member) {
        List<SimpleGrantedAuthority> authorities = member.getRole().stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return new CustomUserDetails(member.getId(),
                member.getEmail(),
                authorities);
    }

    public Long getId() {
        return id;
    }

    // UserDetail Override
    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User Override
    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public String toString() {
        return "{" + "id = " + id + ", email = " + email + "}";
    }

}