package moaboa.auth.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import moaboa.auth.global.error.filter.ExceptionFilter;
import moaboa.auth.token.jwt.JwtAuthenticationEntryPoint;
import moaboa.auth.token.jwt.JwtAuthenticationProcessingFilter;
import moaboa.auth.token.jwt.JwtUtil;
import moaboa.auth.oauth2.CustomOAuth2UserService;
import moaboa.auth.oauth2.handler.OAuth2LoginFailureHandler;
import moaboa.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import moaboa.auth.token.refresh.RefreshTokenRepository;
import moaboa.auth.global.error.ErrorCode;
import moaboa.auth.global.response.ErrorResponse;
import moaboa.auth.member.Role;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                // h2 enable
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                new AntPathRequestMatcher("/auth/token/validation"),
                                new AntPathRequestMatcher("/auth/token"),
                                new AntPathRequestMatcher("/auth/health"),
                                new AntPathRequestMatcher("/auth/prometheus"),
                                new AntPathRequestMatcher("/auth/metrics"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/index.html"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/login/**"),
                                new AntPathRequestMatcher("/oauth2/**"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/html/**"),
                                new AntPathRequestMatcher("/favicon.ico")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/members/createUser")
                        ).hasRole(Role.GUEST.name())
                        .requestMatchers(
                                new AntPathRequestMatcher("/auth/**")
                        ).hasRole(Role.MEMBER.name())
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(new JwtAuthenticationProcessingFilter(jwtUtil, jwtUtil.getMemberCommandRepository(), refreshTokenRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(exceptionFilter(), JwtAuthenticationProcessingFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
//                .requestMatchers(PathRequest.toH2Console())
        );
    }

    // 서버에 요청을 할 때 액세스가 가능한지 권한을 체크후 액세스 할 수 없는 요청을 했을시 동작
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {

            ErrorCode customException = ErrorCode.UNAUTHORIZED_CLIENT;
            ErrorResponse errorResponse = new ErrorResponse(customException.getCode(), customException.getMessage());

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", "FAILED");
            responseBody.put("data", errorResponse);

            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
        };
    }

    @Bean
    public ExceptionFilter exceptionFilter() {
        return new ExceptionFilter();
    }


}
