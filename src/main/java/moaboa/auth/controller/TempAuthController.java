package moaboa.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moaboa.auth.member.dto.MemberRequestDto;
import moaboa.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class TempAuthController {

    private final AuthService authService;

    @PostMapping("/temp-signup")
    public ResponseEntity<Long> tempSignup(@RequestBody MemberRequestDto.CreateDto request, HttpServletResponse response) {
        Long memberId = authService.tempSignup(request, response);
        return ResponseEntity.ok().body(memberId);
    }

    @GetMapping("/token/reissue")
    public ResponseEntity<HttpStatus> tokenReissue(@RequestParam Long memberId, HttpServletResponse response) {
        authService.setAccessToken(memberId, response);
        return ResponseEntity.ok().build();
    }
}
