package moaboa.auth.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import moaboa.auth.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("AUT-ERR-001", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED_CLIENT("AUT-ERR-002", HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다"),
    EXPIRED_ACCESS_TOKEN("AUT-ERR-003", HttpStatus.UNAUTHORIZED, "만료된 액세스 토큰입니다"),
    EXPIRED_REFRESH_TOKEN("AUT-ERR-004", HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
