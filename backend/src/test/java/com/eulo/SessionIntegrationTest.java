package com.eulo;

import com.eulo.model.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für die Session-API — Erstellen, Abrufen und Statusänderungen.
 */
@DisplayName("Session API")
class SessionIntegrationTest extends TestBase {

    private Session buildSession(String id, String status) {
        return new Session(id, "u1", "Alex Rivera", "u2", "Sarah Chen",
                "s1", "Web Development", status,
                Instant.now().plus(1, ChronoUnit.DAYS).toString(),
                "online", null);
    }

    // -----------------------------------------------------------------------
    // GET /api/sessions
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/sessions")
    class GetSessions {

        @Test
        @DisplayName("Mit Token → eigene Sessions werden zurückgegeben")
        void getSessions_withAuth_returnsUserSessions() throws Exception {
            String token = obtainToken();
            when(sessionRepository.findBySeekerIdOrTutorId("u1", "u1"))
                    .thenReturn(List.of(
                            buildSession("s1", "matched"),
                            buildSession("s2", "completed")
                    ));

            mockMvc.perform(get("/api/sessions").param("userId", "u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value("s1"))
                    .andExpect(jsonPath("$[0].status").value("matched"))
                    .andExpect(jsonPath("$[1].status").value("completed"));
        }

        @Test
        @DisplayName("Kein Token → 401 Unauthorized")
        void getSessions_withoutToken_returns401() throws Exception {
            mockMvc.perform(get("/api/sessions").param("userId", "u1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Keine Sessions vorhanden → leere Liste")
        void getSessions_noSessions_returnsEmptyList() throws Exception {
            String token = obtainToken();
            when(sessionRepository.findBySeekerIdOrTutorId("u1", "u1"))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/sessions").param("userId", "u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/sessions
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/sessions")
    class CreateSession {

        @Test
        @DisplayName("Mit Token → Session wird erstellt (Status 'open')")
        void createSession_withAuth_returnsCreatedSession() throws Exception {
            String token = obtainToken();
            Session saved = buildSession("s_new", "open");
            when(sessionRepository.save(any())).thenReturn(saved);

            String body = """
                    {
                      "seekerId":"u1","seekerName":"Alex Rivera",
                      "tutorId":"u2","tutorName":"Sarah Chen",
                      "subjectId":"s1","subjectName":"Web Development",
                      "status":"open","mode":"online",
                      "date":"%s"
                    }
                    """.formatted(Instant.now().plus(7, ChronoUnit.DAYS));

            mockMvc.perform(post("/api/sessions")
                            .contentType(APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("s_new"))
                    .andExpect(jsonPath("$.status").value("open"))
                    .andExpect(jsonPath("$.subjectName").value("Web Development"));
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void createSession_withoutAuth_returns401() throws Exception {
            mockMvc.perform(post("/api/sessions")
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // -----------------------------------------------------------------------
    // PATCH /api/sessions/{id}/status
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("PATCH /api/sessions/{id}/status")
    class UpdateSessionStatus {

        @Test
        @DisplayName("'open' → 'matched': Tutor akzeptiert Anfrage")
        void updateStatus_openToMatched_returns200() throws Exception {
            String token   = obtainToken();
            Session updated = buildSession("s1", "matched");
            when(sessionRepository.findById("s1")).thenReturn(Optional.of(buildSession("s1", "open")));
            when(sessionRepository.save(any())).thenReturn(updated);

            mockMvc.perform(patch("/api/sessions/s1/status")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"status":"matched"}
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("matched"));
        }

        @Test
        @DisplayName("'matched' → 'completed': Session abgeschlossen")
        void updateStatus_matchedToCompleted_returns200() throws Exception {
            String token   = obtainToken();
            Session updated = buildSession("s1", "completed");
            when(sessionRepository.findById("s1")).thenReturn(Optional.of(buildSession("s1", "matched")));
            when(sessionRepository.save(any())).thenReturn(updated);

            mockMvc.perform(patch("/api/sessions/s1/status")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"status":"completed"}
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("completed"));
        }

        @Test
        @DisplayName("Unbekannte Session-ID → 404 Not Found")
        void updateStatus_unknownId_returns404() throws Exception {
            String token = obtainToken();
            when(sessionRepository.findById("unknown")).thenReturn(Optional.empty());

            mockMvc.perform(patch("/api/sessions/unknown/status")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"status":"matched"}
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void updateStatus_withoutAuth_returns401() throws Exception {
            mockMvc.perform(patch("/api/sessions/s1/status")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"status":"matched"}
                                    """))
                    .andExpect(status().isUnauthorized());
        }
    }
}
