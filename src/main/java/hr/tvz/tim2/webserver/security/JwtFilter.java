package hr.tvz.tim2.webserver.security;

import hr.tvz.tim2.webserver.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (!isEndpointAllowingUnauthenticatedAccess(request)) {
            String jwtToken = extractJwtToken(request);

            log.trace("doFilter for endpoint: {} resolved jwt: {}", request.getRequestURI(), jwtToken);

            if (jwtToken != null && !jwtToken.isEmpty()) {
                boolean authenticate = jwtService.authenticate(jwtToken);
                if (!authenticate) {
                    unauthorized(response);
                    return;
                }
            } else {
                log.warn("JWT token is null or empty for endpoint: {}", request.getRequestURI());
                unauthorized(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    private String extractJwtToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(AUTHORIZATION_TOKEN_PREFIX)) {
            return bearerToken.substring(AUTHORIZATION_TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean isEndpointAllowingUnauthenticatedAccess(HttpServletRequest request) {
        log.info("Checking if {} is unauthenticated endpoint", request.getRequestURI());
        String uri = request.getRequestURI();

        var pathMatcher = new AntPathMatcher();
        var isAllowed = Arrays.stream(SecurityConfig.UNAUTHENTICATED_ENDPOINTS)
                .anyMatch(endpoint -> pathMatcher.match(endpoint, uri));
        log.info("It is allowed = {}", isAllowed);
        return isAllowed;
    }
}