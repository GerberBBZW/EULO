package com.eulo;

import com.eulo.model.User;
import com.eulo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Gemeinsame Basis für alle Integrationstests.
 * Alle Repository-Beans werden durch Mocks ersetzt – kein laufendes MongoDB nötig.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class TestBase {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected ObjectMapper objectMapper;

    @MockBean protected UserRepository userRepository;
    @MockBean protected SubjectRepository subjectRepository;
    @MockBean protected VocationalGroupRepository vocationalGroupRepository;
    @MockBean protected TutoringOfferRepository offerRepository;
    @MockBean protected SessionRepository sessionRepository;

    protected static final String VALID_EMAIL    = "alex.rivera@school.edu";
    protected static final String VALID_PASSWORD = "password123";

    protected User testUser;

    @BeforeEach
    void setUpBase() {
        when(userRepository.count()).thenReturn(1L); // DataInitializer überspringen

        testUser = new User(
                "u1", "Alex Rivera", VALID_EMAIL,
                passwordEncoder.encode(VALID_PASSWORD),
                "student", "Information Technology",
                null, "IT student passionate about coding.", List.of("Web Development"), 12
        );

        when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(testUser));
        when(userRepository.findById("u1")).thenReturn(Optional.of(testUser));
    }

    /** Loggt testUser ein und gibt den JWT-Token zurück. */
    protected String obtainToken() throws Exception {
        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(VALID_EMAIL, VALID_PASSWORD)))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }
}
