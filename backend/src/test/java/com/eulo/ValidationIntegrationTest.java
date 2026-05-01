package com.eulo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Negativtests für serverseitige Input-Validierung (Security Control 2).
 * Stellt sicher, dass ungültige Inputs mit HTTP 400 abgelehnt werden.
 */
@DisplayName("Input Validation (Security Control 2)")
class ValidationIntegrationTest extends TestBase {

    // -----------------------------------------------------------------------
    // POST /api/auth/register — Pflichtfeldprüfung
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/auth/register — Validierung")
    class RegisterValidation {

        @Test
        @DisplayName("Fehlende E-Mail → 400 Bad Request (NT-03)")
        void register_withoutEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"name":"Test User","password":"secret123"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Ungültige E-Mail-Adresse → 400 Bad Request")
        void register_withInvalidEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","email":"not-an-email","password":"secret123"}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }

        @Test
        @DisplayName("Leeres Passwort → 400 Bad Request")
        void register_withBlankPassword_returns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","email":"test@school.edu","password":""}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/sessions — Pflichtfeldprüfung
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/sessions — Validierung")
    class SessionValidation {

        @Test
        @DisplayName("Fehlende seekerId → 400 Bad Request")
        void createSession_withoutSeekerId_returns400() throws Exception {
            String token = obtainToken();
            mockMvc.perform(post("/api/sessions")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "tutorId":"u2","tutorName":"Sarah",
                                      "subjectId":"s1","subjectName":"Math",
                                      "date":"2026-06-01T10:00:00Z","mode":"online"
                                    }
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Notizen über 500 Zeichen → 400 Bad Request")
        void createSession_withTooLongNotes_returns400() throws Exception {
            String token = obtainToken();
            String longNotes = "x".repeat(501);
            mockMvc.perform(post("/api/sessions")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "seekerId":"u1","seekerName":"Alex",
                                      "tutorId":"u2","tutorName":"Sarah",
                                      "subjectId":"s1","subjectName":"Math",
                                      "date":"2026-06-01T10:00:00Z","mode":"online",
                                      "notes":"%s"
                                    }
                                    """.formatted(longNotes))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }
    }

    // -----------------------------------------------------------------------
    // POST /api/offers — Pflichtfeldprüfung
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/offers — Validierung")
    class OfferValidation {

        @Test
        @DisplayName("Fehlende tutorId → 400 Bad Request")
        void createOffer_withoutTutorId_returns400() throws Exception {
            String token = obtainToken();
            mockMvc.perform(post("/api/offers")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "tutorName":"Alex",
                                      "subjectId":"s1","subjectName":"Math",
                                      "mode":"online"
                                    }
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Beschreibung über 500 Zeichen → 400 Bad Request")
        void createOffer_withTooLongDescription_returns400() throws Exception {
            String token = obtainToken();
            String longDesc = "y".repeat(501);
            mockMvc.perform(post("/api/offers")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {
                                      "tutorId":"u1","tutorName":"Alex",
                                      "subjectId":"s1","subjectName":"Math",
                                      "mode":"online","description":"%s"
                                    }
                                    """.formatted(longDesc))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }
    }
}
