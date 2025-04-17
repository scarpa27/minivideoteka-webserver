package hr.tvz.tim2.webserver.security;

import hr.tvz.tim2.webserver.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class JwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_TOKEN_PREFIX = "Bearer ";

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
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
        String uri = request.getRequestURI();
        var isAllowed = Arrays.stream(SecurityConfig.UNAUTHENTICATED_ENDPOINTS)
                     .toList()
                     .stream()
                     .anyMatch(endpoint -> uri.contains(endpointWithoutWildcard(endpoint)));
        return isAllowed;
    }

    private String endpointWithoutWildcard(String endpoint) {
        return endpoint.replace("**", "");
    }
}