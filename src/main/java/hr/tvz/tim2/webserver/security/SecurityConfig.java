package hr.tvz.tim2.webserver.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    static final String[] UNAUTHENTICATED_ENDPOINTS = new String[]{
            "/common",
            "/common/**",
            "/auth",
            "/auth/**",
            "/h2",
            "/h2/**",
            "/h2-console",
            "/h2-console/**",
            "/swagger-ui",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/api-docs",
            "/api-docs/**",
            "/movies",
            "/movies/**",
            "/stock",
            "/stock/**"
    };
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests((auth) -> {
            auth.requestMatchers(UNAUTHENTICATED_ENDPOINTS).permitAll();
            auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
            auth.anyRequest().authenticated();
        }).httpBasic(withDefaults());


        http.sessionManagement(x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(x -> x
                .authenticationEntryPoint((request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API exception:\n" + exception.getMessage());
        }));

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.getOrBuild();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(UNAUTHENTICATED_ENDPOINTS);
    }
}