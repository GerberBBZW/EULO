package com.eulo.controller;
 
import com.eulo.model.User;
import com.eulo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
 
    private final UserService userService;
 
    public record LoginRequest(String email, String password) {}
 
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        return userService.authenticate(request.email(), request.password())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }
}
