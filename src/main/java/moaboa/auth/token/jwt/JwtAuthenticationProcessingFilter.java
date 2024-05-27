package moaboa.auth.token.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.global.error.ErrorCode;
import moaboa.auth.global.error.TokenException;
import moaboa.auth.token.refresh.RefreshTokenRepository;
import moaboa.auth.member.Member;
import moaboa.auth.member.repository.query.MemberQueryRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String[] NO_CHECK_URL = {"/login/**", "/auth/health", "/auth/token"};
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtUtil jwtUtil;
    private final MemberQueryRepository memberQueryRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        for (String pattern : NO_CHECK_URL) {
            if (pathMatcher.match(pattern, requestURI)) {
                filterChain.doFilter(request, response);
                return; // 이후 현재 필터 진행 막기
            }
        }

        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken이 없거나 유효 X or DB에 저장된 RefreshToken과 다르다면 => null 반환
        // 사용자의 요청 헤더에 RefreshToken이 있는 경우는, AccessToken이 만료되어 요청한 경우
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken은 모두 null
        String refreshToken = jwtUtil.extractRefreshToken(request)
                .orElse(null);

        // 리프레시 토큰이 요청 헤더에 존재했다면, 사용자가 AccessToken이 만료되어서
        // RefreshToken까지 보낸 것이므로 리프레시 토큰이 DB의 리프레시 토큰과 일치하는지 판단 후,
        // 일치한다면 AccessToken을 재발급
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // RefreshToken을 보낸 경우에는 AccessToken을 재발급 하고 인증 처리는 하지 않게 하기위해 바로 return으로 필터 진행 막기
        }

        // RefreshToken이 없거나 유효하지 않다면, AccessToken을 검사하고 인증을 처리하는 로직 수행
        // AccessToken이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필터로 넘어가기 때문에 403 에러 발생
        // AccessToken이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     *  파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     *  jwtUtil.createAccessToken()으로 AccessToken 생성,
     *  reIssueRefreshToken()로 기존 리프레시 토큰 삽입 or 생성
     *  그 후 jwtUtil.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        Long memberId = refreshTokenRepository.findMemberIdByToken(refreshToken)
                .orElseThrow(() -> new TokenException(ErrorCode.EXPIRED_REFRESH_TOKEN));
        jwtUtil.sendAccessAndRefreshToken(response, jwtUtil.reIssueAccessToken(memberId), jwtUtil.getRefreshToken(memberId));
    }

    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        Optional<Member> user = jwtUtil.extractAccessToken(request)
                .filter(jwtUtil::isAccessTokenValid)
                .flatMap(accessToken -> jwtUtil.extractId(accessToken)
                        .flatMap(id -> memberQueryRepository.findById(Long.parseLong(id))));
                user.ifPresent(this::saveAuthentication);

        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(Member member) {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .roles(member.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
