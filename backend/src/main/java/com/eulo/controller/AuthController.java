package com.eulo.controller;

import com.eulo.model.AuthResponse;
import com.eulo.security.JwtUtil;
import com.eulo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public record LoginRequest(String email, String password) {}

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.authenticate(request.email(), request.password())
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getId());
                    return ResponseEntity.ok(new AuthResponse(token, user));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
