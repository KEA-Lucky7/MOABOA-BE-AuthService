package moaboa.auth.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    BAD_REQUEST("AUT-ERR-001", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    UNAUTHORIZED_CLIENT("AUT-ERR-002", HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다"),
    EXPIRED_ACCESS_TOKEN("AUT-ERR-003", HttpStatus.UNAUTHORIZED, "만료된 액세스 토큰입니다"),
    EXPIRED_REFRESH_TOKEN("AUT-ERR-004", HttpStatus.UNAUTHORIZED, "만료된 리프레시 토큰입니다"),
    EMPTY_ACCESS_TOKEN("AUT-ERR-005", HttpStatus.BAD_REQUEST, "액세스 토큰이 비어있습니다"),
    BAD_ACCESS_TOKEN("AUT-ERR-006", HttpStatus.BAD_REQUEST, "액세스 토큰이 유효하지 않습니다"),


    NOT_EXIST_USER("GLB-ERR-006", HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
