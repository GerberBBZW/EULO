package com.eulo;

import com.eulo.model.User;
import com.eulo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integrationstest für den Auth-Flow (Happy Path + Negativtests).
 *
 * Testet die vollständige HTTP-Schicht inkl. Spring Security,
 * JWT-Generierung und Jackson-Serialisierung – ohne echte MongoDB-Verbindung
 * (alle Repositories werden durch @MockBean ersetzt; der MongoDB-Driver
 * baut die Verbindung erst beim ersten tatsächlichen Aufruf auf).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private PasswordEncoder passwordEncoder;

    // Repositories als Mocks – kein laufendes MongoDB nötig
    @MockBean private UserRepository userRepository;
    @MockBean private SubjectRepository subjectRepository;
    @MockBean private VocationalGroupRepository vocationalGroupRepository;
    @MockBean private TutoringOfferRepository offerRepository;
    @MockBean private SessionRepository sessionRepository;

    private static final String VALID_EMAIL    = "alex.rivera@school.edu";
    private static final String VALID_PASSWORD = "password123";

    private User testUser;

    @BeforeEach
    void setUp() {
        // Seed-Guard: DataInitializer überspringt, wenn count > 0
        when(userRepository.count()).thenReturn(1L);

        testUser = new User(
                "u1", "Alex Rivera", VALID_EMAIL,
                passwordEncoder.encode(VALID_PASSWORD),
                "student", "Information Technology",
                null, "Test bio", List.of("Web Development"), 5
        );

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
        when(userRepository.findById("u1")).thenReturn(Optional.of(testUser));
    }

    // -----------------------------------------------------------------------
    // Happy Path
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("POST /api/auth/login mit gültigen Credentials → 200 + JWT-Token")
        void login_withValidCredentials_returns200AndToken() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"%s"}
                                    """.formatted(VALID_EMAIL, VALID_PASSWORD)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.user.id").value("u1"))
                    .andExpect(jsonPath("$.user.email").value(VALID_EMAIL))
                    .andExpect(jsonPath("$.user.name").value("Alex Rivera"))
                    // Sicherheits-Check: Passwort-Hash darf niemals zurückgegeben werden
                    .andExpect(jsonPath("$.user.password").doesNotExist());
        }

        @Test
        @DisplayName("GET /api/subjects ist ohne Token öffentlich erreichbar")
        void getSubjects_withoutToken_returns200() throws Exception {
            when(subjectRepository.findAll()).thenReturn(List.of());
            mockMvc.perform(get("/api/subjects"))
                    .andExpect(status().isOk());
        }
    }

    // -----------------------------------------------------------------------
    // Register
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Neue Registrierung → 201 Created + JWT-Token (SM-04)")
        void register_withValidData_returns201AndToken() throws Exception {
            String newEmail = "new.user@school.edu";
            User newUser = new User("u99", "New User", newEmail,
                    passwordEncoder.encode("secret123"),
                    "student", null, null, null, List.of(), 0);
            when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
            when(userRepository.save(any())).thenReturn(newUser);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"New User","email":"%s","password":"secret123"}
                                    """.formatted(newEmail)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.user.email").value(newEmail))
                    .andExpect(jsonPath("$.user.password").doesNotExist());
        }

        @Test
        @DisplayName("Registrierung ohne E-Mail → 400 Bad Request (NT-03)")
        void register_withoutEmail_returns400() throws Exception {
            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Test","password":"secret123"}
                                    """))
                    .andExpect(status().isBadRequest());
        }
    }

    // -----------------------------------------------------------------------
    // Negativtests
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Negativtests")
    class NegativeTests {

        @Test
        @DisplayName("POST /api/auth/login mit falschem Passwort → 401 Unauthorized")
        void login_withWrongPassword_returns401() throws Exception {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"%s","password":"falschesPasswort"}
                                    """.formatted(VALID_EMAIL)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("POST /api/auth/login mit unbekannter E-Mail → 401 Unauthorized")
        void login_withUnknownEmail_returns401() throws Exception {
            when(userRepository.findByEmail("unbekannt@school.edu")).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"email":"unbekannt@school.edu","password":"password123"}
                                    """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/sessions ohne Token → 401 Unauthorized (geschützter Endpoint)")
        void getSessions_withoutToken_returns401() throws Exception {
            mockMvc.perform(get("/api/sessions").param("userId", "u1"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/sessions mit ungültigem Token → 401 Unauthorized")
        void getSessions_withInvalidToken_returns401() throws Exception {
            mockMvc.perform(get("/api/sessions")
                            .param("userId", "u1")
                            .header("Authorization", "Bearer ungültig.token.wert"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
