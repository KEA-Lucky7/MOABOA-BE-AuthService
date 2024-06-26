package moaboa.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import moaboa.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/token/validation")
    public ResponseEntity<Long> validateToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.validateToken(request, response));
    }

    @PostMapping("/token/server-validation")
    public ResponseEntity<Long> validateServerToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.validateServerToken(request));
    }

    @GetMapping("/token")
    public ResponseEntity<HttpStatus> giveTemporaryToken(@RequestParam Long id, HttpServletResponse response) {
        authService.giveTemporaryToken(id, response);
        return ResponseEntity.ok().build();
    }
}
