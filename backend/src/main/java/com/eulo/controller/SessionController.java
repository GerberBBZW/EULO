package com.eulo.controller;

import com.eulo.model.Session;
import com.eulo.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    public List<Session> getSessions(@RequestParam String userId) {
        return sessionService.findByUserId(userId);
    }

    @PostMapping
    public Session createSession(@Valid @RequestBody Session session) {
        return sessionService.save(session);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Session> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return sessionService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
