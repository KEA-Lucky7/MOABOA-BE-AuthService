package moaboa.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.jwt.JwtUtil;
import moaboa.auth.oauth2.SocialLoginUser;
import moaboa.auth.oauth2.userinfo.CustomOAuth2User;
import moaboa.auth.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if (userRepository.findByEmail(oAuth2User.getEmail()).isEmpty()) {
                log.info("회원가입 로직");
                String accessToken = jwtUtil.createAccessToken(oAuth2User.getEmail());
                response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);
//                response.sendRedirect("/login"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                jwtUtil.sendAccessAndRefreshToken(response, accessToken, null);
                // 여기서 멤버 생성 로직 호출
                SocialLoginUser socialLoginUser = SocialLoginUser.from(oAuth2User);
            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
            getRedirectStrategy().sendRedirect(request, response, "/");
        } catch (Exception e) {
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        log.info("로그인 성공!");
        String accessToken = jwtUtil.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtUtil.createRefreshToken(oAuth2User.getEmail());
        response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtUtil.getRefreshHeader(), "Bearer " + refreshToken);

        jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtUtil.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
