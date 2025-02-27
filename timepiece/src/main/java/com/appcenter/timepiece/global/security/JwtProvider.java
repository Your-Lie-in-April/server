package com.appcenter.timepiece.global.security;

import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.JwtEmptyException;
import com.appcenter.timepiece.global.exception.MismatchTokenTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService;
    @Value("${spring.jwt.secret}")
    private String rawSecretKey;

    @Value("${spring.jwt.refresh-token-valid-time}")
    private Long refreshTokenValidTime;

    @Value("${spring.jwt.access-token-valid-time}")
    private Long accessTokenValidTime;

    private Key secretKey;

    private final String ACCESS_TOKEN = "accessToken";
    private final String REFRESH_TOKEN = "refreshToken";

    @PostConstruct
    protected void init() {
        log.info("[init] 시크릿키 초기화 시작");
        rawSecretKey = Base64.getEncoder().encodeToString(rawSecretKey.getBytes(StandardCharsets.UTF_8));
        secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(rawSecretKey));
        log.info("[init] 시크릿키 초기화 성공");
    }

    public void setCookie(HttpServletResponse response, String accessToken, String refreshToken){
        // Access Token 쿠키
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(convertMilliSecondsToSeconds(accessTokenValidTime))
                .sameSite("Strict")
                .build();

        // Refresh Token 쿠키
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(convertMilliSecondsToSeconds(refreshTokenValidTime))
                .sameSite("Strict")
                .build();

        // 응답에 쿠키 추가
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    public String createAccessToken(Long id, String email, List<String> roles) {
        return createToken(id, email, roles, ACCESS_TOKEN, accessTokenValidTime);
    }

    public String createRefreshToken(Long id, String email, List<String> roles) {
        return createToken(id, email, roles, REFRESH_TOKEN, refreshTokenValidTime);
    }

    private String createToken(Long id, String email, List<String> roles, String tokenType, long validTime) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("memberId", id);
        claims.put("roles", roles);
        claims.put("type", tokenType);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        log.info("[getAuthentication] 토큰 인증 정보 조회 시작");
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getMemberEmail(token));
        log.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails UserName : {}",
                userDetails.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    private String getMemberEmail(String token) {
        log.info("[getMemberEmail] 토큰 기반 회원 구별 정보 추출");
        Claims claims = getTokenClaims(token);
        String email = claims.getSubject();
        log.info("[getMemberEmail] 토큰 기반 회원 구별 정보 추출 완료, info : {}", email);
        return email;
    }

    //Json 파서가 memberId를 Integer로 강제 형변환하는 문제 해결을 위해 Number로 Long 형변환 후 반환
    //oauth2 로그인 과정에서의 토큰 발급엔 잘 동작하지만, accessToken reissue 과정에서 위와 같은 문제 발생
    public Long getMemberId(String token) {
        log.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
        Object memberIdObj = tokenParser(token).getBody().get("memberId");
        Long memberId = ((Number) memberIdObj).longValue();
        log.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료, info : {}", memberId);
        return memberId;
    }

    public void validAccessToken(String token) {
        String tokenType = (String) getTokenClaims(token).get("type");
        if (!tokenType.equals(ACCESS_TOKEN)) {
            throw new MismatchTokenTypeException(ExceptionMessage.TOKEN_TYPE_INVALID.getMessage());
        }
    }

    public void validRefreshToken(String token) {
        String tokenType = (String) getTokenClaims(token).get("type");
        if (!tokenType.equals(REFRESH_TOKEN)) {
            throw new MismatchTokenTypeException(ExceptionMessage.TOKEN_TYPE_INVALID.getMessage());
        }
    }

    public String getAccessToken(HttpServletRequest request) {
        log.info("[getAccessToken] 쿠키에서 Token 값 추출");
        return getAuthorizationToken(request, ACCESS_TOKEN);
    }

    public String getRefreshToken(HttpServletRequest request) {
        log.info("[getRefreshToken] 쿠키에서 Token 값 추출");
        return getAuthorizationToken(request, REFRESH_TOKEN);
    }

    public String getAuthorizationToken(HttpServletRequest request, String tokenType){
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> tokenType.equals(cookie.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);
        }
        return null;
    }

    public boolean validDateToken(String token) {
        log.info("[validateToken] 토큰 유효 체크 시작");
        Jws<Claims> claims = tokenParser(token);

        if (!claims.getBody().getExpiration().before(new Date())) {
            log.info("[validDateToken] 토큰 유효성 체크 성공");
            return true;
        } else {
            log.info("[validDateToken] 토큰 유효성 체크 실패");
            return false;
        }
    }

    public Jws<Claims> tokenParser(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
    }

    public Claims getTokenClaims(String token){
        Jws<Claims> tokenJws = tokenParser(token);
        return tokenJws.getBody();
    }

    public Long convertMilliSecondsToSeconds(Long time){
        return time / 1000;
    }
}