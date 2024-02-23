package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.redis.RefreshToken;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    private Key secretKey;

    private final RefreshTokenRepository refreshTokenRepository;

    private final CustomUserDetailsService customUserDetailsService;
    @PostConstruct
    protected void init() {
        log.info("[init] 시크릿키 초기화 시작");
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        log.info("[init] 시크릿키 초기화 성공");
    }


    @Transactional
    public void saveTokenInfo(Long Id, String refreshToken) {
        refreshTokenRepository.save(new RefreshToken(Id, refreshToken));
    }

//    @Transactional
//    public void removeRefreshToken(Long memberId) {
//        refreshTokenRepository.findByMemberId(memberId)
//                .ifPresent(refreshToken -> refreshTokenRepository.delete(refreshToken));
//    }

    public String createRefreshToken(Long id, String email,  List<String> roles) {
        Claims claims = Jwts
                .claims().setSubject(email);
        claims.put("memberId", id);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000L * 60 * 30))//유효시간
                .signWith(SignatureAlgorithm.HS256, secretKey) //HS256알고리즘으로 key를 암호화 해줄것이다.
                .compact();
    }

    public String createAccessToken(Long id,String email, List<String> roles){
        log.info("[createAccessToken] 토큰 생성 시작");
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roles", roles);
        claims.put("memberId", id);

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 1000L * 60 * 3))
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret 값 세팅
                .compact();

        log.info("[createAccessToken] 토큰 생성 완료");
        return token;
    }



    public Authentication getAuthentication(String token) {
        log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getMemberEmail(token));
        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    public String getMemberEmail(String token) {
        log.info("[getMemberEmail] 토큰 기반 회원 구별 정보 추출");
        String info = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        log.info("[getMemberEmail] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    public Long getMemberId(String token){
        log.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        Long info = Long.valueOf(Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("memberId").toString());
        log.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", info);
        return info;
    }

    public String resolveAccessToken(HttpServletRequest request) {
        log.info("[resolveAccessToken] HTTP 헤더에서 AccessToken 값 추출");
        return request.getHeader("Access");
    }

    public String resolveRefreshToken(HttpServletRequest request){
        log.info("[resolveRefreshToken] HTTP 헤더에서 RefreshToken 값 추출");
        return request.getHeader("Refresh");
    }

    public boolean validateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작");
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            log.info("[validateToken] 토큰 유효 체크 성공");
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("[validateToken] 토큰 유효 체크 중 오류 발생");
            return false;
        }
    }
}
