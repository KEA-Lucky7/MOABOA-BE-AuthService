package moaboa.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.global.error.ErrorCode;
import moaboa.auth.global.error.TokenException;
import moaboa.auth.member.Member;
import moaboa.auth.member.Role;
import moaboa.auth.member.repository.command.MemberCommandRepository;
import moaboa.auth.oauth2.userinfo.CustomOAuth2User;
import moaboa.auth.token.jwt.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final MemberCommandRepository memberCommandRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            Member member = memberCommandRepository.findBySocialId(oAuth2User.getId())
                    .orElseThrow(() -> new TokenException(ErrorCode.NOT_EXIST_USER));

            // 처음 요청한 회원
            if (member.getRole() == Role.GUEST) {
                String accessToken = jwtUtil.createAccessToken(member.getId());
                String refreshToken = jwtUtil.setRefreshToken(member.getId());

                jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken);

                String redirectUrl = "https://moaboa.shop/login/redirect?access_code="+ accessToken + "&redirect_code=" + refreshToken;

                // 프런트에 회원가입 페이지 리다이렉션
                response.sendRedirect(redirectUrl);
            } else {
                loginSuccess(response, member); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loginSuccess(HttpServletResponse response, Member member) {
        log.info("로그인 성공!");
        String accessToken = jwtUtil.createAccessToken(member.getId());
        String refreshToken = jwtUtil.setRefreshToken(member.getId());
        response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtUtil.getRefreshHeader(), refreshToken);

        jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    }

}
