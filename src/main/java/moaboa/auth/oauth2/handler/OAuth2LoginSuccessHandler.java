package moaboa.auth.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moaboa.auth.jwt.JwtUtil;
import moaboa.auth.oauth2.userinfo.CustomOAuth2User;
import moaboa.auth.user.Role;
import moaboa.auth.user.User;
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
            User user = userRepository.findBySocialId(oAuth2User.getId())
                    .orElseThrow(RuntimeException::new);

            // 처음 요청한 회원
            if (user.getRole() == Role.GUEST) {
                String accessToken = jwtUtil.createAccessToken(user.getId());
                response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);

                jwtUtil.sendAccessAndRefreshToken(response, accessToken, null);
                // 여기서 프런트에 응답을 줘야할 듯
                log.info("리다이렉트");
                response.sendRedirect("/api/updateUser");
            } else {
                loginSuccess(response, user); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, User user) throws IOException {
        log.info("로그인 성공!");
        String accessToken = jwtUtil.createAccessToken(user.getId());
        String refreshToken = jwtUtil.createRefreshToken();
        response.addHeader(jwtUtil.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtUtil.getRefreshHeader(), "Bearer " + refreshToken);

        jwtUtil.sendAccessAndRefreshToken(response, accessToken, refreshToken);
//        jwtUtil.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }

}
