package com.eulo.controller;

import com.eulo.model.User;
import com.eulo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updated) {
        return userService.findById(id).map(existing -> {
            updated.setId(id);
            updated.setPassword(existing.getPassword()); // preserve hashed password
            return ResponseEntity.ok(userService.save(updated));
        }).orElse(ResponseEntity.notFound().build());
    }
}
