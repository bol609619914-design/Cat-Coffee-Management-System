package com.catcoffee.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/swagger-ui.html"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthUserFactory authUserFactory;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthUserFactory authUserFactory) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authUserFactory = authUserFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.isValid(token, JwtTokenProvider.ACCESS_TOKEN)) {
            Map<String, Object> tokenMeta = jwtTokenProvider.parseTokenMeta(token);
            AuthUser authUser = authUserFactory.fromUserId((Long) tokenMeta.get("uid"));
            Integer tokenVersion = (Integer) tokenMeta.get("tokenVersion");
            if (authUser.getStatus() != null && authUser.getStatus() == 1
                    && authUser.getTokenVersion() != null
                    && authUser.getTokenVersion().equals(tokenVersion)) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    authUser, null, authUser.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.contains(path) || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs/");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
