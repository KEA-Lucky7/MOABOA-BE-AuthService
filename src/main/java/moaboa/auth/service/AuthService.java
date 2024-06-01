package moaboa.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import moaboa.auth.member.dto.MemberRequestDto;

public interface AuthService {

    Long validateToken(HttpServletRequest request, HttpServletResponse response);

    void giveTemporaryToken(Long id, HttpServletResponse response);

    Long validateServerToken(HttpServletRequest request);

    Long tempSignup(MemberRequestDto.CreateDto request, HttpServletResponse response);

    void tokenReissue(Long memberId, HttpServletResponse response);
}
