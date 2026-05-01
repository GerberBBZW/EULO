package com.eulo.controller;

import com.eulo.model.User;
import com.eulo.security.InputSanitizer;
import com.eulo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    /**
     * Mass Assignment + IDOR protection:
     * - Only the authenticated user may update their own profile.
     * - Only whitelisted fields are applied (name, bio, avatarUrl,
     *   vocationalGroup, subjectsTutored).
     * - Sensitive fields (id, email, role, password, sessionsCompleted)
     *   are always preserved from the database record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable String id,
            @RequestBody User incoming,
            Authentication auth) {

        String me = (String) auth.getPrincipal();
        if (!me.equals(id)) {
            return ResponseEntity.status(403).build();  // IDOR guard
        }

        return userService.findById(id).map(existing -> {
            // Whitelist: copy only safe, editable fields
            existing.setName(InputSanitizer.sanitize(incoming.getName()));
            existing.setBio(InputSanitizer.sanitize(incoming.getBio()));
            existing.setAvatarUrl(incoming.getAvatarUrl());
            existing.setVocationalGroup(incoming.getVocationalGroup());
            existing.setSubjectsTutored(incoming.getSubjectsTutored());

            // ALL other fields are preserved from DB (id, email, role, password, sessionsCompleted)
            return ResponseEntity.ok(userService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }
}
