package moaboa.auth.global.error;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private final ErrorCode errorCode;

    public TokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
