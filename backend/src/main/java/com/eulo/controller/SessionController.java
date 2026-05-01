package com.eulo.controller;

import com.eulo.model.Session;
import com.eulo.security.InputSanitizer;
import com.eulo.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private static final List<String> VALID_STATUSES =
            List.of("open", "matched", "completed", "cancelled");

    private final SessionService sessionService;

    /**
     * IDOR guard: users can only fetch their own sessions.
     */
    @GetMapping
    public ResponseEntity<List<Session>> getSessions(
            @RequestParam String userId,
            Authentication auth) {
        String me = (String) auth.getPrincipal();
        if (!me.equals(userId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(sessionService.findByUserId(userId));
    }

    /**
     * Force seekerId to the authenticated user (prevents spoofing).
     * Sanitize free-text notes field.
     */
    @PostMapping
    public Session createSession(@Valid @RequestBody Session session, Authentication auth) {
        String me = (String) auth.getPrincipal();
        session.setSeekerId(me);                                     // IDOR: cannot book as someone else
        session.setNotes(InputSanitizer.sanitize(session.getNotes())); // XSS sanitize
        return sessionService.save(session);
    }

    /**
     * IDOR guard: only the seeker or tutor of the session may change its status.
     * Validates status value against whitelist.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Session> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            Authentication auth) {

        String status = body.get("status");
        if (status == null || !VALID_STATUSES.contains(status)) {
            return ResponseEntity.badRequest().build();
        }

        String me = (String) auth.getPrincipal();

        return sessionService.findById(id)
                .map(session -> {
                    // IDOR: only participants may update
                    boolean isSeeker = me.equals(session.getSeekerId());
                    boolean isTutor  = me.equals(session.getTutorId());
                    if (!isSeeker && !isTutor) {
                        return ResponseEntity.status(403).<Session>build();
                    }
                    return sessionService.updateStatus(id, status)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
