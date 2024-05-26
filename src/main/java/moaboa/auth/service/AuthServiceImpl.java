package moaboa.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moaboa.auth.global.error.ErrorCode;
import moaboa.auth.global.error.TokenException;
import moaboa.auth.token.jwt.JwtUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtUtil jwtUtil;

    private static final String USER_HEADER = "User";

    @Override
    public void validateToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST));
        response.addHeader(
                USER_HEADER,
                jwtUtil.extractId(accessToken)
                        .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST))
        );
    }
}