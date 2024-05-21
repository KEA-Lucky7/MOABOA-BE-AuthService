package moaboa.auth.token.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import moaboa.auth.error.ErrorCode;
import moaboa.auth.response.ErrorResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

// 인증이 되지않은 유저가 요청을 했을때 동작
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED_CLIENT.getCode(), ErrorCode.UNAUTHORIZED_CLIENT.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonErrorResponse = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(ErrorCode.UNAUTHORIZED_CLIENT.getStatus().value());
        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // application/json
        response.getWriter().write(jsonErrorResponse);
    }
}