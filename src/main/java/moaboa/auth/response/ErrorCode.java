package moaboa.auth.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED_CLIENT("AUT-ERR-001", HttpStatus.UNAUTHORIZED, "잘못된 요청입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
