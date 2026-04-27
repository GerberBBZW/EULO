package com.eulo.security;

import com.eulo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String clientIp = request.getRemoteAddr();
            String requestUri = request.getRequestURI();

            if (jwtUtil.isValid(token)) {
                String userId = jwtUtil.extractUserId(token);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userRepository.findById(userId).ifPresent(user -> {
                        var auth = new UsernamePasswordAuthenticationToken(
                                user.getId(), null, Collections.emptyList());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
                }
            } else {
                log.warn("AUTH_FAIL: ungültiger Token von IP={} auf Endpunkt={}", clientIp, requestUri);
            }
        }

        chain.doFilter(request, response);
    }
}
