package moaboa.auth.token.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/token")
@RequiredArgsConstructor
public class TokenController {

    @PostMapping("/validation")
    public ResponseEntity<HttpStatus> validateToken() {
        return ResponseEntity.ok().build();
    }
}
