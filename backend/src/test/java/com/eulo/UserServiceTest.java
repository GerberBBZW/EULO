package com.eulo;

import com.eulo.model.User;
import com.eulo.repository.UserRepository;
import com.eulo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests für UserService — Authentifizierung und Benutzerabfrage.
 */
@DisplayName("UserService Unit Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);

        testUser = new User("u1", "Alex Rivera", "alex@school.edu",
                passwordEncoder.encode("password123"),
                "student", null, null, null, List.of(), 0);
    }

    @Test
    @DisplayName("authenticate → korrektes Passwort liefert User")
    void authenticate_withCorrectPassword_returnsUser() {
        when(userRepository.findByEmail("alex@school.edu")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.authenticate("alex@school.edu", "password123");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("u1");
    }

    @Test
    @DisplayName("authenticate → falsches Passwort liefert leer")
    void authenticate_withWrongPassword_returnsEmpty() {
        when(userRepository.findByEmail("alex@school.edu")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.authenticate("alex@school.edu", "wrongpassword");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("authenticate → unbekannte E-Mail liefert leer")
    void authenticate_withUnknownEmail_returnsEmpty() {
        when(userRepository.findByEmail("nobody@school.edu")).thenReturn(Optional.empty());

        Optional<User> result = userService.authenticate("nobody@school.edu", "password123");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById → bekannte ID liefert User")
    void findById_withKnownId_returnsUser() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById("u1");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alex Rivera");
    }

    @Test
    @DisplayName("findById → unbekannte ID liefert leer")
    void findById_withUnknownId_returnsEmpty() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThat(userService.findById("unknown")).isEmpty();
    }
}
