package moaboa.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moaboa.auth.global.error.ErrorCode;
import moaboa.auth.global.error.TokenException;
import moaboa.auth.member.Member;
import moaboa.auth.member.dto.MemberRequestDto;
import moaboa.auth.member.repository.command.MemberCommandRepository;
import moaboa.auth.token.jwt.JwtUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtUtil jwtUtil;
    private final MemberCommandRepository commandRepository;

    private static final String USER_HEADER = "User";

    @Override
    public Long validateToken(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST));
        String memberId = jwtUtil.extractId(accessToken)
                .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST));
        response.addHeader(USER_HEADER, memberId);

        return Long.parseLong(memberId);
    }

    @Override
    public Long validateServerToken(HttpServletRequest request) {
        String accessToken = jwtUtil.extractAccessToken(request)
                .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST));
        String memberId = jwtUtil.extractId(accessToken)
                .orElseThrow(() -> new TokenException(ErrorCode.BAD_REQUEST));
        return Long.parseLong(memberId);
    }

    @Override
    public Long tempSignup(MemberRequestDto.CreateDto request, HttpServletResponse response) {
        Member createdMember = commandRepository.save(Member.from(request));
        setAccessToken(createdMember.getId(), response);
        return createdMember.getId();
    }

    @Override
    public void setAccessToken(Long memberId, HttpServletResponse response) {
        String token = jwtUtil.createAccessToken(memberId);
        jwtUtil.setAccessTokenHeader(response, token);
    }

    @Override
    public void giveTemporaryToken(Long id, HttpServletResponse response) {
        jwtUtil.setAccessTokenHeader(response, jwtUtil.createAccessToken(id));
    }


}
