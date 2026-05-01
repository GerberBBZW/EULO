package com.eulo.security;

import com.eulo.model.ErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IP-basiertes Rate Limiting.
 *
 * Login-Endpoint:  max. 5 Versuche / 60 s → 15 Minuten Sperre
 * Alle Endpoints:  max. 120 Requests / 60 s
 */
@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // --- Konfiguration ---
    private static final int  LOGIN_MAX_ATTEMPTS  = 5;
    private static final long LOGIN_WINDOW_MS      = 60_000;
    private static final long LOGIN_BLOCK_MS        = 15 * 60_000;

    private static final int  GLOBAL_MAX_REQUESTS  = 120;
    private static final long GLOBAL_WINDOW_MS      = 60_000;

    // --- State ---
    private record Window(AtomicInteger count, long windowStart) {}

    private final ConcurrentHashMap<String, Window>  globalWindows = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Window>  loginWindows  = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long>    loginBlocked  = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;

    public RateLimitFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = getClientIp(req);
        boolean isLoginEndpoint = req.getRequestURI().equals("/api/auth/login")
                && "POST".equalsIgnoreCase(req.getMethod());

        // --- Login-spezifische Sperre prüfen ---
        if (isLoginEndpoint) {
            Long blockedUntil = loginBlocked.get(ip);
            if (blockedUntil != null && Instant.now().toEpochMilli() < blockedUntil) {
                long remaining = (blockedUntil - Instant.now().toEpochMilli()) / 1000;
                log.warn("RATE_LIMIT: Login gesperrt IP={} noch {}s", ip, remaining);
                sendTooManyRequests(res, "Too many login attempts. Try again in " + remaining + " seconds.");
                return;
            }

            if (isRateLimited(loginWindows, ip, LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW_MS)) {
                loginBlocked.put(ip, Instant.now().toEpochMilli() + LOGIN_BLOCK_MS);
                log.warn("RATE_LIMIT: Login IP={} gesperrt für 15 Minuten", ip);
                sendTooManyRequests(res, "Too many login attempts. Blocked for 15 minutes.");
                return;
            }
        }

        // --- Globales Rate Limit ---
        if (isRateLimited(globalWindows, ip, GLOBAL_MAX_REQUESTS, GLOBAL_WINDOW_MS)) {
            log.warn("RATE_LIMIT: Global IP={} überschritten", ip);
            sendTooManyRequests(res, "Too many requests. Please slow down.");
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isRateLimited(ConcurrentHashMap<String, Window> windows,
                                   String ip, int maxRequests, long windowMs) {
        long now = Instant.now().toEpochMilli();
        Window w = windows.compute(ip, (k, existing) -> {
            if (existing == null || now - existing.windowStart() > windowMs) {
                return new Window(new AtomicInteger(1), now);
            }
            existing.count().incrementAndGet();
            return existing;
        });
        return w.count().get() > maxRequests;
    }

    private void sendTooManyRequests(HttpServletResponse res, String message) throws IOException {
        res.setStatus(429);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setHeader("Retry-After", "60");
        objectMapper.writeValue(res.getWriter(), new ErrorDto("RATE_LIMIT_EXCEEDED", message));
    }

    private String getClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
