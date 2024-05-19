package moaboa.auth.token.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.token.refresh.RefreshTokenRepository;
import moaboa.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;


    public String createAccessToken(Long id) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("id", id)
                .subject(ACCESS_TOKEN_SUBJECT)
                .issuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * AccessToken 재발급
     */
    public String reIssueAccessToken(Long id) {
        log.info("Access Token 재발급");
        return createAccessToken(id);
    }

    private String createRefreshToken(Long id) {
        String key = UUID.randomUUID().toString();
        refreshTokenRepository.save(key, id);
        return key;
    }

    public String getRefreshToken(Long id) {
        Long userId = userRepository.findById(id)
                .orElseThrow(RuntimeException::new)
                .getId();

        return findRefreshToken(userId);
    }

    /**
     * 리프레시 토큰 가져오는 메소드
     * redis에 존재하는 경우 가져오고 존재하지 않다면 새로 생성
     * */
    private String findRefreshToken(Long userId) {
        return refreshTokenRepository.findById(userId)
                .orElseGet(() -> {
                    log.info("refreshToken 재발급");
                    return createRefreshToken(userId);
                });
    }

    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        log.info("액세스 헤더: {}", request.getHeader(accessHeader));
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader));
    }

    public Optional<String> extractId(String accessToken) {
        try {
            log.info("토큰 아이디 추출");
            return Optional.ofNullable(Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("id", String.class));
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // 토큰 파싱이 성공적으로 이루어졌다면 토큰은 유효
            log.info("토큰 유효");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 액세스 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
