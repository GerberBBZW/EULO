package com.eulo.controller;

import com.eulo.model.AuthResponse;
import com.eulo.model.User;
import com.eulo.repository.UserRepository;
import com.eulo.security.JwtUtil;
import com.eulo.service.UserService;
import com.eulo.security.InputSanitizer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {}

    public record RegisterRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 200) String email,
            // min 8 chars, at least 1 letter and 1 digit
            @NotBlank
            @Size(min = 8, max = 128, message = "Password must be 8–128 characters")
            @Pattern(regexp = ".*[A-Za-z].*", message = "Password must contain at least one letter")
            @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one digit")
            String password,
            String role) {}

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return userService.authenticate(request.email(), request.password())
                .map(user -> {
                    log.info("AUTH_OK: Benutzer={} hat sich eingeloggt", user.getEmail());
                    String token = jwtUtil.generateToken(user.getId());
                    return ResponseEntity.ok(new AuthResponse(token, user));
                })
                .orElseGet(() -> {
                    log.warn("AUTH_FAIL: Fehlgeschlagener Login für Email={}", request.email());
                    return ResponseEntity.status(401).build();
                });
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(409).build();
        }
        // Sanitize free-text input, force role to student/teacher only
        String safeName = InputSanitizer.sanitize(request.name());
        String safeRole = "teacher".equalsIgnoreCase(request.role()) ? "teacher" : "student";

        User user = new User(null, safeName, request.email(),
                passwordEncoder.encode(request.password()),
                safeRole,
                null, null, null, null, 0);
        User saved = userRepository.save(user);
        log.info("AUTH_OK: Neuer Benutzer registriert={}", saved.getEmail());
        String token = jwtUtil.generateToken(saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, saved));
    }
}
