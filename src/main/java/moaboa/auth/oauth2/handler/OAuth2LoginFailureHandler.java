package moaboa.auth.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2Error oauth2Error = ((OAuth2AuthenticationException) exception).getError();
            String errorCode = oauth2Error.getErrorCode();
            String errorMessage = oauth2Error.getDescription();
            log.error("OAuth2 Error Code: {}, Error Description: {}", errorCode, errorMessage);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"" + errorCode + "\", \"error_description\": \"" + errorMessage + "\"}");
            out.flush();
        } else {
            log.error("Authentication failed: {}", exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"unauthorized\", \"error_description\": \"" + exception.getMessage() + "\"}");
            out.flush();
        }

        log.info("Authentication failure: {}", exception.getMessage());
        errorTrace(exception);
    }


    private void errorTrace(AuthenticationException exception) {
        // 스택 트레이스를 로그에 출력
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        log.warn("Exception stack trace: \n{}", stackTrace);
    }
}
