package moaboa.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    Long validateToken(HttpServletRequest request, HttpServletResponse response);

    void giveTemporaryToken(Long id, HttpServletResponse response);
}
