package com.eulo;

import com.eulo.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstests für die User-API — Profil abrufen und aktualisieren.
 */
@DisplayName("User API")
class UserIntegrationTest extends TestBase {

    // -----------------------------------------------------------------------
    // GET /api/users/{id}
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUser {

        @Test
        @DisplayName("Mit Token → Benutzerprofil ohne Passwort-Hash")
        void getUser_withAuth_returnsUserWithoutPassword() throws Exception {
            String token = obtainToken();

            mockMvc.perform(get("/api/users/u1")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("u1"))
                    .andExpect(jsonPath("$.name").value("Alex Rivera"))
                    .andExpect(jsonPath("$.email").value(VALID_EMAIL))
                    .andExpect(jsonPath("$.role").value("student"))
                    // Sicherheitscheck: BCrypt-Hash darf nicht zurückgegeben werden
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("Unbekannte ID → 404 Not Found")
        void getUser_unknownId_returns404() throws Exception {
            String token = obtainToken();
            when(userRepository.findById("nobody")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/users/nobody")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void getUser_withoutAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/users/u1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // -----------------------------------------------------------------------
    // PUT /api/users/{id}
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("Profil-Update → aktualisierter Benutzer wird zurückgegeben")
        void updateUser_withAuth_returnsUpdatedUser() throws Exception {
            String token = obtainToken();
            User updated = new User("u1", "Alex Rivera Updated", VALID_EMAIL,
                    null, "student", "Information Technology",
                    null, "Neue Bio.", List.of("Web Development", "Mathematics"), 15);
            when(userRepository.save(any())).thenReturn(updated);

            String body = """
                    {
                      "id":"u1",
                      "name":"Alex Rivera Updated",
                      "email":"%s",
                      "role":"student",
                      "bio":"Neue Bio.",
                      "vocationalGroup":"Information Technology",
                      "subjectsTutored":["Web Development","Mathematics"],
                      "sessionsCompleted":15
                    }
                    """.formatted(VALID_EMAIL);

            mockMvc.perform(put("/api/users/u1")
                            .contentType(APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Alex Rivera Updated"))
                    .andExpect(jsonPath("$.bio").value("Neue Bio."))
                    .andExpect(jsonPath("$.sessionsCompleted").value(15))
                    // Sicherheitscheck: kein Passwort in der Antwort
                    .andExpect(jsonPath("$.password").doesNotExist());
        }

        @Test
        @DisplayName("Update für unbekannte ID → 404 Not Found")
        void updateUser_unknownId_returns404() throws Exception {
            String token = obtainToken();
            when(userRepository.findById("nobody")).thenReturn(Optional.empty());

            mockMvc.perform(put("/api/users/nobody")
                            .contentType(APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","email":"test@school.edu","role":"student"}
                                    """)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Ohne Token → 401 Unauthorized")
        void updateUser_withoutAuth_returns401() throws Exception {
            mockMvc.perform(put("/api/users/u1")
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
