//package moaboa.auth.jwt;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import moaboa.auth.response.ErrorCode;
//import moaboa.auth.response.ErrorResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//
//import java.io.IOException;
//
//public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
////        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.UNAUTHORIZED_CLIENT);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonErrorResponse = objectMapper.writeValueAsString("에러~");
//
//        response.setStatus(HttpStatus.BAD_REQUEST.value());
//        response.setCharacterEncoding("utf-8");
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // application/json
//        response.getWriter().write(jsonErrorResponse);
//    }
//
//}